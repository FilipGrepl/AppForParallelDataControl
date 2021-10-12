/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;


import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.enums.StepStatus;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Predicate;

/**
 * The Dao object of StepRunData entity.
 * @author Filip
 */
public class StepRunDataDao extends Dao<StepRunData, RunJobNodeStepKey> {
    
    /**
     * Constructor
     */
    public StepRunDataDao() {
        super(StepRunData.class);
    }
    
    /**
     * Method, that returns error inputs which corresponds with specific StepRunData entity.
     * @param RJNSkey Key to specific StepRunData entity.
     * @return List of all error inputs of specific step on specific node.
     */
    public List<String> getErrorInputs(RunJobNodeStepKey RJNSkey) {
        List<String> errInputs = new ArrayList<>();
        this.getByIdWithCollections(RJNSkey).getErrors().forEach(error -> {
            if (error.getPathToInputFile() != null)
                errInputs.add(error.getPathToInputFile());
        });
        return errInputs;
    }
    
    
    /**
     * Method, that returns data about step, that can be started on specfic node.
     * @param nodeID Id of node for which the data about step that can be started to be returned.
     * @return Data about step, that can be started on specfic node. If no entity is found, null is returned.
     */
    public StepRunData getStepToRun(Long nodeID) {
        this.prepareCriteria();
        Predicate byNodeID = this.queryBuilder.equal(this.queryRoot.get("node"), nodeID);
        Predicate byStatus = this.queryBuilder.equal(this.queryRoot.get("stepStatus"), StepStatusEnum.WAITING);
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.and(byNodeID, byStatus));
        List<StepRunData> stepRunData = this.executeQuery();
        stepRunData.sort((StepRunData stepRunData1, StepRunData stepRunData2) -> stepRunData1.getRunJob().getStartedAt().compareTo(stepRunData2.getRunJob().getStartedAt()));
        // check if the previous step has been finished on this node or on all nodes - if the previous step must be synchronized on all nodes
        for (StepRunData JRD : stepRunData) {
            if (canBeStarted(JRD))
                return JRD;
        }
        return null;
    }
    
    /**
     * Method, that returns running data about all steps on all nodes.
     * @param runJobId The id of runJob for which the running data to be returned.
     * @return The running data about all steps on all nodes.
     */
    public List<StepRunData> getRunJobAllRunData(Long runJobId) {
        this.prepareCriteria();
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.equal(this.queryRoot.get("runJob"), runJobId));
        return this.executeQuery();
    }
    
    /**
     * Method, that returns running data about specific step of task on all nodes.
     * @param runJobId The id of runJob for which the running data to be returned.
     * @param stepId The id of step for which the running data to be returned.
     * @return The running data about specific step of task on all nodes.
     */
    public List<StepRunData> getStepAllRunData(Long runJobId, Long stepId) {
        this.prepareCriteria();
        Predicate[] predicates = new Predicate[2];
        predicates[0] = this.queryBuilder.equal(this.queryRoot.get("runJob"), runJobId);
        predicates[1] = this.queryBuilder.equal(this.queryRoot.get("step"), stepId);   
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.and(predicates));
        return this.executeQuery();
    }
    
    /**
     * Method, that returns running data about all steps of task on specific node.
     * @param runJobId The id of runJob for which the running data to be returned.
     * @param nodeId The id of node for which the running data to be returned.
     * @return The running data about specific step of task on specific node.
     */
    public List<StepRunData> getNodeAllRunData(Long runJobId, Long nodeId) {
        this.prepareCriteria();
        Predicate[] predicates = new Predicate[2];
        predicates[0] = this.queryBuilder.equal(this.queryRoot.get("runJob"), runJobId);
        predicates[1] = this.queryBuilder.equal(this.queryRoot.get("node"), nodeId);   
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.and(predicates));
        return this.executeQuery();
    }
    
    /**
     * Method that check if specific step of task on specific node can be started.
     * @param stepRunData The data about specific step of task on specific node.
     * @return True if step can be started. False otherwise.
     */
    private boolean canBeStarted(StepRunData stepRunData) {
        Step step = stepRunData.getStep();
        Step previousStep = new StepDao().getPreviousStep(step);
        if (previousStep != null) {
            if (previousStep.isSynchronizedStep()) {
                
                for(StepRunData srd : this.getStepAllRunData(stepRunData.getRunJob().getId(), previousStep.getId())) {
                    if (srd.getStepStatus() != StepStatusEnum.FINISHED)
                        return false;
                }
            }
            else {
                RunJobNodeStepKey preRunJobNodeStepKey = new RunJobNodeStepKey(
                    stepRunData.getRunJob().getId(), 
                    stepRunData.getNode().getId(), 
                    previousStep.getId());
                StepRunData prevStepRunData = this.getById(preRunJobNodeStepKey);
                if (prevStepRunData == null) {
                    daoLogger.logMissingStepRunDataRecErr(preRunJobNodeStepKey);
                    return false;
                }
                else if (prevStepRunData.getStepStatus() != StepStatusEnum.FINISHED)
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Method, that initializes statuses of processed steps when the primary node is restarted.
     */
    public void initializeStepRunDataStatus() {
        this.getAll().forEach(s -> {
            if (s.getStepStatus() == StepStatusEnum.RUNNING_ERROR) {
                s.setStepStatus(StepStatusEnum.ERROR);
                this.update(s);
            }
            else if (s.getStepStatus() == StepStatusEnum.RUNNING) {
                s.setStepStatus(StepStatusEnum.WAITING);
                this.update(s);
            }
        });
    }
    
    /**
     * Method, that sets lastFiniShedAt property of next step in order.
     * @param stepRunData StepRunData of currently processed step.
     */
    public void setLastFinishedAtNextStep(StepRunData stepRunData) {
        List<StepRunData> stepsRunData = this.getNodeAllRunData(stepRunData.getRunJob().getId(), stepRunData.getNode().getId());
        stepsRunData
                .stream()
                .filter(stepRunDataIn -> stepRunDataIn.getStep().getStepOrder() == stepRunData.getStep().getStepOrder()+1)
                .findFirst().ifPresent(nextStepRunData -> {
                    nextStepRunData.setLastFinishedAt(new Date(System.currentTimeMillis()));
                    this.update(nextStepRunData);
                });
    }
    
    /**
     * Method, that creates StepRunData for all steps of specific task on all nodes. 
     * @param job The job for which the StepRunData to be created.
     * @param runJobID The Id of runJob for which the StepRunData to be created.
     * @return List of created StepRunData for all steps of specific task on all nodes.
     */
    public List<StepRunData> createNewStepRecords(Job job, Long runJobID) {
        final List<StepRunData> stepRunData = new ArrayList<>();
        job.getJobNodes().forEach(node -> {
            job.getJobSteps().forEach(step -> {
                stepRunData.add(new StepRunData( new RunJobNodeStepKey(runJobID, node.getId(), step.getId()), StepStatus.StepStatusEnum.WAITING, 
                                                0, 0, null, null, null, 0, 0, 0, 0, 
                                                0, 0, 0, 0, 0, 0, null, 0, null, 0, null, 0, null, 0, 0, true, false));                
            });
        });        
        return stepRunData;
    }
}
