/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.RunTasks;

import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.TaskStatusOnNode;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskOnNodeService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import com.app.primaryNodeApp.model.database.entity.Error;
import com.app.primaryNodeApp.services.ErrorService.ErrorService;
import com.app.primaryNodeApp.services.NodeService.NodeService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import com.app.primaryNodeApp.services.StepRunDataService.StepRunDataService;

/**
 * The backing bean of log and stderr content page.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class LogStderrContentBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long runJobID;
    private Long nodeID;
    private Long errorID;
    private String contentType;
    
    private RunTaskData runTaskData;
    private TaskStatusOnNode taskStatusOnNode;
    private Error error;
    
    private final RunJobService runJobService;
    private final NodeService nodeService;
    private final StepRunDataService stepRunDataService;
    private final ErrorService errorService;

    /** OBJECT METHODS **/
    
    /**
     * Constructor
     */
    public LogStderrContentBean() {
        runJobService = new RunJobService();
        nodeService = new NodeService();
        stepRunDataService = new StepRunDataService();
        errorService = new ErrorService();
    }
    
    /**
     * Method, which is called when URL parameters(runJobID, nodeID, errorID, contentType) are processed.
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
        this.error = errorService.getById(this.errorID);
        if (error == null) {
            throw new RuntimeException("Chyba s ID " + errorID + " neexistuje.");
        }
        
        runTaskData = new RunTaskService().getRunTaskData(runJobID);
        taskStatusOnNode = RunTaskOnNodeService.getTaskStatusOnNodeData(runJobID, nodeID);       
        
    }

    /**
     * Getter of runJobID.
     * @return RunJobID;
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
     * @param nodeID New value of nodeID.
     */
    public void setNodeID(Long nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Getter of errorID.
     * @return ErrorID.
     */
    public Long getErrorID() {
        return errorID;
    }

    /**
     * Setter of errorID.
     * @param errorID New value of errorID.
     */
    public void setErrorID(Long errorID) {
        this.errorID = errorID;
    }

    /**
     * Getter of type of displayed value - stderr or log.
     * @return Type of displayed value.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Setter of type of displayed value - stderr or log.
     * @param contentType New value of content type.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     * Getter of task status on node data.
     * @return Task status on node data.
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
     * Getter od error details.
     * @return Object with error details.
     */
    public Error getError() {
        return error;
    }
    
    /**
     * Update task data when server push event is received on client side.
     */
    public void update() {
        this.loadData();
    }
}
