/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.dataClasses.interfaces.FilterInterface;
import com.app.primaryNodeApp.services.dataClasses.interfaces.SortInterface;
import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.Serializable;
import java.util.Date;

/**
 * The data of saved task.
 * @author filip
 */
public class SavedTaskData implements SortInterface, FilterInterface, Serializable {
    
    /** OBJECT PROPERTIES **/
    
    private long jobID;
    private String taskName;
    private Date lastStartedAt;
    private Integer numberOfServers;
    private Integer numberOfSteps;
    private Long runningTime;
    private StepStatusEnum taskStatus;
    private String statusClass;
    
    /** OBJECT METHODS **/
    
    public SavedTaskData() {
        this.jobID = -1;
        this.taskName = null;
        this.lastStartedAt = null;
        this.numberOfServers = null;
        this.numberOfSteps = null;
        this.runningTime = null;
        this.taskStatus = null;
        this.statusClass = null;
    }
    
    /** GETTERS AND SETTERS **/

    public long getJobID() {
        return jobID;
    }

    public void setJobID(long jobID) {
        this.jobID = jobID;
    }   

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getLastStartedAt() {
        return lastStartedAt;
    }

    public void setLastStartedAt(Date lastStartedAt) {
        this.lastStartedAt = lastStartedAt;
    }
    
    public Integer getNumberOfServers() {
        return numberOfServers;
    }

    public void setNumberOfServers(Integer numberOfServers) {
        this.numberOfServers = numberOfServers;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }
        
    @Override
    public Integer getNumberOfServersToFilterAndSort() {
        return this.numberOfServers;
    }
    
    @Override
    public Date getStartedAt() {
        return this.lastStartedAt;
    }

    public void setRunningTime(Long runningTime) {
        this.runningTime = runningTime;
    }
    
    @Override
    public Long getRunningTime() {
        return runningTime;
    }
    
    /**
     * Getter of running time converted to string.
     * @return String with converted running time.
     */
    public String getRunningTimeStr() {
        if (this.runningTime == null)
            return null;
        else
            return CommonRunService.getTimeStr(this.runningTime);
    }

    public Integer getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(Integer numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }
    
    @Override
    public StepStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(StepStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }
    
    /**
     * Getter of running time in days for interface implementation.
     * @return null.
     */
    @Override
    public Long getRunningTimeInDays() {
        return null;
    }
    
     /**
     * Getter of running time in days for interface implementation.
     * @return null.
     */
    @Override
    public Long getRunningTimeInHours() {
        return null;
    }
}
