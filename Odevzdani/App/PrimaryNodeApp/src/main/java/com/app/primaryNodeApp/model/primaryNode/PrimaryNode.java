/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode;

import com.app.primaryNodeApp.model.primaryNode.secNodeManager.SecNodeManager;
import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.dao.StepRunDataDao;
import com.app.commons.eventLogger.EventLogger;
import com.app.primaryNodeApp.model.database.dao.ErrorDao;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import com.app.primaryNodeApp.model.database.util.DatabaseInitializer;
import com.app.primaryNodeApp.model.database.util.HibernateUtil;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Singleton bean for execution, monitoring and management of parallel
 * processing on all Secondary nodes.
 *
 * @author Filip
 */
@Singleton
@Startup
public class PrimaryNode {
   
    /** STATIC PROPERTIES **/

    private static final Logger LOGGER = EventLogger.getInstance().getLogger(PrimaryNode.class.getName());
    
    /** OBJECT PROPERTIES **/
    
    @Inject
    private SecNodeManager secNodeManager;
    
    private final RunJobDao runJobDao;
    private final JobDao jobDao;
    private final StepRunDataDao stepRunDataDao;
    private final NodeDao nodeDao;
    private final ErrorDao errorDao;
    
    @Resource(name="PATH_TO_ERR_LOG")
    private String PATH_TO_ERR_LOG;

    /**
     * Constructor
     */
    public PrimaryNode() {
        runJobDao = new RunJobDao();
        jobDao = new JobDao();
        stepRunDataDao = new StepRunDataDao();
        nodeDao = new NodeDao();
        errorDao = new ErrorDao();
    }

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    public void initialize() {
        EventLogger.getInstance().addFileHandler(PATH_TO_ERR_LOG);
        
        DatabaseInitializer.saveInitData();
        new NodeDao().initializeNodesStatus();                  // all nodes is disconnected when the Primare node is started
        new StepRunDataDao().initializeStepRunDataStatus();     // all RUNNING job steps are WAITING(for RUNNING) OR in ERROR (for ERROR/RUNNING) 
    }
    
    /**
     * Method which is called before the bean is unset from scope.
     */
    @PreDestroy
    public void preDestroy() {
        try {
            LOGGER.log(Level.INFO, "Close file handler for saving information to log file.");
            EventLogger.getInstance().closeFileHandler();                       // close file handler for writing errors
            secNodeManager.closeListener(); // close ConnectionListener()
        } catch (IOException e) {
        }
        secNodeManager.stopExecutingOnAllNodes();

        HibernateUtil.shutdown();                                               // close database connection
    }

    /**
     * Method, that sets flag about new feasible step on all secondary nodes of task.
     * @param jobID The ID of job, which secondary nodes to be notified.
     */
    public void startJob(Long jobID) {
        // task is saved and has status "WAITING" (no errors) - new task or stopped task
        // all threads, which has status "FREE" must be notified -> must check, if some task could to run.
        
        Job job = jobDao.getByIdWithCollections(jobID);
        
        if (job != null) {           

            for (Node node : job.getJobNodes()) {
                if (node.getNodeStatus() == NodeStatusEnum.FREE) {
                    secNodeManager.setNewFeasibleStep(node.getNodeIP());
                }
            }
        }
    }

    /**
     * This method is for optimalization. When this method is called multiple times, the runJob and node are obtained from database only once.
     * @param runJob The runJob to be stopped.
     * @param node The node on which the executing to be stooped.
     */
    @Lock(READ)
    private void stopJobOnSecNodeExec(RunJob runJob, Node node) {
        long start = System.currentTimeMillis();
        StepRunDataDao stepRunDataDaoLocal = new StepRunDataDao();
        List<StepRunData> stepsRunData = stepRunDataDaoLocal.getNodeAllRunData(runJob.getId(), node.getId());
        stepsRunData.sort((StepRunData stepRunData1, StepRunData stepRunData2) -> stepRunData1.getStep().getStepOrder() - stepRunData2.getStep().getStepOrder());
        
        for (StepRunData stepRunData : stepsRunData) {
            switch (stepRunData.getStepStatus()) {
                case PAUSED:
                case ERROR:
                    return;
                case WAITING:
                    stepRunData.setRerunButtonPressed(false);
                    stepRunData.setRunButtonPressed(false);
                    stepRunData.setStepStatus(StepStatusEnum.PAUSED);
                    stepRunDataDaoLocal.update(stepRunData);            
                    return;

                case RUNNING:
                case RUNNING_ERROR:
                    secNodeManager.stopExecutingOnNode(node.getNodeIP(), start);
                    return;
                case FINISHED:
                default:
                    break;
            }
        }
    }

