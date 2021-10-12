/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.HistoryTasks;

import com.app.primaryNodeApp.controllers.backingBeans.AbstractBeans.GeneralTaskBean;
import com.app.primaryNodeApp.services.dataClasses.HistoryTaskData;
import com.app.primaryNodeApp.services.HistoryTasServices.HistoryTasksService;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * The backing bean for history task overview.
 * @author filip
 */

@ManagedBean
@ViewScoped
public class HistoryOfTasksBean extends GeneralTaskBean<HistoryTaskData, HistoryTasksService> implements Serializable{

    /** OBJECT METHODS **/
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.setService(new HistoryTasksService());
        this.loadData();
    }

}
