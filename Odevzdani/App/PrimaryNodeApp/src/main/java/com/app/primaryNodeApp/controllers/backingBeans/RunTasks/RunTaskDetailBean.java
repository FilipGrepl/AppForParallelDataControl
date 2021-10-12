/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.RunTasks;

import com.app.primaryNodeApp.services.dataClasses.ActRunStepData;
import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.TaskStatusOnNode;
import com.app.primaryNodeApp.services.RunTaskServices.ActRunStepsService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskOnNodeService;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import com.app.primaryNodeApp.model.primaryNode.PrimaryNode;
import com.app.primaryNodeApp.services.NodeService.NodeService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * The backing bean for page of run task detail.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class RunTaskDetailBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long runJobID;

    private RunTaskData taskData;
    private List<ActRunStepData> actRunStepsData;
    private List<TaskStatusOnNode> taskStatusOnNodes;

    private boolean isDisableRunButton;
    private boolean isDisableStopButton;
    private boolean isDisableDeleteButton;

    private List<TaskStatusOnNode> tasksStatusOnNodeFiltered;
    private List<String> runningSteps;
    private List<String> runningStepsStrs;

    @Inject
    PrimaryNode primaryNode;
    
    private final RunJobService runJobService;
    private final NodeService nodeService;
    
    /** OBJECT METHODS **/
    
    /**
     *  Constructor.
     */
    public RunTaskDetailBean() {
        runJobService = new RunJobService();
        nodeService = new NodeService();
    }

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.actRunStepsData = new ArrayList<>();

        this.taskStatusOnNodes = new ArrayList<>();

        this.runningSteps = new ArrayList<>();
        this.runningStepsStrs = new ArrayList<>();

    }

    /**
     * Method, which is called when URL parameters(runJobID, nodeID, errorID, contentType) are processed.
     */
    public void loadData() {
        
        if (runJobService.getById(runJobID) == null) {
            throw new RuntimeException("Úloha s ID " + runJobID + " neexistuje.");
        }
        
        this.taskData = new RunTaskService().getRunTaskData(runJobID);
        this.actRunStepsData = ActRunStepsService.getActRunStepsData(runJobID);

        this.taskStatusOnNodes = RunTaskOnNodeService.getTaskStatusOnNodeData(runJobID);

        Predicate<TaskStatusOnNode> byRunStatus = (TaskStatusOnNode taskStatusOnNode) -> 
                   taskStatusOnNode.getStepStatus() == StepStatusEnum.RUNNING
                || taskStatusOnNode.getStepStatus() == StepStatusEnum.RUNNING_ERROR
                || taskStatusOnNode.getStepStatus() == StepStatusEnum.WAITING;
        Predicate<TaskStatusOnNode> byStopStatus = (TaskStatusOnNode taskStatusOnNode) -> 
                   (taskStatusOnNode.getStepStatus() == StepStatusEnum.PAUSED
                || taskStatusOnNode.getStepStatus() == StepStatusEnum.ERROR) && taskStatusOnNode.isIsRunnableStepRunData();

        this.isDisableStopButton = !taskStatusOnNodes.stream().anyMatch(byRunStatus);
        this.isDisableRunButton = !taskStatusOnNodes.stream().anyMatch(byStopStatus);
        this.isDisableDeleteButton = taskStatusOnNodes.stream().anyMatch(byRunStatus);

        // for filtering
        for (TaskStatusOnNode node : this.taskStatusOnNodes) {
            if (!this.runningSteps.contains(node.getRunningStepName())) {
                this.runningSteps.add(node.getRunningStepName());
            }
            if (!this.runningStepsStrs.contains(node.getStepsStr())) {
                this.runningStepsStrs.add(node.getStepsStr());
            }
        }
    }

    /**
     * Getter of task data.
     * @return Task data.
     */
    public RunTaskData getTaskData() {
        return taskData;
    }

    /**
     * Getter of runJobID.
     * @return RunJobID.
     */
    public Long getRunJobID() {
        return runJobID;
    }

    /**
     * Setter of runJobID.
     * @param runJobID Nea value of runJobID.
     */
    public void setRunJobID(Long runJobID) {
        this.runJobID = runJobID;
    }

    /**
     * Getter of data of actual processed steps of task.
     * @return List of actual processed steps of task.
     */
    public List<ActRunStepData> getActRunStepsData() {
        return actRunStepsData;
    }

    /**
     * Method, which returns true if the run button should be disabled and false if the run button shouldn't be disabled.
     * @return True if the run button should be disabled. False otherwise.
     */
    public boolean isIsDisableRunButton() {
        return isDisableRunButton;
    }

    /**
     * Method, which returns true if the stop button should be disabled and false if the stop button shouldn't be disabled.
     * @return True if the stop button should be disabled. False otherwise.
     */
    public boolean isIsDisableStopButton() {
        return isDisableStopButton;
    }

    /**
     * Method, which returns true if the delete button should be disabled and false if the delete button shouldn't be disabled.
     * @return True if the delete button should be disabled. False otherwise.
     */
    public boolean isIsDisableDeleteButton() {
        return isDisableDeleteButton;
    }
    
    /**
     * Getter of filtered task status on node data.
     * @return List of filtered task status on node data.
     */
    public List<TaskStatusOnNode> getTasksStatusOnNodeFiltered() {
        return tasksStatusOnNodeFiltered;
    }

    /**
     * Setter of filtered task status on node data.
     * @param tasksStatusOnNodeFiltered New value of list of task status on nodes data.
     */
    public void setTasksStatusOnNodeFiltered(List<TaskStatusOnNode> tasksStatusOnNodeFiltered) {
        this.tasksStatusOnNodeFiltered = tasksStatusOnNodeFiltered;
    }

    /**
     * Getter of task status on nodes data.
     * @return List of task status on node data.
     */
    public List<TaskStatusOnNode> getTaskStatusOnNodes() {
        return taskStatusOnNodes;
    }

    /**
     * Setter of task status on nodes data.
     * @param taskStatusOnNodes New value of list of task status on nodes data.
     */
    public void setTaskStatusOnNodes(List<TaskStatusOnNode> taskStatusOnNodes) {
        this.taskStatusOnNodes = taskStatusOnNodes;
    }

    /**
     * Getter of run steps names for filtering.
     * @return List of run steps names.
     */
    public List<String> getRunningSteps() {
        return runningSteps;
    }

    /**
     * Setter of run steps names for filtering.
     * @param runningSteps New value of run steps names.
     */
    public void setRunningSteps(List<String> runningSteps) {
        this.runningSteps = runningSteps;
    }

    /**
     * Getter of order of running steps with number of all steps of task.
     * @return List of order of all running steps with number of all steps of task.
     */
    public List<String> getRunningStepsStrs() {
        return runningStepsStrs;
    }

    /**
     * Setter of order of running steps with number of all steps of task.
     * @param runningStepsStrs New value of order of all running steps with number of all steps of task.
     */
    public void setRunningStepsStrs(List<String> runningStepsStrs) {
        this.runningStepsStrs = runningStepsStrs;
    }

    /**
     * Handler of click event on stop task on node button.
     * @param nodeID Id of node on which the task should be stopped.
     */
    public void stopTaskOnNodeHandler(Long nodeID) {
        primaryNode.stopJobOnSecNode(runJobID, nodeID);
        this.loadData();
        Node node = nodeService.getById(nodeID);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Úloha " + this.taskData.getTaskName() + " na uzlu " + node.getNodeName() + " byla pozastavena"));
    }

    /**
     * Handler of click event on stop task on all nodes button.
     */
    public void stopTaskHandler() {
        primaryNode.stopJobOnAllNodes(runJobID);
        this.loadData();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Úloha " + this.taskData.getTaskName() + " byla pozastavena"));
    }

    /**
     * Handler of click event on run task on node button.
     * @param nodeID Id of node on which the task should be ran.
     */
    public void runStoppedTaskOnNodeHandler(Long nodeID) {
        primaryNode.runStoppedTaskOnSecNode(runJobID, nodeID);
        this.loadData();
        Node node = nodeService.getById(nodeID);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Úloha " + this.taskData.getTaskName() + " na uzlu " + node.getNodeName() + " byla spuštěna"));
    }

    /**
     * Handler of click event on run task on all nodes button.
     */
    public void runStoppedTaskHandler() {
        primaryNode.runStoppedTaskOnAllNodes(runJobID);
        this.loadData();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Úloha " + this.taskData.getTaskName() + " byla spuštěna"));
    }

    /**
     * Handler of click event on repeat task on node button.
     * @param nodeID Id of node on which the task should be repeated.
     */
    public void repeatTaskOnNodeHandler(Long nodeID) {
        primaryNode.rerunJobOnSecNode(runJobID, nodeID);
        this.loadData();
        Node node = nodeService.getById(nodeID);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Opětovné zpracování chybných vstupů úlohy " + this.taskData.getTaskName() + " na uzlu " + node.getNodeName() + " bylo spuštěno"));
    }
    
     /**
     * Handler of click event on skipp error inputs on node button.
     * @param nodeID Id of node on which the processing of error inputs should be skipped.
     */
    public void skipErrOnNodeHandler(Long nodeID) {
        primaryNode.skipErrInputsOnNode(runJobID, nodeID);
        this.loadData();
        Node node = nodeService.getById(nodeID);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Zpracování chybných vstupů úlohy " + this.taskData.getTaskName() + " na uzlu " + node.getNodeName() + " bylo přeskočeno"));
    }
    
    /**
     * Handler of click event on delete task button.
     * @throws IOException If an IOException occurs.
     */
    public void deleteTaskHandler() throws IOException {
        runJobService.delete(runJobID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("runningTasks.xhtml?i=0");
    }

    /**
     * Getter of all step statuses on all nodes for filtering.
     * @return List of all step statuses on all nodes for filtering.
     */
    public List<String> getStepStatuses() {
        List<String> statuses = new ArrayList<>();
        for (StepStatusEnum status : StepStatusEnum.values()) {
            if (!statuses.contains(status.getMessage())) {
                statuses.add(status.getMessage());
            }
        }
        return statuses;
    }

    /**
     * Update task data when server push event is received on client side.
     */
    public void update() {
        this.loadData();
    }
}
