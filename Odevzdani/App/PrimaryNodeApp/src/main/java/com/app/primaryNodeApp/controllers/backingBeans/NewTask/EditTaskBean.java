/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.NewTask;

import com.app.primaryNodeApp.controllers.backingBeans.AbstractBeans.GeneralNewTaskBean;
import com.app.primaryNodeApp.services.NewTaskServices.NewTaskService;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.services.JobService.JobService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * The backing bean for editing form of existing task.
 * @author filip
 */

@ViewScoped
@ManagedBean
public class EditTaskBean extends GeneralNewTaskBean implements Serializable {
    
    /** OBJECT PROPERTIES **/
    private final RunJobService runJobService;
    private final JobService jobService;
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor
     */
    public EditTaskBean() {
        runJobService = new RunJobService();
        jobService = new JobService();
    }
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.jobName = null;
        this.stepsValues = null;
        this.jobName = null;
    }
    
    /**
     * Method, which is called when URL parameters(jobID) are processed.
     */
    @Override
    public void loadData() {
        if (this.jobID != null) {
            if (jobService.getById(jobID) == null) {
                throw new RuntimeException("Úloha s ID " + jobID + " neexistuje.");
            }
            this.setDataInForm(jobID);
        }
    }
    
    /**
     * Method which persists changes in configuration of task.
     * @return The updated job object.
     */
    protected Job updateTask() {
        List<Step> jobSteps = NewTaskService.getStepsToSave(stepsValues);
        List<Node> jobNodes = this.pickListBean.getJobNodes();
        return jobService.updateJob(jobID, jobName, jobSteps, jobNodes);
    }
    
    /**
     * Handler of click event to update button.
     * @throws IOException If an IOException occurs.
     */
    @Override
    public void persistTaskHandler() throws IOException {
        this.updateTask();
        FacesContext.getCurrentInstance().getExternalContext().redirect("savedTaskDetail.xhtml?i=3&jobID="+this.jobID+"&faces-redirect=true");
    }

    /**
     * Handler of click event to update and run button.
     * @throws IOException If an IOException occurs.
     */
    @Override
    public void persistAndRunTaskHandler() throws IOException {
        Job job = this.updateTask();
        RunJob runJob = runJobService.saveNewRunJob(job.getId());
        primaryNode.startJob(job.getId());
        FacesContext.getCurrentInstance().getExternalContext().redirect("runningTasks.xhtml?i=0&faces-redirect=true");
    }
}
