/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.RunTasks;

import com.app.primaryNodeApp.services.dataClasses.ErrorDetail;
import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.TaskStatusOnNode;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskOnNodeService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.primaryNode.PrimaryNode;
import com.app.primaryNodeApp.services.NodeService.NodeService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import com.app.primaryNodeApp.services.StepRunDataService.StepRunDataService;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * The backing bean for displaying detail of errors on secondary node page.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class ErrorsDetailBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long runJobID;
    private Long nodeID;

    private RunTaskData runTaskData;

    private TaskStatusOnNode taskStatusOnNode;
    private List<ErrorDetail> errorsDetail;

    @Inject
    PrimaryNode primaryNode;
    
    private final RunJobService runJobService;
    private final NodeService nodeService;
    private final StepRunDataService stepRunDataService;
    
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor
     */
    public ErrorsDetailBean() {
        runJobService = new RunJobService();
        nodeService = new NodeService();
        stepRunDataService = new StepRunDataService();
    }

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        errorsDetail = new ArrayList<>();
    }

    /**
     * Method, which is called when URL parameters(runJobID, nodeID) are processed.
     */
    public void loadData() {
        
        if (runJobService.getById(runJobID) == null) {
            throw new RuntimeException("Úloha s ID " + runJobID + " neexistuje.");
        }
        if (nodeService.getById(nodeID) == null) {
            throw new RuntimeException("Výpočetní uzel s ID " + nodeID + " neexistuje.");
        }
        if (stepRunDataService.getNodeAllRunData(runJobID, nodeID).isEmpty()) {
            throw new RuntimeException("Úloha s ID " + runJobID + " nebyla na uzlu s ID " + nodeID + " spuštěna.");
        }
        
        runTaskData = new RunTaskService().getRunTaskData(runJobID);
        taskStatusOnNode = RunTaskOnNodeService.getTaskStatusOnNodeData(runJobID, nodeID);
        
        errorsDetail.clear();
        stepRunDataService.getByIdWithCollections(new RunJobNodeStepKey(runJobID, nodeID, taskStatusOnNode.getStepID())).getErrors().forEach(error -> {
            errorsDetail.add(new ErrorDetail(error));
        });
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
     * @param runJobID New value of runJobID.
     */
    public void setRunJobID(Long runJobID) {
        this.runJobID = runJobID;
    }

    /**
     * Getter of nodeID.
     * @return NodeID.
     */
    public Long getNodeID() {
        return nodeID;
    }

    /**
     * Setter of nodeID.
     * @param nodeID NodeID.
     */
    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Getter of task status data on secondary node.
     * @return Task status data on secondary node.
     */
    public TaskStatusOnNode getTaskStatusOnNode() {
        return taskStatusOnNode;
    }

    /**
     * Getter of run task data.
     * @return Run task data.
     */
    public RunTaskData getRunTaskData() {
        return runTaskData;
    }

    /**
     * Getter of all errors on secondary node that occur.
     * @return List of all errors on secondary node.
     */
    public List<ErrorDetail> getErrorsDetail() {
        return errorsDetail;
    }

    /**
     * The handler of click event on repeat button.
     * @throws IOException If an IOException occurs.
     */
    public void runStoppedTaskOnNodeHandler() throws IOException {
        primaryNode.runStoppedTaskOnSecNode(runJobID, nodeID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("runTaskDetail.xhtml?runJobID=" + this.runJobID);
    }
    
    /**
     * Handler of click event on repeat task on node button.
     * @throws java.io.IOException If an IOException occurs.
     */
    public void repeatTaskOnNodeHandler() throws IOException {
        primaryNode.rerunJobOnSecNode(runJobID, nodeID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("runTaskDetail.xhtml?runJobID=" + this.runJobID);
    }

    /**
     * Update task data when server push event is received on client side.
     */
    public void update() {
        this.loadData();
    }
}
