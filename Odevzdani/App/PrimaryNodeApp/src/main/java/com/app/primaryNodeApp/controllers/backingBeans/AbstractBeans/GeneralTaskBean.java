/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.AbstractBeans;

import com.app.primaryNodeApp.controllers.backingBeans.FilterBean.FilterBean;
import com.app.primaryNodeApp.services.dataClasses.interfaces.FilterInterface;
import com.app.primaryNodeApp.services.dataClasses.interfaces.TaskDataInterface;
import com.app.primaryNodeApp.controllers.backingBeans.SortBean.SortBean;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Class which implements the common functionality for overview of run, history and saved tasks.
 * @author filip
 * @param <T> T specify type of displayed data.
 * @param <M> M specify type of service, which loads task data of type T.
 */
public abstract class GeneralTaskBean<T extends FilterInterface, M extends TaskDataInterface<T>> implements Serializable {
    
    /** STATIC METHODS **/
    public static String DATABASE_INTERNAL_ERROR_MSG = "Interní chyba - při zpracování požadavku v databázi došlo k chybě.";

    /** OBJECT PROPERTIES **/
    
    // service for loading data
    private M service;
    
    // loaded data
    private List<T> tasksData;
    // filtered data
    private List<T> tasksDataToDisplay;

    @Inject
    private FilterBean filterBean;

    @Inject
    private SortBean sortBean;
    
    /** OBJECT METHODS **/
   
    /**
     * Setter of service.
     * @param service New service for loading data.
     */
    protected void setService(M service) {
        this.service = service;
    }
    
    /**
     * The method, which is called after service is set for loading data
     */
    public void loadData() {
        
        this.tasksData = service.getTasksData();

        this.tasksDataToDisplay = new ArrayList<>();
        this.tasksDataToDisplay.addAll(this.tasksData);

        this.ajaxSortHandler();
    }

    /**
     * Getter of filtered data.
     * @return Filtered task data.
     */
    public List<T> getTasksDataToDisplay() {
        return tasksDataToDisplay;
    }

    /**
     * Getter of filter bean.
     * @return Instance of filter bean.
     */
    public FilterBean getFilterBean() {
        return filterBean;
    }

    /**
     * Getter of sort bean.
     * @return Instance of sort bean.
     */
    public SortBean getSortBean() {
        return sortBean;
    }

    /**
     * Handler of change event of filter parameters.
     */
    public void ajaxFilterHandler() {
        this.tasksDataToDisplay.clear();
        this.tasksDataToDisplay.addAll(this.tasksData);
        this.filterBean.filterData(this.tasksDataToDisplay);
        this.ajaxSortHandler();
    }

    /**
     * Handler of change event of sort selectOneButton.
     */
    public void ajaxSortHandler() {
        this.sortBean.sortData(this.tasksDataToDisplay);
    }

    /**
     * Handler of click event of button for deleting all filters.
     */
    public void deleteFilterOnClick() {
        this.filterBean.clearFilters();
        this.tasksDataToDisplay.clear();
        this.tasksDataToDisplay.addAll(this.tasksData);
        this.ajaxSortHandler();
    }
    
    /**
     * Update task data when server push event is received on client side.
     */
    public void update() {
        this.tasksData = service.getTasksData();
        this.ajaxFilterHandler();
    }
}
