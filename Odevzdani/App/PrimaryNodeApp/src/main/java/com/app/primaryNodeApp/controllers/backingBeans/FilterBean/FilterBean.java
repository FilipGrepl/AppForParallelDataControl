/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.FilterBean;

import com.app.primaryNodeApp.services.dataClasses.interfaces.FilterInterface;
import com.app.primaryNodeApp.services.dataClasses.TaskData;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * The backing bean for filtering task overview data.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class FilterBean implements Serializable {

    /** OBJECT PROPERTIES **/
    
    // filtering
    private String taskName;

    private Date dateFrom;
    private Date dateTo;

    private Integer execDaysFrom;
    private Integer execDaysTo;
    private Integer execHoursFrom;
    private Integer execHoursTo;

    private Integer minLoadServer;
    private Integer maxLoadServer;

    private Integer minLoadServerI;
    private Integer maxLoadServerI;

    //private String whichSliderIs;

    private boolean isCheckedRunning;
    private boolean isCheckedRunningError;
    private boolean isCheckedError;
    private boolean isCheckedWaiting;
    private boolean isCheckedPaused;
    
    /** OBJECT METHODS **/

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.taskName = null;
        this.dateFrom = null;
        this.dateTo = null;
        this.execDaysFrom = null;
        this.execDaysTo = null;
        this.execHoursFrom = null;
        this.execHoursTo = null;
        
        this.minLoadServer = this.minLoadServerI = 0;
        this.maxLoadServer = this.maxLoadServerI = 100;
        
        this.isCheckedRunning = false;
        this.isCheckedRunningError = false;
        this.isCheckedError = false;
        this.isCheckedWaiting = false;
        this.isCheckedPaused = false;
    }

    /**
     * Method, which filters data based on filter settings.
     * @param tasksDataToDisplay Data to be filtered.
     */
    public void filterData(List<? extends FilterInterface> tasksDataToDisplay) {
        if (this.taskName != null) {
            tasksDataToDisplay.removeIf(task -> !task.getTaskName().toLowerCase().contains(this.taskName.toLowerCase()));
        }

        if (this.dateFrom != null) {
            tasksDataToDisplay.removeIf(task -> (task.getStartedAt() == null || !task.getStartedAt().after(this.dateFrom)));
        }

        if (this.dateTo != null) {
            tasksDataToDisplay.removeIf(task -> (task.getStartedAt() == null || !task.getStartedAt().before(this.dateTo)));
        }

        if (this.minLoadServerI != null) {
            tasksDataToDisplay.removeIf(task -> task.getNumberOfServersToFilterAndSort() < this.minLoadServerI);
        }

        if (this.maxLoadServerI != null) {
            tasksDataToDisplay.removeIf(task -> task.getNumberOfServersToFilterAndSort() > this.maxLoadServerI);
        }

        if (this.execDaysFrom != null) {
            tasksDataToDisplay.removeIf(task -> {
                long execFrom = this.execDaysFrom * TaskData.HOURS_IN_DAY;
                if (this.execHoursFrom != null) {
                    execFrom += this.execHoursFrom;
                }
                return task.getRunningTimeInHours() < execFrom;
            });
        }

        if (this.execHoursFrom != null && this.execDaysFrom == null) {
            tasksDataToDisplay.removeIf(task -> task.getRunningTimeInHours() < this.execHoursFrom);
        }

        if (this.execDaysTo != null) {
            tasksDataToDisplay.removeIf(task -> {
                long execTo = this.execDaysTo * TaskData.HOURS_IN_DAY;
                if (this.execHoursTo != null) {
                    execTo += this.execHoursTo;
                }
                return task.getRunningTimeInHours() > execTo;
            });
        }

        if (this.execHoursTo != null & this.execDaysTo == null) {
            tasksDataToDisplay.removeIf(task -> task.getRunningTimeInHours() > this.execHoursTo);
        }

        if (this.isCheckedRunning || this.isCheckedRunningError || this.isCheckedError || this.isCheckedWaiting || this.isCheckedPaused) {
            tasksDataToDisplay.removeIf(task -> {
                if (this.isCheckedRunning && task.getTaskStatus() == StepStatusEnum.RUNNING) {
                    return false;
                }
                if (this.isCheckedRunningError && task.getTaskStatus() == StepStatusEnum.RUNNING_ERROR) {
                    return false;
                }
                if (this.isCheckedError && task.getTaskStatus() == StepStatusEnum.ERROR) {
                    return false;
                }
                if (this.isCheckedWaiting && task.getTaskStatus() == StepStatusEnum.WAITING) {
                    return false;
                }
                if (this.isCheckedPaused && task.getTaskStatus() == StepStatusEnum.PAUSED) {
                    return false;
                }
                return true;
            });
        }
    }

    /**
     * Method which set filter settings to default values.
     */
    public void clearFilters() {
        this.taskName = null;
        this.dateFrom = null;
        this.dateTo = null;
        this.execDaysFrom = null;
        this.execDaysTo = null;
        this.execHoursFrom = null;
        this.execHoursTo = null;
        this.minLoadServer = this.minLoadServerI = 0;
        this.maxLoadServer = this.maxLoadServerI = 100;
        this.isCheckedRunning = false;
        this.isCheckedRunningError = false;
        this.isCheckedError = false;
        this.isCheckedWaiting = false;
        this.isCheckedPaused = false;

    }

    /**
     * Getter of filter task name.
     * @return Filter task name.
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Setter of filter task name.
     * @param taskName New value of filter task name.
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Getter of filter date from which the task started.
     * @return Filter date from which the task started.
     */
    public Date getDateFrom() {
        return dateFrom;
    }

    /**
     * Setter of filter date from which the task started.
     * @param dateFrom New value of filter date from which the task started.
     */
    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * Getter of filter date to which the task started.
     * @return Filter date to which the task started.
     */
    public Date getDateTo() {
        return dateTo;
    }

    /**
     * Setter of filter date to which the task started.
     * @param dateTo New value of filter date to which the task started.
     */
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * Getter of filter execute days from.
     * @return Filter execute days from.
     */
    public Integer getExecDaysFrom() {
        return execDaysFrom;
    }

    /**
     * Setter of filter execute days from.
     * @param execDaysFrom New value of execute days from.
     */
    public void setExecDaysFrom(Integer execDaysFrom) {
        this.execDaysFrom = execDaysFrom;
    }

    /**
     * Getter of filter execute days to.
     * @return Filter execute days to.
     */
    public Integer getExecDaysTo() {
        return execDaysTo;
    }

    /**
     * Setter of filter execute days to.
     * @param execDaysTo New value of execute days to.
     */
    public void setExecDaysTo(Integer execDaysTo) {
        this.execDaysTo = execDaysTo;
    }

    /**
     * Getter of filter execute hours from.
     * @return Filter execute hours from.
     */
    public Integer getExecHoursFrom() {
        return execHoursFrom;
    }    

    /**
     * Setter of filter execute hours from.
     * @param execHoursFrom New value of execute hours from.
     */
    public void setExecHoursFrom(Integer execHoursFrom) {
        this.execHoursFrom = execHoursFrom;
    }

    /**
     * Getter of filter execute hours to.
     * @return Filter execute hours to.
     */
    public Integer getExecHoursTo() {
        return execHoursTo;
    }

    /**
     * Setter of filter execute hours to.
     * @param execHoursTo New value of execute hours to.
     */
    public void setExecHoursTo(Integer execHoursTo) {
        this.execHoursTo = execHoursTo;
    }

    /**
     * Getter of filter minimum loading servers.
     * @return Filter minimum loading servers.
     */
    public Integer getMinLoadServer() {
        return minLoadServer;
    }

    /**
     * Setter of filter minimum loading servers.
     * @param minLoadServer New value of minimum loading servers.
     */
    public void setMinLoadServer(Integer minLoadServer) {
        this.minLoadServer = minLoadServer;
    }

    /**
     * Getter of filter maximum loading servers.
     * @return Filter maximum loading servers.
     */
    public Integer getMaxLoadServer() {
        return maxLoadServer;
    }

    /**
     * Setter of filter maximum loading servers.
     * @param maxLoadServer New value of maximum loading servers.
     */
    public void setMaxLoadServer(Integer maxLoadServer) {
        this.maxLoadServer = maxLoadServer;
    }

    /**
     * Getter of filter minimum loading servers input value.
     * @return Filter minimum loading servers input value.
     */
    public Integer getMinLoadServerI() {
        return minLoadServerI;
    }

    /**
     * Setter of filter minimum loading servers input value.
     * @param minLoadServerI New value of minimum loading servers input value.
     */
    public void setMinLoadServerI(Integer minLoadServerI) {
        this.minLoadServerI = minLoadServerI;
    }

    /**
     * Getter of filter maximum loading servers input value.
     * @return Filter maximum loading servers input value.
     */
    public Integer getMaxLoadServerI() {
        return maxLoadServerI;
    }

    /**
     * Setter of filter maximum loading servers input value.
     * @param maxLoadServerI New value of maximum loading servers input value.
     */
    public void setMaxLoadServerI(Integer maxLoadServerI) {
        this.maxLoadServerI = maxLoadServerI;
    }
    
    /**
     * Getter of filter is checked running checkbox status.
     * @return True if the running checkbox is checked. False otherwise.
     */
    public boolean isIsCheckedRunning() {
        return isCheckedRunning;
    }

    /**
     * Setter of filter is checked running checkbox status.
     * @param isCheckedRunning New value of is checked running checkbox.
     */
    public void setIsCheckedRunning(boolean isCheckedRunning) {
        this.isCheckedRunning = isCheckedRunning;
    }

    /**
     * Getter of filter is checked runningError checkbox status.
     * @return True if the runningError checkbox is checked. False otherwise.
     */
    public boolean isIsCheckedRunningError() {
        return isCheckedRunningError;
    }

    /**
     * Setter of filter is checked runningError checkbox status.
     * @param isCheckedRunningError New value of is checked runningError checkbox.
     */
    public void setIsCheckedRunningError(boolean isCheckedRunningError) {
        this.isCheckedRunningError = isCheckedRunningError;
    }

    /**
     * Getter of filter is checked error checkbox status.
     * @return True if the error checkbox is checked. False otherwise.
     */
    public boolean isIsCheckedError() {
        return isCheckedError;
    }

    /**
     * Setter of filter is checked error checkbox status.
     * @param isCheckedError New value of is checked error checkbox.
     */
    public void setIsCheckedError(boolean isCheckedError) {
        this.isCheckedError = isCheckedError;
    }

    /**
     * Getter of filter is checked waiting checkbox status.
     * @return True if the waiting checkbox is checked. False otherwise.
     */
    public boolean isIsCheckedWaiting() {
        return isCheckedWaiting;
    }

    /**
     * Setter of filter is checked waiting checkbox status.
     * @param isCheckedWaiting New value of is checked waiting checkbox.
     */
    public void setIsCheckedWaiting(boolean isCheckedWaiting) {
        this.isCheckedWaiting = isCheckedWaiting;
    }

    /**
     * Getter of filter is checked paused checkbox status.
     * @return True if the paused checkbox is checked. False otherwise.
     */
    public boolean isIsCheckedPaused() {
        return isCheckedPaused;
    }

    /**
     * Setter of filter is checked paused checkbox status.
     * @param isCheckedPaused New value of is checked paused checkbox.
     */
    public void setIsCheckedPaused(boolean isCheckedPaused) {
        this.isCheckedPaused = isCheckedPaused;
    }
}
