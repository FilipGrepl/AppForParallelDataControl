/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.RunTaskServices;

import com.app.primaryNodeApp.services.dataClasses.TaskStatusOnNode;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.NodeDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.dao.StepRunDataDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The service for obtaining data about running task.
 * @author filip
 */
public class RunTaskOnNodeService {

    /**
     * Method, that returns data about task status on specific node.
     * @param runJobID The id of run job for which the running data to be returned.
     * @param nodeID The id of node for whcih the running data to be returned.
     * @return Data about task status on specific node.
     */
    public static TaskStatusOnNode getTaskStatusOnNodeData(Long runJobID, Long nodeID) {
        RunJob runJob = new RunJobDao().getByIdWithCollections(runJobID);
        Job job = new JobDao().getByIdWithCollections(runJob.getJob().getId());
        Node node = new NodeDao().getById(nodeID);

        return RunTaskOnNodeService.createTaskStatusOnNodeData(runJob, job, node);
    }

    /**
     * Method, that returns data about task status on all nodes.
     * @param runJobID The id of run job for which the running data to be returned.
     * @return List of running data about task status on all nodes.
     */
    public static List<TaskStatusOnNode> getTaskStatusOnNodeData(Long runJobID) {
        List<TaskStatusOnNode> taskStatusOnNodes = new ArrayList<>();
        RunJob runJob = new RunJobDao().getByIdWithCollections(runJobID);
        Job job = new JobDao().getByIdWithCollections(runJob.getJob().getId());

        for (Node node : job.getJobNodes()) {
            taskStatusOnNodes.add(RunTaskOnNodeService.createTaskStatusOnNodeData(runJob, job, node));
        }
        return taskStatusOnNodes;
    }

    /**
     * This method is for optimalization. When this method is called multiple times, the runJob and job is obtained from database only once.
     * It returns data about task status on specific node.
     * @param runJob The runJob for which the data about task status to be returned.
     * @param job The job for which the data about task status to be returned.
     * @param node The node for which the data about task status to be returned.
     * @return Data about task status on specific node.
     */
    private static TaskStatusOnNode createTaskStatusOnNodeData(RunJob runJob, Job job, Node node) {

        TaskStatusOnNode taskStatusOnNode = null;
        List<StepRunData> stepsRunData = RunTaskOnNodeService.getStepsRunData(runJob, node);
        stepsRunData.sort((StepRunData stepRunData1, StepRunData stepRunData2) -> stepRunData1.getStep().getStepOrder() - stepRunData2.getStep().getStepOrder());
        for (StepRunData stepRunData : stepsRunData) {

            boolean isLastStep = stepsRunData.indexOf(stepRunData) + 1 == stepsRunData.size();
            if (stepRunData.getStepStatus() != StepStatusEnum.FINISHED || isLastStep) {
                taskStatusOnNode = RunTaskOnNodeService.setTaskStatusOnNodeData(job, node, stepRunData);
                break;
            }
        }
        return taskStatusOnNode;
    }

