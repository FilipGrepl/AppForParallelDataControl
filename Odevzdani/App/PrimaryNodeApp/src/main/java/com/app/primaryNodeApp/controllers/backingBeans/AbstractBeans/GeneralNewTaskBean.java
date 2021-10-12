/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.AbstractBeans;

import com.app.primaryNodeApp.services.dataClasses.interfaces.EnumInterface;
import com.app.primaryNodeApp.controllers.backingBeans.PickListBean.PickListBean;
import com.app.primaryNodeApp.services.dataClasses.NewTaskStepValues;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.services.NewTaskServices.NewTaskService;
import com.app.primaryNodeApp.model.database.enums.IOtypes;
import com.app.primaryNodeApp.model.primaryNode.PrimaryNode;
import com.app.primaryNodeApp.services.JobService.JobService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.primefaces.event.TabChangeEvent;

/**
 * Class, which implements common functionality for creating a new task and editing an existing task.
 * @author filip
 */
public abstract class GeneralNewTaskBean implements Serializable {

    /** STATIC PROPERTIES **/
    
    public static final String NEW_STEP_LABEL = "Přidat krok";
    
    /** OBJECT PROPERTIES **/

    protected Long jobID;

    // entered steps values
    protected List<NewTaskStepValues> stepsValues;

    // job name
    protected String jobName;
    
    private final JobService jobService;

    @Inject
    protected PickListBean pickListBean;

    @Inject
    protected PrimaryNode primaryNode;

    /** OBJECT METHODS **/
    
    public GeneralNewTaskBean() {
        jobService = new JobService();
    }
    
    /**
     * Getter of job ID.
     * @return job ID.
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
     * Getter of job name.
     * @return The job name.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Setter of job name.
     * @param jobName The new job name.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Getter of steps values.
     * @return The list of all steps of the task.
     */
    public List<NewTaskStepValues> getStepsValues() {
        return stepsValues;
    }

    /**
     * Setter of steps values.
     * @param stepsValues New list of steps values of the task.
     */
    public void setStepsValues(List<NewTaskStepValues> stepsValues) {
        this.stepsValues = stepsValues;
    }

    /**
     * Getter of pickListBean.
     * @return The pickListBean instance.
     */
    public PickListBean getPickListBean() {
        return pickListBean;
    }

    /**
     * Getter of all input type values.
     * @return The list of select items of all input type values.
     */
    public List<SelectItem> getInputTypeValues() {
        List<SelectItem> IOvalues = new ArrayList<>();
        for (IOtypes.IOtypesEnum IOtype : IOtypes.IOtypesEnum.values()) {
            IOvalues.add(new SelectItem(IOtype.toString(), IOtype.getMessage()));
        }
        return IOvalues;
    }
    
     /**
     * Getter of all output type values.
     * @param stepIndex The index of step in list of steps for which the output type to be displayed.
     * @return The list of select items of all output type values.
     */
    public List<SelectItem> getOutputTypeValues(int stepIndex) {
        IOtypes.IOtypesEnum inputType = this.stepsValues.get(stepIndex).getStep().getInputType();        
        List<SelectItem> Ovalues = new ArrayList<>();
        
        if (inputType != null) {
            for (IOtypes.IOtypesEnum Otype: IOtypes.getOutputTypes(inputType)) {
                Ovalues.add(new SelectItem(Otype.toString(), Otype.getMessage()));
            }
        }
        return Ovalues;
    }

    /**
     * Getter of time units for max processing time of each input fs node.
     * @return The list of select items of all time units values.
     */
    public List<SelectItem> getTimeoutUnitValues() {
        return generalValues(NewTaskStepValues.TimeoutUnitEnum.values());
    }

    /**
     * Getter of log size units for max log size specification.
     * @return The list of select items of all log size unit values.
     */
    public List<SelectItem> getLogSizeUnitValues() {
        return generalValues(NewTaskStepValues.LogSizeUnitEnum.values());
    }

    /**
     * Method, which return the list of select items for any class which implements EnumInterface.
     * @param <T> Class which imlements EnumInterface.
     * @param enumValues Array of classes which implements EnumInterface.
     * @return The list of select items - one item for each item in enumValues array.
     */
    public static <T extends EnumInterface> List<SelectItem> generalValues(T[] enumValues) {
        List<SelectItem> selectItems = new ArrayList<>();
        for (T item : enumValues) {
            selectItems.add(new SelectItem(item.toString(), item.getMessage()));
        }
        return selectItems;
    }

    /**
     * Handler of tabChange event on tabView of all steps.
     * @param event The event which contains the tab on which the event was created.
     */
    public void tabChangeHandler(TabChangeEvent event) {
        if (event.getTab().getTitle().equals(NEW_STEP_LABEL)) {
            NewTaskService.addNewStep(stepsValues);
        }
    }

    /**
     * Handler of click event on remove step button
     * @param stepValues Values of step to be removed.
     */
    public void removeStepHandler(NewTaskStepValues stepValues) {
        this.stepsValues.remove(stepValues);
        for (int cnt = 0; cnt < stepsValues.size(); cnt++) {
            if (cnt == stepsValues.size() - 1) {
                stepsValues.get(cnt).getStep().setStepOrder(0);
            } else {
                stepsValues.get(cnt).getStep().setStepOrder(cnt + 1);
            }
        }
    }
    
    /**
     * Set values of all items in the form according to the specific task.
     * @param jobID The id of task based on the items in the form are set.
     */
    protected void setDataInForm(Long jobID) {
         if (jobID == null) {
            this.stepsValues = NewTaskService.getInitList();
            this.jobName = null;
            pickListBean.initDualList();
        } else {
            Job job = jobService.getByIdWithCollections(jobID);
            this.stepsValues = NewTaskService.getJobStepValuesList(job);            
            this.jobName = job.getJobName();            
            pickListBean.setDualListByJob(job);
        }
    }
    
    /** ABSTRACT METHODS **/
    
    /**
     * Abstract method for loading task data.
     */
    public abstract void loadData();

    /**
     * Abstract method for saving task.
     * @throws IOException If an IOException occurs.
     */
    public abstract void persistTaskHandler() throws IOException;

    /**
     * Abstract method for saving and running task.
     * @throws IOException If an IO exception occurs.
     */
    public abstract void persistAndRunTaskHandler() throws IOException;
}
