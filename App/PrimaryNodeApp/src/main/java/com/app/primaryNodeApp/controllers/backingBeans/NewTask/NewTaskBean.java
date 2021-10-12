/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.NewTask;

import com.app.primaryNodeApp.controllers.backingBeans.AbstractBeans.GeneralNewTaskBean;
import com.app.primaryNodeApp.services.dataClasses.interfaces.EnumInterface;
import com.app.primaryNodeApp.services.NewTaskServices.NewTaskService;
import com.app.primaryNodeApp.services.SavedTaskServices.SavedTaskJobSerializatorService;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.services.JobService.JobService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.event.FileUploadEvent;

/**
 * The backing bean for new task form.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class NewTaskBean extends GeneralNewTaskBean implements Serializable {
    
    /** STATIC PROPERTIES **/

    public static enum ConfigTypesEnum implements EnumInterface {
        OWN("Vlastní"),
        LOAD_EXIST_TASK("Načíst uloženou konfiguraci úlohy"),
        LOAD_FROM_FILE("Načíst konfiguraci ze souboru");

        private final String message;

        ConfigTypesEnum(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
    
    /** OBJECT PROPERTY **/
    
    // job
    private List<SelectItem> existJobs; // selectOneMenu exist jobs
    private Long selectedJobID;

    // config type
    private ConfigTypesEnum configType;
    
    private final RunJobService runJobService;
    private final JobService jobService;
    
    
    /** OBJECT METHODS **/
    
    /**
     *  Constructor.
     */
    public NewTaskBean() {
        runJobService = new RunJobService();
        jobService = new JobService();
    }
    
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {

        this.configType = ConfigTypesEnum.OWN;
        this.initAll();
    }

    /**
     * Method, which initializes all form items to default values.
     */
    private void initAll() {
        pickListBean.initDualList();
        this.existJobs = new ArrayList<>();
        this.selectedJobID = null;
        this.jobName = null;

        stepsValues = NewTaskService.getInitList();

        jobService.getAll().forEach(job -> {
            this.existJobs.add(new SelectItem(job.getId(), job.getJobName()));
        });

        this.jobName = null;
    }

    /**
     * Getter of list of all existing jobs in application.
     * @return The list of all existing jobs in application.
     */
    public List<SelectItem> getExistJobs() {
        return existJobs;
    }

    /**
     * Getter of configuration options.
     * @return The list of select items of all configuration options.
     */
    public List<SelectItem> getConfigOptions() {
        return generalValues(ConfigTypesEnum.values());
    }

    /**
     * Getter of selected config type.
     * @return Selected config type.
     */
    public ConfigTypesEnum getConfigType() {
        return configType;
    }

    /***
     * Setter of config type.
     * @param configType Nea value of config type
     */
    public void setConfigType(ConfigTypesEnum configType) {
        this.configType = configType;
    }

    /**
     * Getter of selected job id.
     * @return The id of selected job based on the items in the form are set.
     */
    public Long getSelectedJobID() {
        return selectedJobID;
    }

    /**
     * Setter of selected job id
     * @param selectedJobID New value of selected job id.
     */
    public void setSelectedJobID(Long selectedJobID) {
        this.selectedJobID = selectedJobID;
    }

    /**
     * Handler of change event on selectOneMenu with all task in system.
     */
    public void changeSelectedJobHandler() {
        this.setDataInForm(this.selectedJobID);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dokončeno", "Načtení konfigurace úlohy proběhlo úspěšně"));
    }

    

    /**
     * Handler of upload event which set all items in form based on configuration file.
     * @param event Event which contains the configuration file.
     * @throws IOException In an IOException occurs.
     */
    public void uploadHandler(FileUploadEvent event) throws IOException {

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(event.getFile().getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        try {
            Job deserializedJob = SavedTaskJobSerializatorService.deserializeJob(textBuilder.toString());

            this.jobName = deserializedJob.getJobName();
            stepsValues = NewTaskService.getJobStepValuesList(deserializedJob);
            pickListBean.setDualListByJob(deserializedJob);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Dokončeno", "Nahrání konfigurace úlohy proběhlo úspěšně"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Chyba", "Nahrání konfigurace úlohy neproběhlo úspěšně. Chyba: " + e.getMessage()));
        }

    }

    /**
     * Handler of change event of selectOneRadio with configuration options.
     */
    public void taskConfOptionHandler() {
        this.initAll();
    }

    /**
     * Method which persists new task.
     * @return The Job, which is saved.
     */
    private Job saveTask() {
        List<Step> jobSteps = NewTaskService.getStepsToSave(stepsValues);
        List<Node> jobNodes = this.pickListBean.getJobNodes();
        return jobService.saveNewJob(jobName, jobSteps, jobNodes);
    }
    
    /** OVERRIED ABSTRACT METHODS OF SUPER CLASS **/
    
    /**
     * Method which is called, after URL parameters is processed
     */
    @Override
    public void loadData() {
        if (this.jobID != null) {
            if (jobService.getById(jobID) == null) {
                throw new RuntimeException("Úloha s ID " + jobID + " neexistuje.");
            }
            this.selectedJobID = this.jobID;
            this.configType = ConfigTypesEnum.LOAD_EXIST_TASK;
            this.setDataInForm(jobID);
        }
    }
    
    /**
     * Handler of click event to save button
     * @throws IOException If an IOException occurs.
     */
    @Override
    public void persistTaskHandler() throws IOException {
        this.saveTask();
        FacesContext.getCurrentInstance().getExternalContext().redirect("savedTasksOptions.xhtml?i=3&faces-redirect=true");
    }

    /**
     * Handler of click event to run and save button
     * @throws IOException If an IOException occurs.
     */
    @Override
    public void persistAndRunTaskHandler() throws IOException {
        Job job = this.saveTask();
        RunJob runJob = runJobService.saveNewRunJob(job.getId());
        primaryNode.startJob(job.getId());
        FacesContext.getCurrentInstance().getExternalContext().redirect("runningTasks.xhtml?i=0&faces-redirect=true");
    }
}
