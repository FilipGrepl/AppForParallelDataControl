/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import java.io.Serializable;
import java.util.List;
import org.primefaces.model.chart.BarChartModel;

/**
 * The historical graph data of one step of task.
 * @author filip
 */
public class HistoryStepGraphData implements Serializable {

    /** OBJECT PROPERTIES **/
    
    private BarChartModel execGraph;
    private BarChartModel fileProcGraph;
    private BarChartModel normalizedFileProcGraph;
    private BarChartModel usageRAMGraph;
    private BarChartModel usageCPUGraph;

    private String stepName;
    private Long stepId;
    
    private List<String> pathsToMinFileProcGraph;
    private List<String> pathsToMaxFileProcGraph;
    
    private List<String> pathsToMinFileNormProcGraph;
    private List<String> pathsToMaxFileNormProcGraph;

    boolean CheckMinFileExecTime;
    boolean CheckAvgFileExecTime;
    boolean CheckMaxFileExecTime;
    
    boolean CheckNormalizedMinFileExecTime;
    boolean CheckNormalizedMaxFileExecTime;

    boolean CheckMinRamUsage;
    boolean CheckAvgRamUsage;
    boolean CheckMaxRamUsage;

    boolean CheckMinCpuUsage;
    boolean CheckAvgCpuUsage;
    boolean CheckMaxCpuUsage;
    
    /** OBJECT METHODS **/

    public HistoryStepGraphData() {        
        execGraph = null;
        fileProcGraph = null;
        normalizedFileProcGraph = null;
        usageRAMGraph = null;
        usageCPUGraph = null;
        
        
        stepName = null;
        stepId = null;
        
        pathsToMinFileProcGraph = null;
        pathsToMaxFileProcGraph = null;

        pathsToMinFileNormProcGraph = null;
        pathsToMaxFileNormProcGraph = null;

        CheckMinFileExecTime = true;
        CheckAvgFileExecTime = true;
        CheckMaxFileExecTime = true;
        
        CheckNormalizedMinFileExecTime = true;
        CheckNormalizedMaxFileExecTime = true;

        CheckMinRamUsage = true;
        CheckAvgRamUsage = true;
        CheckMaxRamUsage = true;

        CheckMinCpuUsage = true;
        CheckAvgCpuUsage = true;
        CheckMaxCpuUsage = true;
    }

    /** GETTERS AND SETTERS **/
    
    public BarChartModel getExecGraph() {
        return execGraph;
    }

    public void setExecGraph(BarChartModel execGraph) {
        this.execGraph = execGraph;
    }

    public BarChartModel getFileProcGraph() {
        return fileProcGraph;
    }

    public void setFileProcGraph(BarChartModel fileProcGraph) {
        this.fileProcGraph = fileProcGraph;
    }

    public BarChartModel getNormalizedFileProcGraph() {
        return normalizedFileProcGraph;
    }

    public void setNormalizedFileProcGraph(BarChartModel normalizedFileProcGraph) {
        this.normalizedFileProcGraph = normalizedFileProcGraph;
    }
    
    public BarChartModel getUsageRAMGraph() {
        return usageRAMGraph;
    }

    public void setUsageRAMGraph(BarChartModel usageRAMGraph) {
        this.usageRAMGraph = usageRAMGraph;
    }

    public BarChartModel getUsageCPUGraph() {
        return usageCPUGraph;
    }

