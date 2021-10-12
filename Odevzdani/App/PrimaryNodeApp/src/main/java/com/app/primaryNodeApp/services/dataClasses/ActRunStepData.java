/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.Serializable;

/**
 * The data of actual run steps.
 * @author filip
 */
public class ActRunStepData implements Serializable  {

    /** OBJECT PROPERTIES **/
    
    private String stepName;
    private Integer stepOrder;
    private Integer numberOfSteps;

    private Integer numberOfFinishedServers;
    private Integer numberOfServers;
    private Integer numberOfRunningServers;

    private StepStatusEnum stepStatus;
    private String statusClass;

    /** GETTERS AND SETTERS **/
    
    public String getStatusClass() {
        return statusClass;
    }

    public void setStatusClass(String statusClass) {
        this.statusClass = statusClass;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public Integer getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(Integer numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public Integer getNumberOfFinishedServers() {
        return numberOfFinishedServers;
    }

    public void setNumberOfFinishedServers(Integer numberOfFinishedServers) {
        this.numberOfFinishedServers = numberOfFinishedServers;
    }

    public Integer getNumberOfServers() {
        return numberOfServers;
    }

    public void setNumberOfServers(Integer numberOfServers) {
        this.numberOfServers = numberOfServers;
    }

    public Integer getNumberOfRunningServers() {
        return numberOfRunningServers;
    }

    public void setNumberOfRunningServers(Integer numberOfRunningServers) {
        this.numberOfRunningServers = numberOfRunningServers;
    }

    public StepStatusEnum getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatusEnum stepStatus) {
        this.stepStatus = stepStatus;
    }
    
    public String getStepStatusStr() {
        return this.stepStatus.getMessage();
    }

    public String getStepOrderStr() {
        return this.stepOrder + " / " + this.numberOfSteps;
    }

    public double getFinishedPercentage() {
        return (double) this.numberOfFinishedServers / this.numberOfServers * 100;
    }

    public String getFinishedPercentageStr() {
        return (int) getFinishedPercentage() + " %";
    }

    public String getRunningAtStr() {
        return this.numberOfRunningServers + " / " + this.numberOfServers;
    }
}
