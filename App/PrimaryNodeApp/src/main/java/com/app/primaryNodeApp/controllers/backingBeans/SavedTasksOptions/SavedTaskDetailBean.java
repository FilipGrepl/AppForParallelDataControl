/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.SavedTasksOptions;

import com.app.primaryNodeApp.services.dataClasses.SavedTaskData;
import com.app.primaryNodeApp.services.dataClasses.StepConfData;
import com.app.primaryNodeApp.services.RunTaskServices.RunTaskConfService;
import com.app.primaryNodeApp.services.SavedTaskServices.SavedTaskService;
import com.app.primaryNodeApp.services.SavedTaskServices.SavedTaskJobSerializatorService;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.primaryNode.PrimaryNode;
import com.app.primaryNodeApp.services.JobService.JobService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * The backing bean for page of saved task detail.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class SavedTaskDetailBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long jobID;

    private SavedTaskData savedTaskData;
    private StreamedContent exportedFile;
    
    private List<Node> jobNodes;
    private List<StepConfData> stepConfData;
    
    @Inject
    PrimaryNode primaryNode;
    
    private final RunJobService runJobService;
    private final JobService jobService;
    
    
    /** OBJECT METHODS **/
    
    /**
     *  Constructor.
     */
    public SavedTaskDetailBean() {
        runJobService = new RunJobService();
        jobService = new JobService();
    }
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
    }

    /**
     * Method, which is called when URL parameters(jobID) are processed.
     */
    public void loadData() {
        
        Job job = jobService.getByIdWithCollections(jobID);
        if (job == null) {
            throw new RuntimeException ("Úloha s ID " + jobID + " neexistuje.");
        }
        this.jobNodes = job.getJobNodes();
        this.stepConfData = RunTaskConfService.getStepConfData(jobID);
        this.savedTaskData = new SavedTaskService().getSavedTaskData(jobID);
        this.exportedFile = DefaultStreamedContent.builder()
                .contentType("text/plain")
                .name(job.getJobName()+"_configuration.json")
                .stream(() -> new ByteArrayInputStream(SavedTaskJobSerializatorService.serializeJob(job).getBytes()))
                .build();
    }

    /**
     * Getter of jobID.
     * @return JobID.
     */
    public Long getJobID() {
        return jobID;
    }

    /**
     * Setter of jobID.
     * @param jobID New value of jobID.
     */
    public void setJobID(Long jobID) {
        this.jobID = jobID;
    }

    /**
     * Getter of saved task data.
     * @return Data of saved task.
     */
    public SavedTaskData getSavedTaskData() {
        return savedTaskData;
    }

    /**
     * Getter of all job secondary nodes.
     * @return List of all job secondary nodes.
     */
    public List<Node> getJobNodes() {
        return jobNodes;
    }
    
    /**
     * Getter of all steps configuration data.
     * @return List of all steps configuration data.
     */
    public List<StepConfData> getStepConfData() {
        return stepConfData;
    }

    /**
     * Getter of content of file with task configuration.
     * @return Cotnent of file with task configuration.
     */
    public StreamedContent getExportedFile() {
        return exportedFile;
    }

    /**
     * Handler of click event on delete task button.
     * @throws IOException If an IOException occurs.
     */
    public void deleteTaskHandler() throws IOException {        
        try {
            jobService.delete(jobID);
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Chyba", e.getMessage()));
            return;
        }        
        FacesContext.getCurrentInstance().getExternalContext().redirect("savedTasksOptions.xhtml?i=3");
        
    }

    /**
     * Handler of click event on run task button.
     * @throws IOException If an IOException occurs.
     */
    public void runTaskHandler() throws IOException {
        // check if task isn't already running
        if (!jobService.isRunningTask(jobID)) {
            RunJob runJob =  runJobService.saveNewRunJob(jobID);
            primaryNode.startJob(jobID);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Úloha " + jobService.getById(jobID).getJobName() + " byla spuštěna"));
            this.loadData();
        }
    }
    
    /**
     * Update task data when server push event is received on client side.
     */
    public void update() {
        this.savedTaskData = new SavedTaskService().getSavedTaskData(jobID);
    }
}
