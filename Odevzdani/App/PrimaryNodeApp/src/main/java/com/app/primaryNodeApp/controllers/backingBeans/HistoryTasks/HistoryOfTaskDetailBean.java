/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.HistoryTasks;

import com.app.primaryNodeApp.services.dataClasses.HistoryStepGraphData;
import com.app.primaryNodeApp.services.dataClasses.HistoryTaskData;
import com.app.primaryNodeApp.services.HistoryTasServices.HistoryTaskGraphService;
import com.app.primaryNodeApp.services.HistoryTasServices.HistoryTasksService;
import com.app.primaryNodeApp.services.RunJobService.RunJobService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.chart.BarChartModel;

/**
 * The backing bean for history task detail.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class HistoryOfTaskDetailBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private HistoryTaskData historyTaskData;
    private Long runJobID;

    private List<HistoryStepGraphData> historyStepGraphData;
    private BarChartModel stepExecTimeGraph;
    
    private HistoryTaskGraphService historyTaskGraphService;
    private final RunJobService runJobService;
    
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor
     */
    public HistoryOfTaskDetailBean() {
        runJobService = new RunJobService();
    }

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.historyStepGraphData = new ArrayList<>();
    }

    /**
     * Method which is called when the URL parameters (runJobID) are loaded.
     */
    public void loadData() {
        
        if (runJobService.getById(runJobID) == null) {
            throw new RuntimeException("Úloha s ID " + runJobID + " neexistuje.");
        }
        historyTaskData = new HistoryTasksService().getRunTaskData(runJobID);        
        historyTaskGraphService = new HistoryTaskGraphService(runJobID);
        stepExecTimeGraph = historyTaskGraphService.getStepExecTimeGraph();
        historyStepGraphData = historyTaskGraphService.getHistoryStepGraphData();
    }

    /**
     * Getter of run job id.
     * @return Run job id value.
     */
    public Long getRunJobID() {
        return runJobID;
    }

    /**
     * Setter of run job id.
     * @param runJobID New value of run job id.
     */
    public void setRunJobID(Long runJobID) {
        this.runJobID = runJobID;
    }

    /**
     * Get data of finished task.
     * @return Data of finished task.
     */
    public HistoryTaskData getHistoryTaskData() {
        return historyTaskData;
    }

    /**
     * Getter of graph data for all task steps.
     * @return List of graph data for all task steps.
     */
    public List<HistoryStepGraphData> getHistoryStepGraphData() {
        return historyStepGraphData;
    }

    /**
     * Getter of graph data of total execute time on each secondary node.
     * @return 
     */
    public BarChartModel getStepExecTimeGraph() {
        return stepExecTimeGraph;
    }
    
    /**
     * Handler of change event on min/avg/max checboxes of file execute time graph.
     * @param index Index into the list of all steps statistic data - it selects graph of concrete step.
     */
    public void changeFileExecCheckboxHandler(int index) {
        this.historyTaskGraphService.createFileExecModel(this.historyStepGraphData.get(index));
    }
    
    /**
     * Handler of change event on min/avg/max checboxes of file execute time graph.
     * @param index Index into the list of all steps statistic data - it selects graph of concrete step.
     */
    public void changeNormalizedFileExecCheckboxHandler(int index) {
        this.historyTaskGraphService.createNormalizedFileExecModel(this.historyStepGraphData.get(index));
    }
    
    /**
     * Handler of change event on min/avg/max checboxes of ram usage graph.
     * @param index Index into the list of all steps statistic data - it selects graph of concrete step.
     */
    public void changeRamUsageCheckboxHandler(int index) {
        this.historyTaskGraphService.createUsageRamModel(this.historyStepGraphData.get(index));
    }
    
    /**
     * Handler of change event on min/avg/max checboxes of cpu usage graph.
     * @param index Index into the list of all steps statistic data - it selects graph of concrete step.
     */
    public void changeCpuUsageCheckboxHandler(int index) {
        this.historyTaskGraphService.createUsageCpuModel(this.historyStepGraphData.get(index));
    }
    
    /**
     * Handler of click event on delete run job button. It deletes all data of run job.
     * @throws IOException If an IOException occurs.
     */
    public void deleteRunJobTask() throws IOException {
        runJobService.delete(runJobID);
        FacesContext.getCurrentInstance().getExternalContext().redirect("historyOfTasks.xhtml?i=2");
    }
}
