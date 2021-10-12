/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses.interfaces;

import java.util.Date;

/**
 * Interface for data to be sorted.
 * @author filip
 */
public interface SortInterface {
    public Date getStartedAt();
    public Long getRunningTime();
    public Integer getNumberOfServersToFilterAndSort();    
}
