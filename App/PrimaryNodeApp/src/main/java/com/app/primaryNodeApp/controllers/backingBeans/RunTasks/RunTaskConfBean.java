/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.RunTasks;

import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.StepConfData;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskConfService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskService;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * The backing bean for run task configuration page.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class RunTaskConfBean implements Serializable {
    
    /** OBJECT PROPERTIES **/
    
    private Long runJobID;

    private RunTaskData taskData;
    private List<StepConfData> stepConfData;


    private final RunJobService runJobService;
    
    /** OBJECT METHODS **/
    
    /**
     *  Constructor.
     */
    public RunTaskConfBean() {
        runJobService = new RunJobService();
    }
    
    /**
     * Method, which is called when URL parameters(runJobID) are processed.
     */
    public void loadData() {        
        RunJob runJob = runJobService.getById(runJobID);
        if (runJob == null) {
            throw new RuntimeException("Úloha s ID " + runJobID + " neexistuje.");
        }
        
        this.taskData = new RunTaskService().getRunTaskData(runJobID);
        this.stepConfData = RunTaskConfService.getStepConfData(runJob.getJob().getId());        
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
     * Getter of task data.
     * @return Task data.
     */
    public RunTaskData getTaskData() {
        return taskData;
    }

    /**
     * Getter of steps configuration data.
     * @return List of steps configuration data.
     */
    public List<StepConfData> getStepsConfData() {
        return stepConfData;
    }   
    
    /**
     * Update task data when server push event is received on client side.
     */
    public void update() {
        this.taskData = new RunTaskService().getRunTaskData(runJobID);
    }
}
