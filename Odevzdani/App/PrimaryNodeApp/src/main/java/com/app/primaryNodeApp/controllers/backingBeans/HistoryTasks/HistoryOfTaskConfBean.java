/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.HistoryTasks;

import com.app.primaryNodeApp.services.dataClasses.HistoryTaskData;
import com.app.primaryNodeApp.services.dataClasses.StepConfData;
import com.app.primaryNodeApp.services.HistoryTasServices.HistoryTasksService;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskConfService;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * The backing bean for history task configuration.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class HistoryOfTaskConfBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long runJobID;

    private HistoryTaskData historyTaskData;
    private List<StepConfData> stepConfData;
    
    private final RunJobService runJobService;
    
    /** OBJECT METHODS **/

    /**
     * Constructor
     */
    public HistoryOfTaskConfBean() {
        runJobService = new RunJobService();
    }

    /**
     * Method which is called when the URL parameters (runJobID) are loaded.
     */
    public void loadData() {
        RunJob runJob = runJobService.getById(runJobID);
        if (runJob == null) {        
            throw new RuntimeException("Úloha s ID " + runJobID + " neexistuje.");
        }
        historyTaskData = new HistoryTasksService().getRunTaskData(runJobID);
        this.stepConfData = RunTaskConfService.getStepConfData(runJob.getJob().getId());
    }

    /**
     * Getter of run job id.
     * @return Run job id value.
     */
    public Long getRunJobID() {
        return runJobID;
    }

    /**
     * Setter of run job id.
     * @param runJobID New value of run job id.
     */
    public void setRunJobID(Long runJobID) {
        this.runJobID = runJobID;
    }

    /**
     * Get configuration of all steps.
     * @return List of configuration of all steps.
     */
    public List<StepConfData> getStepConfData() {
        return stepConfData;
    }
    
    /**
     * Get data of finished task.
     * @return Data of finished task.
     */
    public HistoryTaskData getHistoryTaskData() {
        return historyTaskData;
    }
}
