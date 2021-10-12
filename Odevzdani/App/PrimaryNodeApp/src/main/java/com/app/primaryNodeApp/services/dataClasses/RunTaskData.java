/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import java.io.Serializable;

/**
 * The data of running task.
 * @author filip
 */
public class RunTaskData extends TaskData implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Integer numberOfRunningServers;

    private String lastSuccStep;

    private String statusClass;                 // class to set color of displayed status

    private Integer finishedSteps;
    private Integer allSteps;
    
    /** OBJECT METHODS **/
    
    public RunTaskData() {
        this.numberOfRunningServers = 0;
        this.lastSuccStep = null;
        this.statusClass = null;
        this.finishedSteps = 0;
        this.allSteps = 0;
    }    
    
     /** GETTERS AND SETTERS **/
   
    public Integer getNumberOfRunningServers() {
        return numberOfRunningServers;
    }

    public void setNumberOfRunningServers(Integer numberOfRunningServers) {
        this.numberOfRunningServers = numberOfRunningServers;
    }

    public String getLastSuccStep() {
        return lastSuccStep;
    }

    public void setLastSuccStep(String lastSuccStep) {
        this.lastSuccStep = lastSuccStep;
    }

    public String getStatusClass() {
        return statusClass;
    }
    
     public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }

    public Integer getFinishedSteps() {
        return finishedSteps;
    }

    public void setFinishedSteps(Integer finishedSteps) {
        this.finishedSteps = finishedSteps;
    }

    public Integer getAllSteps() {
        return allSteps;
    }

    public void setAllSteps(Integer allSteps) {
        this.allSteps = allSteps;
    } 

    /**
     * Getter of ratio of running servers to all servers as string.
     * @return Ratio of running servers to all servers as string.
     */
    public String getRunAtStr() {        
        return CommonRunService.getRatioStr(this.numberOfRunningServers.toString(), this.getNumberOfServers().toString());
    }

    /**
     * Getter of ratio of finished steps to all steps as string.
     * @return Ratio of finished steps to all steps as string.
     */
    public String getStepsStr() {
        return CommonRunService.getRatioStr(this.finishedSteps.toString(), this.allSteps.toString());
    }

    public double getStepsPercentage() {
        return ((double) this.finishedSteps) / this.allSteps * 100;
    } 
    
    @Override
    public Integer getNumberOfServersToFilterAndSort() {
        return this.numberOfRunningServers;
    }   
}