    /**
     * Method, that stops executing of task on specific secondary node.
     * @param runJobID The id of runjob to be stopped.
     * @param nodeID The id of node on which the executing to be stopped.
     */
    public void stopJobOnSecNode(Long runJobID, Long nodeID) {
        // check if the Node is occupied and  if it is true, send stop message to Sec. Node - terminate job
        // check if it the job running on specific node

        Node node = nodeDao.getById(nodeID);
        RunJob runJob = runJobDao.getById(runJobID);

        if (node != null && runJob != null) {
            this.stopJobOnSecNodeExec(runJob, node);
        }
    }

    /**
     * Method, that stop executing task on all secondary nodes.
     * @param runJobID The id of runjob to be stopped.
     */
    public void stopJobOnAllNodes(Long runJobID) {
        // get all sec. nodes, on which the job running or running with errors and send them the message - terminate job.

        RunJob runJob = runJobDao.getById(runJobID);
        Job job = jobDao.getByIdWithCollections(runJob.getJob().getId());

        List<Thread> threads = new ArrayList<>();
        
        for (Node node : job.getJobNodes()) {
            Thread th = new Thread(() -> {
               this.stopJobOnSecNodeExec(runJob, node);
           });
           threads.add(th);
           th.start();
        }
        
        
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO
            }
        }
    }

    /**
     * Method, that runs stopped task on specific secondary node.
     * @param runJobID The id of runjob to be ran.
     * @param nodeID The id of node on which the executing to be ran.
     */
    @Lock(READ)
    public void runStoppedTaskOnSecNode(Long runJobID, Long nodeID) {
        RunJob runJob = new RunJobDao().getById(runJobID);
        Node node = new NodeDao().getById(nodeID);
        StepRunDataDao stepRunDataDaoLocal = new StepRunDataDao();

        if (runJob != null && node != null) {
            
            // find stopped step of task
            Predicate<StepRunData> isIoErrorOccurs = stepRunData -> !stepRunData.getErrors().isEmpty() && stepRunData.getErrors().stream().allMatch(error -> error.getPathToInputFile() == null);
            Predicate<StepRunData> isRunnableStepRunData = stepRunData -> isIoErrorOccurs.test(stepRunData) || 
                    stepRunData.getTotalFsNodes() - stepRunData.getProcessedFsNodes() != stepRunData.getErrors().size() ||
                    stepRunData.getTotalFsNodes() == 0;
            Predicate<StepRunData> byStatus = stepRunData -> (stepRunData.getStepStatus() == StepStatusEnum.PAUSED || stepRunData.getStepStatus() == StepStatusEnum.ERROR);
            
            
            List<StepRunData> stepsRunData = stepRunDataDaoLocal.getNodeAllRunData(runJob.getId(), node.getId())
                    .stream()
                    .filter(byStatus)
                    .map(stepRunData -> stepRunDataDaoLocal.getByIdWithCollections(stepRunData.getId()))
                    .filter(isRunnableStepRunData)
                    .collect(Collectors.toList());
                        
            if (stepsRunData.size() > 1) {
                LOGGER.log(Level.SEVERE, "Multiple {0} or {1} StepRunData be found for runJob with ID: {2} and node with ID: {3}.", 
                        new Object[]{StepStatusEnum.PAUSED.toString(), StepStatusEnum.ERROR.toString(), runJobID, nodeID});
            } else if (stepsRunData.size() == 1) {
                StepRunData stepRunData = stepsRunData.get(0);
                stepRunData.setStepStatus(StepStatusEnum.WAITING);
                stepRunData.setRunButtonPressed(true);
                if (isIoErrorOccurs.test(stepRunData)) { // if IO error occurs
                    stepRunData.setRerunButtonPressed(true);
                }
                stepRunDataDaoLocal.update(stepRunData);

                if (node.getNodeStatus() == NodeStatusEnum.FREE) {
                    secNodeManager.setNewFeasibleStep(node.getNodeIP());
                }
            }
        }
    }

    /**
     * Method, that runs stopped task on all secondary nodes of task.
     * @param runJobID The id of runjob to be ran.
     */
    public void runStoppedTaskOnAllNodes(Long runJobID) {
        RunJob runJob = runJobDao.getById(runJobID);
        
        List<Thread> threads = new ArrayList<>();
        
        if (runJob != null) {
            Job job = jobDao.getByIdWithCollections(runJob.getJob().getId());
            job.getJobNodes().forEach(node -> {
                    Thread th = new Thread(() -> {
                    this.runStoppedTaskOnSecNode(runJobID, node.getId());
               });
               threads.add(th);
               th.start();                
            });
        }
        
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // TODO
            }
        }
    }

    /**
     * Method, that reruns error inputs of task on specific secondary node.
     * @param runJobID The id of runjob to be reran.
     * @param nodeID The id of node on which the executing to be reran.
     */
    public void rerunJobOnSecNode(Long runJobID, Long nodeID) {

        RunJob runJob = runJobDao.getById(runJobID);
        Node node = nodeDao.getById(nodeID);

        if (runJob != null && node != null) {

            // repeate error inputs of last step of task on specified node (has status error or running/error)
            List<StepRunData> stepsRunData = stepRunDataDao.getNodeAllRunData(runJob.getId(), node.getId()).stream().filter(stepRunData -> 
                !stepRunDataDao.getErrorInputs(stepRunData.getId()).isEmpty()
            ).collect(Collectors.toList());
            if (stepsRunData.size() > 1) {
                LOGGER.log(Level.SEVERE, "Multiple StepRunData with error inputs be found for runJob with ID: {0} and node with ID: {1}.",
                        new Object[]{runJobID, nodeID});
            } else if (stepsRunData.size() == 1) {
                StepRunData stepRunData = stepRunDataDao.getByIdWithCollections(stepsRunData.get(0).getId());

                if (stepRunData.getStepStatus() == StepStatusEnum.RUNNING_ERROR) {
                    // send all error inputs to sec node
                    secNodeManager.sendRerunMessage(stepRunData);
                } else {
                    stepRunData.setStepStatus(StepStatusEnum.WAITING);
                    stepRunData.setRerunButtonPressed(true);
                    stepRunDataDao.update(stepRunData);

                    if (node.getNodeStatus() == NodeStatusEnum.FREE) {
                        secNodeManager.setNewFeasibleStep(stepRunData.getNode().getNodeIP());
                    }
                } 
            }
        }
    }
    
    /**
     * Method, that skips processing of error inputs on specific secondary node.
     * @param runJobID The id of runjob of which the processing to be skipped.
     * @param nodeID The id of node on which the processing of error inputs to be skipped.
     */
    public void skipErrInputsOnNode(Long runJobID, Long nodeID) {
        RunJob runJob = runJobDao.getById(runJobID);
        Node node = nodeDao.getById(nodeID);

        if (runJob != null && node != null) {

            // repeate error inputs of last step of task on specified node (has status error or running/error)
            List<StepRunData> stepsRunData = stepRunDataDao.getNodeAllRunData(runJob.getId(), node.getId()).stream().filter(stepRunData -> 
                !stepRunDataDao.getErrorInputs(stepRunData.getId()).isEmpty()
            ).collect(Collectors.toList());
            if (stepsRunData.size() > 1) {
                LOGGER.log(Level.SEVERE, "Multiple StepRunData with error inputs be found for runJob with ID: {0} and node with ID: {1}.",
                        new Object[]{runJobID, nodeID});
            } else if (stepsRunData.size() == 1) {
                StepRunData stepRunData = stepRunDataDao.getByIdWithCollections(stepsRunData.get(0).getId());

                stepRunData.setStepStatus(StepStatusEnum.FINISHED);                
                stepRunData.setFinishedAt(new Timestamp(System.currentTimeMillis()));
                stepRunData.setLastFinishedAt(null);
                stepRunDataDao.update(stepRunData);
                
                stepRunDataDao.setLastFinishedAtNextStep(stepRunData);

                stepRunData.getErrors().forEach(error -> errorDao.delete(error.getId()));
                
                if (node.getNodeStatus() == NodeStatusEnum.FREE) {
                    secNodeManager.setNewFeasibleStep(stepRunData.getNode().getNodeIP());
                }
            }
        }
    }
}
