/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses.interfaces;

import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;

/**
 * Interface for data to be filtered.
 * @author filip
 */
public interface FilterInterface extends SortInterface {

    public String getTaskName();
    public Long getRunningTimeInHours();
    public Long getRunningTimeInDays();
    public StepStatusEnum getTaskStatus();
}
