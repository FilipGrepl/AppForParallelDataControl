/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import java.io.Serializable;
import java.util.Date;

/**
 * The historical data of task.
 * @author filip
 */
public class HistoryTaskData extends TaskData implements Serializable {
    
    /** OBJECT PROPERTIES **/
    
    private Date finishedAt;                    // date, when the task is finished    
    private Long execTime;                      // executing time of task
    private Long waitingTime;                   // waiting time of task
    
    /** OBJECT METHODS **/
    
    public HistoryTaskData() {
        this.finishedAt = null;
        this.execTime = null;
        this.waitingTime = null;
    }
    
     /** GETTERS AND SETTERS **/
    
    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getExecTimeStr() {
        return CommonRunService.getTimeStr(this.execTime);
    }
    
    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public String getWaitingTimeStr() {
        return CommonRunService.getTimeStr(this.waitingTime);
    }

    public void setWaitingTime(Long waitingTime) {
        this.waitingTime = waitingTime;
    }
    
    @Override
    public Integer getNumberOfServersToFilterAndSort() {
        return getNumberOfServers();
    }
}