    /**
     * Method, that sets task status on node data for specific stepRunData.
     * @param job The job for which the data about task status to be returned.
     * @param node The node for which the data about task status to be returned.
     * @param stepRunData The run data about specific step on specific node for which the data about task status to be created.
     * @return Data about task status on specific node.
     */
    private static TaskStatusOnNode setTaskStatusOnNodeData(Job job, Node node, StepRunData stepRunData) {

        StepRunData stepRunDataWithCollections = new StepRunDataDao().getByIdWithCollections(stepRunData.getId());
        
        
        Long runningTime;
        if (stepRunData.getStepStatus() == StepStatusEnum.FINISHED) {
            runningTime = stepRunData.getFinishedAt().getTime() - stepRunData.getFirstStartedAt().getTime();
        } else {
            runningTime = stepRunData.getFirstStartedAt() == null ? 0 : System.currentTimeMillis() - stepRunData.getFirstStartedAt().getTime();
        }
        

        TaskStatusOnNode taskStatusOnNode = new TaskStatusOnNode();
        taskStatusOnNode.setStepID(stepRunData.getStep().getId());
        taskStatusOnNode.setNodeID(node.getId());
        taskStatusOnNode.setNodeName(node.getNodeName());

        taskStatusOnNode.setRunningStep(stepRunData.getStep().getStepOrder());
        taskStatusOnNode.setAllSteps(job.getJobSteps().size());
        taskStatusOnNode.setRunningStepName(stepRunData.getStep().getStepName());

        taskStatusOnNode.setRunningTime(runningTime < 0 ? 0 : runningTime);
        taskStatusOnNode.setWaitingTime(stepRunData.getWaitingTimeToDisplay());

        taskStatusOnNode.setFinishedFiles(stepRunData.getProcessedFsNodes());
        taskStatusOnNode.setAllFiles(stepRunData.getTotalFsNodes());
        taskStatusOnNode.setStepStatus(stepRunData.getStepStatus());
        taskStatusOnNode.setStatusClass(CommonRunService.getStepStatusClass(stepRunData.getStepStatus()));
        
        boolean isIoErrorOccurs = !stepRunDataWithCollections.getErrors().isEmpty() && 
                                    stepRunDataWithCollections.getErrors().stream().allMatch(error -> error.getPathToInputFile() == null);
        
        boolean isRunnableStepRunData = isIoErrorOccurs || 
                stepRunData.getTotalFsNodes() - stepRunData.getProcessedFsNodes() != stepRunDataWithCollections.getErrors().size() || 
                stepRunData.getTotalFsNodes() == 0;
        
        taskStatusOnNode.setIsRunnableStepRunData(isRunnableStepRunData);
        
        taskStatusOnNode.setIsRenderDetailErrorBtn((!stepRunData.isRerunButtonPressed() && !stepRunDataWithCollections.getErrors().isEmpty()));
        taskStatusOnNode.setIsRenderErrRepeatButton(!stepRunData.isRerunButtonPressed() && !stepRunDataWithCollections.getErrors().isEmpty() && !isIoErrorOccurs);
        
        
        switch (taskStatusOnNode.getStepStatus()) { 
            case RUNNING:
            case RUNNING_ERROR:
                taskStatusOnNode.setIsRenderStopBtn(true);
                taskStatusOnNode.setIsRenderRunButton(false);
                break;
            case ERROR:
            case PAUSED:
                taskStatusOnNode.setIsRenderStopBtn(false);                
                if (isRunnableStepRunData)
                    taskStatusOnNode.setIsRenderRunButton(true);
                else
                    taskStatusOnNode.setIsRenderRunButton(false);                
                break;
            case WAITING:
                taskStatusOnNode.setIsRenderStopBtn(true);
                taskStatusOnNode.setIsRenderRunButton(false);
                break;
            default: // FINISHED
                taskStatusOnNode.setIsRenderStopBtn(false);
                taskStatusOnNode.setIsRenderRunButton(false);
        }
        
        if ( (taskStatusOnNode.getStepStatus() == StepStatusEnum.ERROR || taskStatusOnNode.getStepStatus() == StepStatusEnum.PAUSED) &&
                stepRunData.getTotalFsNodes() - stepRunData.getProcessedFsNodes() == stepRunDataWithCollections.getErrors().size() && stepRunData.getTotalFsNodes() != 0)
            taskStatusOnNode.setIsRenderErrSkipButton(true);
        else
            taskStatusOnNode.setIsRenderErrSkipButton(false);

        return taskStatusOnNode;
    }

    /**
     * Method, that returns running data for specific run job on specific secondary node.
     * @param runJob The run job for which the StepRunData to be returned.
     * @param node The node for which the StepRunData to be returned.
     * @return List of running data about all steps on specific secondary node of specific task.
     */
    private static List<StepRunData> getStepsRunData(RunJob runJob, Node node) {
        Predicate<StepRunData> byNodeID = stepRunData -> stepRunData.getNode().getId().equals(node.getId());
        List<StepRunData> filteredStepRunData = runJob.getStepRunData().stream().filter(byNodeID).collect(Collectors.toList());
        return filteredStepRunData;
    }
}
