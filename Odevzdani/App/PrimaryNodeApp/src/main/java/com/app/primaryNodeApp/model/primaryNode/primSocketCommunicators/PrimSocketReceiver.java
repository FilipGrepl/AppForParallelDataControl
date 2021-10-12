/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.primSocketCommunicators;

import com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer.PrimConnStatusMaintainer;
import com.app.primaryNodeApp.primLoggers.PrimSocketEventLogger;
import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.dao.StepRunDataDao;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.IOException;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketCommunicators.SocketReceiver;
import com.app.commons.socketMessages.AcceptConnMsg;
import com.app.commons.socketMessages.DeclineConnMsg;
import com.app.commons.socketMessages.ProcErrorMsg;
import com.app.commons.socketMessages.ProcFinishedMsg;
import com.app.commons.socketMessages.NodeNameMsg;
import com.app.commons.socketMessages.RerunAckMsg;
import com.app.commons.socketMessages.RunStepAckMsg;
import com.app.commons.socketMessages.StopStepAckMsg;
import com.app.commons.socketMessages.UsageMsg;
import com.app.commons.socketMessages.sendingItems.DeclineValues;
import com.app.commons.socketMessages.sendingItems.FinishValues;
import com.app.commons.socketMessages.sendingItems.FinishValues.FinishType;
import com.app.primaryNodeApp.model.database.dao.ErrorDao;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer.PrimConnStatusMaintainer.PrimConnectionStatus;
import com.app.primaryNodeApp.model.primaryNode.secNodeManager.SecNodeManager;
import com.app.primaryNodeApp.model.database.entity.Error;
import static com.app.primaryNodeApp.model.database.entity.Step.BYTES_IN_MB;
import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Predicate;
import javax.net.ssl.SSLSocket;

/**
 * Class for receiving and processing messages from specific Secondary node.
 *
 * @author Filip
 */
public class PrimSocketReceiver extends SocketReceiver<PrimSocketEventLogger> {

    /**
     * OBJECT PROPERTY *
     */
    private final NodeDao nodeDao = new NodeDao();
    private final StepRunDataDao stepRunDataDao = new StepRunDataDao();
    private final ErrorDao errorDao = new ErrorDao();
    private final PrimConnStatusMaintainer primStatusMaintainer;
    private final PrimSocketSender primSocketSender;
    private final SecNodeManager secNodeManager;

    /** OBJECT METHODS **/
    /**
     * Constructor.
     * @param socket Socket, where the connection with Secondary node is established.
     * @param primSocketSender Object for sending messages to Secondary node.
     * @param primConnStatusMaintainer Object, in which the state of connection is maintained
     * @param secNodeManager Object for communication with all Secondary nodes.
     * @throws IOException If an I/O exception occurs.
     */
    public PrimSocketReceiver(
            SSLSocket socket,
            PrimSocketSender primSocketSender,
            PrimConnStatusMaintainer primConnStatusMaintainer,
            SecNodeManager secNodeManager) throws IOException {
        super(socket.getInputStream(), new PrimSocketEventLogger(PrimSocketReceiver.class, socket.getInetAddress().getHostAddress(), socket.getPort()));
        this.primSocketSender = primSocketSender;
        this.primStatusMaintainer = primConnStatusMaintainer;
        this.secNodeManager = secNodeManager;
    }

