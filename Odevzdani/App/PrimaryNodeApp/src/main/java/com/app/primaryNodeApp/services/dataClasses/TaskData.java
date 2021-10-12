/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.dataClasses.interfaces.FilterInterface;
import static com.app.primaryNodeApp.services.dataClasses.RunTaskData.DAY;
import static com.app.primaryNodeApp.services.dataClasses.RunTaskData.HOURS;
import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.util.Date;
import com.app.primaryNodeApp.services.dataClasses.interfaces.SortInterface;
import java.io.Serializable;

/**
 * The common data for run/history/saved data.
 * @author filip
 */
public abstract class TaskData implements SortInterface, FilterInterface, Serializable  {
    
    /** STATIC PROPERTIES **/

    public static final long DAY = 60 * 60 * 24 * 1000;     // milliseconds in one day
    public static final long HOURS = 60 * 60 * 1000;        // millliseconds in one hour
    public static final long MINUTES = 60 * 1000;           // milliseconds in one minutes
    public static final long HOURS_IN_DAY = 24;
    
    /** OBJECT PROPERTIES **/
    
    private String taskName;                    
    private long runJobID;                        

    private Date startedAt;                     
    private Integer numberOfServers;            

    private Long runningTime;                   

    private StepStatusEnum taskStatus;          

    /** OBJECT METHODS **/
    
    public TaskData() {
        this.taskName = null;
        this.runJobID = -1;
        this.startedAt = null;
        this.numberOfServers = 0;
        this.runningTime = null;
        this.taskStatus = null;
    }
    
    /** GETTERS AND SETTERS **/
    
    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getRunJobID() {
        return runJobID;
    }

    public void setRunJobID(long runJobID) {
        this.runJobID = runJobID;
    }

    @Override
    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Integer getNumberOfServers() {
        return numberOfServers;
    }

    public void setNumberOfServers(Integer numberOfServers) {
        this.numberOfServers = numberOfServers;
    }

    @Override
    public Long getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(Long runningTime) {
        this.runningTime = runningTime;
    }

    @Override
    public StepStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(StepStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    /**
     * Getter of running time converted to string.
     * @return String with converted running time.
     */
    public String getRunningTimeStr() {
        return CommonRunService.getTimeStr(runningTime);
    }

    @Override
    public Long getRunningTimeInDays() {
        return this.runningTime / DAY;
    }

    @Override
    public Long getRunningTimeInHours() {
        return this.runningTime / HOURS;
    }
}
