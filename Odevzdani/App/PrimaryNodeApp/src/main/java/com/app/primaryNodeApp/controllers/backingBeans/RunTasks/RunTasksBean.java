/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.RunTasks;

import com.app.primaryNodeApp.controllers.backingBeans.AbstractBeans.GeneralTaskBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskService;
import java.io.Serializable;

/**
 * The backing bean for run task overview.
 * @author filip
 */

@ManagedBean
@ViewScoped
public class RunTasksBean extends GeneralTaskBean<RunTaskData, RunTaskService> implements Serializable {
    
    /** OBJECT METHODS **/
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.setService(new RunTaskService());
        this.loadData();
    }
}