    /**
     * Method, that processes received message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    @Override
    protected void processMessage(GenericSocketMsg message) throws IOException {
        switch (message.getMessageType()) {
            case NODE_NAME:
                procNodeNameMsg((NodeNameMsg) message);
                break;
            case USAGE_OF_RESOURCES:
                procUsageMsg((UsageMsg) message);
                break;
            case RUN_JOB_STEP_ACK:
                procRunStepAckMsg((RunStepAckMsg) message);
                break;
            case STOP_JOB_STEP_ACK:
                procStopStepAckMsg((StopStepAckMsg) message);
                break;
            case PROC_ERROR:
                procFsNodeErrorMsg((ProcErrorMsg) message);
                break;
            case PROC_FINISHED:
                procFsNodeFinishedMsg((ProcFinishedMsg) message);
                break;
            case RERUN_JOB_STEP_ACK:
                procRerunAckMsg((RerunAckMsg) message);
                break;
            case IS_ALIVE:
                break;
            default:
                logger.logUnknownMsgTypeErr(message);
                break;
        }
    }

    /**
     * Processing of NodeName message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    private void procNodeNameMsg(NodeNameMsg message) throws IOException {
        if (primStatusMaintainer.getSecNode() == null) {                                        // Check if communication is already established   
            primSocketSender.setLogNodeName(message.getMessage());
            logger.setNodeName(message.getMessage());
            logger.logReceivedMessage(message.getMessageType(), message.getMessage());
            Node secNode = this.nodeDao.getByName(message.getMessage());
            String declineMsgContent = null;
            
            if (secNode == null) {                                                                                              // Node with specific name is conected first time
                Node nodeByIP = this.nodeDao.getByIP(primSocketSender.getRemoteIPAddress());
                if (nodeByIP != null) {                                                                                         // This IP is saved in DB at node with different name                    
                    logger.logDifferentNameErr(nodeByIP.getNodeName(), nodeByIP.getNodeIP());
                    declineMsgContent = DeclineValues.getDifferentNameErr(nodeByIP.getNodeName());
                } else {
                    secNode = new Node(message.getMessage(), NodeStatusEnum.FREE, primSocketSender.getRemoteIPAddress());
                    this.nodeDao.save(secNode);
                }
            } else if (secNode.getNodeStatus() != NodeStatusEnum.DISCONNECTED) {                                                // Node with this name is already connected
                logger.logNodeDuplicateErr();
                declineMsgContent = DeclineValues.getAlreadyConnectedErr();
            } else if (secNode.getNodeIP() != null && !secNode.getNodeIP().equals(primSocketSender.getRemoteIPAddress())) {     // Node is connected from different IP
                logger.logDifferentIPErr(secNode.getNodeName(), secNode.getNodeIP());
                declineMsgContent = DeclineValues.getDifferentIPErr(primSocketSender.getRemoteIPAddress(), secNode.getNodeIP());
            } else {                                                                                                            // Node is reconnected
                secNode.setNodeStatus(NodeStatusEnum.FREE);
                secNode.setNodeIP(primSocketSender.getRemoteIPAddress());
                this.nodeDao.update(secNode);
            }
            
            if (declineMsgContent != null) {
                primSocketSender.sendMessage(new DeclineConnMsg(declineMsgContent));
                primSocketSender.closeConnection();
                return;
            }
            
            primStatusMaintainer.setSecNode(secNode);
            primSocketSender.sendMessage(new AcceptConnMsg());      
            primSocketSender.startSendingStepsAndIsAliveMessages();
            
           
            secNodeManager.updateNodeStatusEvent();
        } else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage());
        }
    }

    /**
     * Processing of RunStepAck message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    private void procRunStepAckMsg(RunStepAckMsg message) {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.WAIT_TO_RUN_ACK) {
            logger.logReceivedMessage(message.getMessageType(), message.getMessage().toString());
            StepRunData stepRunData = stepRunDataDao.getByIdWithCollections(primStatusMaintainer.getRJNSkey());
            
            if (stepRunData.isRerunButtonPressed()) {
                stepRunData.getErrors().forEach(error -> errorDao.delete(error.getId()));
            }
            stepRunData = stepRunDataDao.getByIdWithCollections(primStatusMaintainer.getRJNSkey()); // must be repeated
            
            stepRunData.setRunButtonPressed(false);
            stepRunData.setRerunButtonPressed(false);
            
            if (stepRunData.getTotalFsNodes() == 0) {
                stepRunData.setTotalFsNodes(message.getMessage());
            }
            
            if ((stepRunData.getStep().getInputType() == IOtypesEnum.FOLDER_OF_FILES || stepRunData.getStep().getInputType() == IOtypesEnum.FOLDER_OF_FOLDERS) &&
                    stepRunData.getStep().getOutputType() == IOtypesEnum.FOLDER) {
                stepRunData.setProcessedFsNodes(0);
            }
           
            if (stepRunData.getErrors().isEmpty())
                stepRunData.setStepStatus(StepStatusEnum.RUNNING);
            else
                stepRunData.setStepStatus(StepStatusEnum.RUNNING_ERROR);

            // firstStartedAt is set only once - when the task is first started.
            if (stepRunData.getFirstStartedAt() == null) {
                stepRunData.setFirstStartedAt(new Date(System.currentTimeMillis()));
            }
            
            if (stepRunData.getLastFinishedAt() == null) {
                if (stepRunData.getStep().getStepOrder() == 1) {
                    long waitingTime = System.currentTimeMillis() - stepRunData.getRunJob().getStartedAt().getTime();
                    stepRunData.setWaitingTime(waitingTime < 0 ? 0 : waitingTime);
                }
            } else {
                long waitingTime = stepRunData.getWaitingTime() + System.currentTimeMillis() - stepRunData.getLastFinishedAt().getTime();
                stepRunData.setWaitingTime(waitingTime < 0 ? 0 : waitingTime);
                stepRunData.setLastFinishedAt(null);
            }
            
            this.stepRunDataDao.update(stepRunData);

            

            primStatusMaintainer.setConnectionStatus(PrimConnectionStatus.RUN_TASK);

            Node node = nodeDao.getById(stepRunData.getNode().getId());
            node.setNodeStatus(NodeStatusEnum.OCCUPIED);
            nodeDao.update(node);

            // create events to update data at client side
            secNodeManager.updateTaskStatusEvent();
            secNodeManager.updateNodeStatusEvent();
        } else {
            logger.logUnexpMsgErr(message.getMessageType(), Long.toString(message.getMessage()));
        }
    }

    /**
     * Processing of Usage message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    private void procUsageMsg(UsageMsg message) {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.RUN_TASK) {
            //logger.logReceivedMessage(message.getMessageType(), message.getMessage().toString());
            StepRunData stepRunData = stepRunDataDao.getById(primStatusMaintainer.getRJNSkey());
            double cpuLoad = message.getMessage().getCpuLoad();
            double ramUsage = message.getMessage().getRamUsage();
            long dataCounter = stepRunData.getAvgRamCpuCounter();

            if (cpuLoad > stepRunData.getMaxCpuLoad()) {
                stepRunData.setMaxCpuLoad(cpuLoad);
            }
            if ((cpuLoad < stepRunData.getMinCpuLoad() || stepRunData.getMinCpuLoad() == 0) && cpuLoad > 0) {
                stepRunData.setMinCpuLoad(cpuLoad);
            }

            double avgCpuLoad = ((stepRunData.getAvgCpuLoad() * dataCounter) + cpuLoad) / (dataCounter + 1);
            stepRunData.setAvgCpuLoad(avgCpuLoad);

            if (ramUsage > stepRunData.getMaxRamUsage()) {
                stepRunData.setMaxRamUsage(ramUsage);
            }
            if ((ramUsage < stepRunData.getMinRamUsage() || stepRunData.getMinRamUsage() == 0) && ramUsage > 0) {
                stepRunData.setMinRamUsage(ramUsage);
            }

            double avgRamUsage = ((stepRunData.getAvgRamUsage() * dataCounter) + ramUsage) / (dataCounter + 1);
            stepRunData.setAvgRamUsage(avgRamUsage);

            stepRunData.setAvgRamCpuCounter(dataCounter + 1);

            stepRunDataDao.update(stepRunData);

        } else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage().toString());
        }
    }

    /**
     * Processing of StopStepAck message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    private void procStopStepAckMsg(StopStepAckMsg message) {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.WAIT_TO_STOP_ACK) {
            logger.logReceivedMessage(message.getMessageType(), "");
            StepRunData stepRunData = stepRunDataDao.getById(primStatusMaintainer.getRJNSkey());
            stepRunData.setStepStatus(stepRunDataDao.getErrorInputs(stepRunData.getId()).isEmpty() ? StepStatusEnum.PAUSED : StepStatusEnum.ERROR);
            stepRunData.setLastFinishedAt(new Date(System.currentTimeMillis()));
            stepRunDataDao.update(stepRunData);
            primStatusMaintainer.setConnectionStatus(PrimConnectionStatus.NOT_RUN_TASK);

            Node node = nodeDao.getById(stepRunData.getNode().getId());
            node.setNodeStatus(NodeStatusEnum.FREE);
            nodeDao.update(node);
            synchronized (this) {
                this.notifyAll();
            }
            // create events to update data at client side
            secNodeManager.updateTaskStatusEvent();
            secNodeManager.updateNodeStatusEvent();
        } else {
            logger.logUnexpMsgErr(message.getMessageType(), "");
        }
    }

    /**
     * Processing of Error message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    private void procFsNodeErrorMsg(ProcErrorMsg message) {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.RUN_TASK
                || primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.WAIT_TO_RUN_ACK) {
            logger.logReceivedMessage(message.getMessageType(), message.getMessage().toString());
            StepRunData stepRunData = stepRunDataDao.getById(primStatusMaintainer.getRJNSkey());
            IOtypesEnum ioType = stepRunData.getStep().getInputType();

            Error error = message.getMessage().getError();
            error.setStepRunData(stepRunData);
            new ErrorDao().save(error);
            
            stepRunData = stepRunDataDao.getByIdWithCollections(primStatusMaintainer.getRJNSkey()); // must be reloaded because new error is saved
            
            Predicate<StepRunData> isIoErrorOccurs = stepRunDataCheck -> !stepRunDataCheck.getErrors().isEmpty() && stepRunDataCheck.getErrors().stream().allMatch(err -> err.getPathToInputFile() == null);
            
            if (isIoErrorOccurs.test(stepRunData)) { // if IO error occurs
                    stepRunData.setRerunButtonPressed(false);
                    stepRunData.setRunButtonPressed(false);
            }
            
            stepRunData.setExecTime(stepRunData.getExecTime() + message.getMessage().getExecTime());

            // může být chyba validace, kdy započítat a kdy ne ? TODO - zeptat se
            /*if ((ioType == IOtypesEnum.FOLDER_OF_FILES || ioType == IOtypesEnum.FOLDER_OF_FOLDERS) && !isIoErrorOccurs.test(stepRunData)) {
                setFSnodeStatistics(stepRunData, message.getMessage());
            }*/
            if (message.getMessage().getFinishType() == FinishType.FINISH_ALL) {
                stepRunData.setStepStatus(StepStatusEnum.ERROR);
                stepRunData.setLastFinishedAt(new Date(System.currentTimeMillis()));
                
                primStatusMaintainer.setConnectionStatus(PrimConnectionStatus.NOT_RUN_TASK);
                Node node = nodeDao.getById(stepRunData.getNode().getId());
                node.setNodeStatus(NodeStatusEnum.FREE);
                nodeDao.update(node);
                // create events to update data at client side
                secNodeManager.updateNodeStatusEvent();
            } else {
                stepRunData.setStepStatus(StepStatusEnum.RUNNING_ERROR);
            }
            
            
            stepRunDataDao.update(stepRunData);
            // create events to update data at client side
            secNodeManager.updateTaskStatusEvent();
        } else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage().toString());
        }
    }

    /**
     * Processing of Finished message.
     * @param message The received message.
     * @throws IOException If an I/O exception occurs.
     */
    private void procFsNodeFinishedMsg(ProcFinishedMsg message) {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.RUN_TASK) {
            logger.logReceivedMessage(message.getMessageType(), message.getMessage().toString());
            StepRunData stepRunData = stepRunDataDao.getByIdWithCollections(primStatusMaintainer.getRJNSkey());
            IOtypesEnum ioType = stepRunData.getStep().getInputType();
            stepRunData.setExecTime(stepRunData.getExecTime()+ message.getMessage().getExecTime());
            stepRunData.setProcessedFsNodes(stepRunData.getProcessedFsNodes() + 1);

            if (ioType == IOtypesEnum.FOLDER_OF_FILES || ioType == IOtypesEnum.FOLDER_OF_FOLDERS) {
                setFSnodeStatistics(stepRunData, message.getMessage());
            }

            if (message.getMessage().getFinishType() == FinishType.FINISH_ALL) {
                // check if all steps are complete on all nodes
                Predicate<StepRunData> isNotFinished = stepRunData2 -> stepRunData2.getStepStatus() != StepStatusEnum.FINISHED;
                RunJobDao runJobDao = new RunJobDao();
                RunJob runJob = runJobDao.getById(this.primStatusMaintainer.getRJNSkey().getRunJobID());

                if (stepRunData.getTotalFsNodes() == stepRunData.getProcessedFsNodes()) {
                    stepRunData.setStepStatus(StepStatusEnum.FINISHED);
                    stepRunData.setFinishedAt(new Timestamp(System.currentTimeMillis()));
                    stepRunData.setLastFinishedAt(null);
                    
                    stepRunDataDao.setLastFinishedAtNextStep(stepRunData);
                    
                } else {
                    if (stepRunData.getErrors().isEmpty()) {
                        stepRunData.setStepStatus(StepStatusEnum.PAUSED);
                    } else {
                        stepRunData.setStepStatus(StepStatusEnum.ERROR);
                    }
                    stepRunData.setLastFinishedAt(new Date(System.currentTimeMillis()));
                }

                stepRunDataDao.update(stepRunData); // must be at this position

                // status is updated -> notify other nodes, that possibly new feasible step is available
                if (stepRunData.getStepStatus() == StepStatusEnum.FINISHED) {
                    // notify if synchronized step is finished
                    Job job = new JobDao().getByIdWithCollections(runJob.getJob().getId());
                    job.getJobNodes().forEach(jobNode -> {
                        if (!jobNode.getId().equals(this.primStatusMaintainer.getRJNSkey().getNodeID())) {
                            secNodeManager.setNewFeasibleStep(jobNode.getNodeIP());
                        }
                    });
                }

                primStatusMaintainer.setConnectionStatus(PrimConnectionStatus.NOT_RUN_TASK);

                Node node = nodeDao.getById(stepRunData.getNode().getId());
                node.setNodeStatus(NodeStatusEnum.FREE);
                nodeDao.update(node);
                // create events to update data at client side
                secNodeManager.updateNodeStatusEvent();

                if (!this.stepRunDataDao.getRunJobAllRunData(runJob.getId()).stream().anyMatch(isNotFinished)) {
                    runJob.setFinishedAt(new Date(System.currentTimeMillis()));
                    runJobDao.update(runJob);
                }
            } else {
                stepRunDataDao.update(stepRunData);
            }
            // create events to update data at client side
            secNodeManager.updateTaskStatusEvent();
        } else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage().toString());
        }
    }

    private void procRerunAckMsg(RerunAckMsg message) {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.RUN_TASK) {
            logger.logReceivedMessage(message.getMessageType(), "");
            StepRunData stepRunData = stepRunDataDao.getByIdWithCollections(primStatusMaintainer.getRJNSkey());
            stepRunData.setRerunButtonPressed(false);
            stepRunData.setStepStatus(StepStatusEnum.RUNNING);
            this.stepRunDataDao.update(stepRunData);

            stepRunData.getErrors().forEach(error -> errorDao.delete(error.getId()));
            // create events to update data at client side
            secNodeManager.updateTaskStatusEvent();
        } else {
            logger.logUnexpMsgErr(message.getMessageType(), "");
        }
    }

    /**
     * Method, which sets up the statistics of processing time per file/folder
     * with the use of the received running time.
     *
     * @param stepRunData The object with the statistic data.
     * @param finishValues The received data about running specific file/folder.
     */
    private void setFSnodeStatistics(StepRunData stepRunData, FinishValues finishValues) {
        long execTime = finishValues.getExecTime();
        long normalizedInputSize = finishValues.getInputSize() / BYTES_IN_MB;
        long normalizedExecTime = normalizedInputSize > 0 ? finishValues.getExecTime() / normalizedInputSize : 0;
        
        long actAvgFileProcCounter = stepRunData.getAvgFileProcCounter();
        long avgFileProcTime = ((stepRunData.getAvgFileProcTime() * actAvgFileProcCounter) + execTime) / (actAvgFileProcCounter + 1);
        
        if (execTime > stepRunData.getMaxFileProcTime()) {
            stepRunData.setMaxFileProcTime(execTime);
            stepRunData.setMaxFilePath(finishValues.getInputPath());
        }        
        if ((execTime < stepRunData.getMinFileProcTime() && execTime > 0) || actAvgFileProcCounter == 0) {
            stepRunData.setMinFileProcTime(execTime);
            stepRunData.setMinFilePath(finishValues.getInputPath());
        }
        
        
        if (normalizedExecTime > stepRunData.getMaxFileNormalizedProcTime()) {
            stepRunData.setMaxFileNormalizedProcTime(normalizedExecTime);
            stepRunData.setMaxFileNormalizedPath(finishValues.getInputPath());
        }
        if ((normalizedExecTime < stepRunData.getMinFileNormalizedProcTime() && execTime > 0) || actAvgFileProcCounter == 0) {
            stepRunData.setMinFileNormalizedProcTime(normalizedExecTime);
            stepRunData.setMinFileNormalizedPath(finishValues.getInputPath());
        }

        stepRunData.setAvgFileProcTime(avgFileProcTime);
        stepRunData.setAvgFileProcCounter(actAvgFileProcCounter + 1);
    }

    /**
     * The thread, that receives and processes incoming messages from Secondary Node.
     */
    @Override
    public void run() {
        while (true) {
            Object o = null;
            try {
                o = receiveMessage();
                GenericSocketMsg message = (GenericSocketMsg) o;
                processMessage(message);
            } catch (ClassNotFoundException ex) {
                logger.logUnknownObjectErr(o);
            } catch (IOException e) {
                logger.logCloseConnErr(e);
                primSocketSender.closeConnection();
                Node secNode = primStatusMaintainer.getSecNode();
                if (secNode != null) {
                    // remove node from connected pool of Secondary nodes
                    secNodeManager.removeFromSocketCommunicators(secNode.getNodeIP());
                    // set node status as disconnected
                    secNode.setNodeStatus(NodeStatusEnum.DISCONNECTED);
                    this.nodeDao.update(secNode);
                    // create events to update data at client side
                    secNodeManager.updateNodeStatusEvent();

                    // set status of running step as waiting
                    if (primStatusMaintainer.getRJNSkey() != null) {
                        StepRunData stepRunData = stepRunDataDao.getById(primStatusMaintainer.getRJNSkey());
                        if (stepRunData.getStepStatus() == StepStatusEnum.RUNNING_ERROR) {
                            stepRunData.setStepStatus(StepStatusEnum.PAUSED);
                        } else {
                            stepRunData.setStepStatus(StepStatusEnum.WAITING);
                        }
                        stepRunData.setLastFinishedAt(new Date(System.currentTimeMillis()));
                        stepRunDataDao.update(stepRunData);
                        // create events to update data at client side
                        secNodeManager.updateTaskStatusEvent();
                    }
                }
                return;
            }
        }
    }
}