    public void setUsageCPUGraph(BarChartModel usageCPUGraph) {
        this.usageCPUGraph = usageCPUGraph;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public List<String> getPathsToMinFileProcGraph() {
        return pathsToMinFileProcGraph;
    }

    public void setPathsToMinFileProcGraph(List<String> pathsToMinFileProcGraph) {
        this.pathsToMinFileProcGraph = pathsToMinFileProcGraph;
    }

    public List<String> getPathsToMaxFileProcGraph() {
        return pathsToMaxFileProcGraph;
    }

    public void setPathsToMaxFileProcGraph(List<String> pathsToMaxFileProcGraph) {
        this.pathsToMaxFileProcGraph = pathsToMaxFileProcGraph;
    }

    public List<String> getPathsToMinFileNormProcGraph() {
        return pathsToMinFileNormProcGraph;
    }

    public void setPathsToMinFileNormProcGraph(List<String> pathsToMinFileNormProcGraph) {
        this.pathsToMinFileNormProcGraph = pathsToMinFileNormProcGraph;
    }

    public List<String> getPathsToMaxFileNormProcGraph() {
        return pathsToMaxFileNormProcGraph;
    }

    public void setPathsToMaxFileNormProcGraph(List<String> pathsToMaxFileNormProcGraph) {
        this.pathsToMaxFileNormProcGraph = pathsToMaxFileNormProcGraph;
    }
    
    public boolean isCheckMinFileExecTime() {
        return CheckMinFileExecTime;
    }

    public void setCheckMinFileExecTime(boolean CheckMinFileExecTime) {
        this.CheckMinFileExecTime = CheckMinFileExecTime;
    }

    public boolean isCheckAvgFileExecTime() {
        return CheckAvgFileExecTime;
    }

    public void setCheckAvgFileExecTime(boolean CheckAvgFileExecTime) {
        this.CheckAvgFileExecTime = CheckAvgFileExecTime;
    }

    public boolean isCheckMaxFileExecTime() {
        return CheckMaxFileExecTime;
    }

    public void setCheckMaxFileExecTime(boolean CheckMaxFileExecTime) {
        this.CheckMaxFileExecTime = CheckMaxFileExecTime;
    }

    public boolean isCheckNormalizedMinFileExecTime() {
        return CheckNormalizedMinFileExecTime;
    }

    public void setCheckNormalizedMinFileExecTime(boolean CheckNormalizedMinFileExecTime) {
        this.CheckNormalizedMinFileExecTime = CheckNormalizedMinFileExecTime;
    }

    public boolean isCheckNormalizedMaxFileExecTime() {
        return CheckNormalizedMaxFileExecTime;
    }

    public void setCheckNormalizedMaxFileExecTime(boolean CheckNormalizedMaxFileExecTime) {
        this.CheckNormalizedMaxFileExecTime = CheckNormalizedMaxFileExecTime;
    }
    
    public boolean isCheckMinRamUsage() {
        return CheckMinRamUsage;
    }

    public void setCheckMinRamUsage(boolean CheckMinRamUsage) {
        this.CheckMinRamUsage = CheckMinRamUsage;
    }

    public boolean isCheckAvgRamUsage() {
        return CheckAvgRamUsage;
    }

    public void setCheckAvgRamUsage(boolean CheckAvgRamUsage) {
        this.CheckAvgRamUsage = CheckAvgRamUsage;
    }

    public boolean isCheckMaxRamUsage() {
        return CheckMaxRamUsage;
    }

    public void setCheckMaxRamUsage(boolean CheckMaxRamUsage) {
        this.CheckMaxRamUsage = CheckMaxRamUsage;
    }

    public boolean isCheckMinCpuUsage() {
        return CheckMinCpuUsage;
    }

    public void setCheckMinCpuUsage(boolean CheckMinCpuUsage) {
        this.CheckMinCpuUsage = CheckMinCpuUsage;
    }

    public boolean isCheckAvgCpuUsage() {
        return CheckAvgCpuUsage;
    }

    public void setCheckAvgCpuUsage(boolean CheckAvgCpuUsage) {
        this.CheckAvgCpuUsage = CheckAvgCpuUsage;
    }

    public boolean isCheckMaxCpuUsage() {
        return CheckMaxCpuUsage;
    }

    public void setCheckMaxCpuUsage(boolean CheckMaxCpuUsage) {
        this.CheckMaxCpuUsage = CheckMaxCpuUsage;
    }
}
