/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;

/**
 * The StepRunData entity. This entity represents running data about specific step of task on specific node.
 * @author Filip
 */
@Entity
@Table(name = "StepRunData")
public class StepRunData implements Serializable, EntityInterface<RunJobNodeStepKey> {
    
    @EmbeddedId
    private RunJobNodeStepKey id;

    @ManyToOne
    @JoinColumn(name = "runJob", updatable = false, insertable = false)
    private RunJob runJob;

    @ManyToOne
    @JoinColumn(name = "node", updatable = false, insertable = false)
    private Node node;

    @ManyToOne
    @JoinColumn(name = "step", updatable = false, insertable = false)
    private Step step;

    @OneToMany(mappedBy = "stepRunData", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Error> errors = new ArrayList<>();

    @Column(name = "stepStatus")
    @Enumerated(EnumType.STRING)
    private StepStatusEnum stepStatus;

    @Column(name = "totalFsNodes")
    @Min(0)
    private long totalFsNodes;

    @Column(name = "processedFsNodes")
    @Min(0)
    private long processedFsNodes;

    @Column(name = "firstStartAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date firstStartedAt;

    @Column(name = "finishedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;
    
    @Column(name = "lastFinishedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastFinishedAt;


    @Column(name = "execTime", nullable = false)
    @Min(0)
    private long execTime;
    
    @Column(name = "waitingTime", nullable = false)
    @Min(0)
    private long waitingTime;

    /* CPU */
    @Column(name = "minCpuLoad", nullable = false)
    @Min(0)
    private double minCpuLoad;

    @Column(name = "maxCpuLoad", nullable = false)
    @Min(0)
    private double maxCpuLoad;

    @Column(name = "avgCpuLoad", nullable = false)
    @Min(0)
    private double avgCpuLoad;

    /* RAM */
    @Column(name = "minRamUsage", nullable = false)
    @Min(0)
    private double minRamUsage;

    @Column(name = "maxRamUsage", nullable = false)
    @Min(0)
    private double maxRamUsage;

    @Column(name = "avgRamUsage", nullable = false)
    @Min(0)
    private double avgRamUsage;

    /* Loaded value counter */
    @Column(name = "avgRamCpuCounter", nullable = false)
    @Min(0)
    private long avgRamCpuCounter;

    /* File processing */
    @Column(name = "minFileProcTime", nullable = false)
    @Min(0)
    private long minFileProcTime;
    
    @Column (name = "minFilePath")
    private String minFilePath;
    
    @Column(name = "minFileNormalizedProcTime", nullable = false)
    @Min(0)
    private long minFileNormalizedProcTime;
    
    @Column (name = "minFileNormalizedPath")
    private String minFileNormalizedPath;

    @Column(name = "maxFileProcTime", nullable = false)
    @Min(0)
    private long maxFileProcTime;
    
    @Column (name = "maxFilePath")
    private String maxFilePath;
    
    @Column(name = "maxFileNormalizedProcTime", nullable = false)
    @Min(0)
    private long maxFileNormalizedProcTime;
    
    @Column (name = "maxFileNormalizedPath")
    private String maxFileNormalizedPath;

    @Column(name = "avgFileProcTime", nullable = false)
    @Min(0)
    private long avgFileProcTime;

    @Column(name = "avgFileProcCounter", nullable = false)
    @Min(0)
    private long avgFileProcCounter;
    
    @Column(name = "runButtonPressed")
    private boolean runButtonPressed;
    
    @Column(name = "rerunButtonPressed")
    private boolean rerunButtonPressed;
    
    public StepRunData() {

    }

    public StepRunData(RunJobNodeStepKey id, StepStatusEnum stepStatus, long totalFsNodes, long processedFsNodes, Date firstStartedAt,  Date finishedAt, Date lastFinishedAt, long execTime, long waitingTime, double minCpuLoad, double maxCpuLoad, double avgCpuLoad, double minRamUsage, double maxRamUsage, double avgRamUsage, long avgRamCpuCounter, long minFileProcTime, String minFilePath, long minFileNormalizedProcTime, String minFileNormalizedPath, long maxFileProcTime, String maxFilePath, long maxFileNormalizedProcTime, String maxFileNormalizedPath, long avgFileProcTime, long avgFileProcCounter, boolean runButtonPressed, boolean rerunButtonPressed) {
        this.id = id;
        this.stepStatus = stepStatus;
        this.totalFsNodes = totalFsNodes;
        this.processedFsNodes = processedFsNodes;
        this.firstStartedAt = firstStartedAt;
        this.finishedAt = finishedAt;
        this.lastFinishedAt = lastFinishedAt;
        this.execTime = execTime;
        this.waitingTime = waitingTime;
        this.minCpuLoad = minCpuLoad;
        this.maxCpuLoad = maxCpuLoad;
        this.avgCpuLoad = avgCpuLoad;
        this.minRamUsage = minRamUsage;
        this.maxRamUsage = maxRamUsage;
        this.avgRamUsage = avgRamUsage;
        this.avgRamCpuCounter = avgRamCpuCounter;
        this.minFileProcTime = minFileProcTime;
        this.minFilePath = minFilePath;
        this.minFileNormalizedProcTime = minFileNormalizedProcTime;
        this.minFileNormalizedPath = minFileNormalizedPath;
        this.maxFileProcTime = maxFileProcTime;
        this.maxFilePath = maxFilePath;
        this.maxFileNormalizedProcTime = maxFileNormalizedProcTime;
        this.maxFileNormalizedPath = maxFileNormalizedPath;
        this.avgFileProcTime = avgFileProcTime;
        this.avgFileProcCounter = avgFileProcCounter;
        this.runButtonPressed = runButtonPressed;
        this.rerunButtonPressed = rerunButtonPressed;
    }

    public RunJobNodeStepKey getId() {
        return id;
    }

    public void setId(RunJobNodeStepKey id) {
        this.id = id;
    }

    public RunJob getRunJob() {
        return runJob;
    }

    public Node getNode() {
        return node;
    }

    public Step getStep() {
        return step;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public StepStatusEnum getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatusEnum stepStatus) {
        this.stepStatus = stepStatus;
    }

    public long getTotalFsNodes() {
        return totalFsNodes;
    }

    public void setTotalFsNodes(long totalFsNodes) {
        this.totalFsNodes = totalFsNodes;
    }

    public long getProcessedFsNodes() {
        return processedFsNodes;
    }

    public void setProcessedFsNodes(long processedFsNodes) {
        this.processedFsNodes = processedFsNodes;
    }

    public Date getFirstStartedAt() {
        return firstStartedAt;
    }

    public void setFirstStartedAt(Date firstStartedAt) {
        this.firstStartedAt = firstStartedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Date getLastFinishedAt() {
        return lastFinishedAt;
    }

    public void setLastFinishedAt(Date lastFinishedAt) {
        this.lastFinishedAt = lastFinishedAt;
    }

    public long getExecTime() {
        return this.execTime;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    /*public long getRunningTimeToDisplay() {
        if (this.stepStatus == StepStatusEnum.RUNNING || this.stepStatus == StepStatusEnum.RUNNING_ERROR) {
            return this.runningTime + System.currentTimeMillis() - this.startedAt.getTime();
        } else {
            return runningTime;
        }
    }*/

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    public double getMinCpuLoad() {
        return minCpuLoad;
    }

    public void setMinCpuLoad(double minCpuLoad) {
        this.minCpuLoad = minCpuLoad;
    }

    public double getMaxCpuLoad() {
        return maxCpuLoad;
    }

    public void setMaxCpuLoad(double maxCpuLoad) {
        this.maxCpuLoad = maxCpuLoad;
    }

    public double getAvgCpuLoad() {
        return avgCpuLoad;
    }

    public void setAvgCpuLoad(double avgCpuLoad) {
        this.avgCpuLoad = avgCpuLoad;
    }

    public double getMinRamUsage() {
        return minRamUsage;
    }

    public void setMinRamUsage(double minRamUsage) {
        this.minRamUsage = minRamUsage;
    }

    public double getMaxRamUsage() {
        return maxRamUsage;
    }

    public void setMaxRamUsage(double maxRamUsage) {
        this.maxRamUsage = maxRamUsage;
    }

    public double getAvgRamUsage() {
        return avgRamUsage;
    }

    public void setAvgRamUsage(double avgRamUsage) {
        this.avgRamUsage = avgRamUsage;
    }

    public long getAvgRamCpuCounter() {
        return avgRamCpuCounter;
    }

    public void setAvgRamCpuCounter(long avgRamCpuCounter) {
        this.avgRamCpuCounter = avgRamCpuCounter;
    }

    public long getMinFileProcTime() {
        return minFileProcTime;
    }

    public void setMinFileProcTime(long minFileProcTime) {
        this.minFileProcTime = minFileProcTime;
    }

    public String getMinFilePath() {
        return minFilePath;
    }

    public void setMinFilePath(String minFilePath) {
        this.minFilePath = minFilePath;
    }

    public long getMinFileNormalizedProcTime() {
        return minFileNormalizedProcTime;
    }

    public void setMinFileNormalizedProcTime(long minFileNormalizedProcTime) {
        this.minFileNormalizedProcTime = minFileNormalizedProcTime;
    }

    public String getMinFileNormalizedPath() {
        return minFileNormalizedPath;
    }

    public void setMinFileNormalizedPath(String minFileNormalizedPath) {
        this.minFileNormalizedPath = minFileNormalizedPath;
    }

    public long getMaxFileProcTime() {
        return maxFileProcTime;
    }

    public void setMaxFileProcTime(long maxFileProcTime) {
        this.maxFileProcTime = maxFileProcTime;
    }

    public String getMaxFilePath() {
        return maxFilePath;
    }

    public void setMaxFilePath(String maxFilePath) {
        this.maxFilePath = maxFilePath;
    }

    public long getMaxFileNormalizedProcTime() {
        return maxFileNormalizedProcTime;
    }

    public void setMaxFileNormalizedProcTime(long maxFileNormalizedProcTime) {
        this.maxFileNormalizedProcTime = maxFileNormalizedProcTime;
    }

    public String getMaxFileNormalizedPath() {
        return maxFileNormalizedPath;
    }

    public void setMaxFileNormalizedPath(String maxFileNormalizedPath) {
        this.maxFileNormalizedPath = maxFileNormalizedPath;
    }

    public long getAvgFileProcTime() {
        return avgFileProcTime;
    }

    public void setAvgFileProcTime(long avgFileProcTime) {
        this.avgFileProcTime = avgFileProcTime;
    }

    public long getAvgFileProcCounter() {
        return avgFileProcCounter;
    }

    public void setAvgFileProcCounter(long avgFileProcCounter) {
        this.avgFileProcCounter = avgFileProcCounter;
    }

    public boolean isRunButtonPressed() {
        return runButtonPressed;
    }

    public void setRunButtonPressed(boolean runButtonPressed) {
        this.runButtonPressed = runButtonPressed;
    }

    public boolean isRerunButtonPressed() {
        return rerunButtonPressed;
    }

    public void setRerunButtonPressed(boolean rerunButtonPressed) {
        this.rerunButtonPressed = rerunButtonPressed;
    }

    public long getWaitingTimeToDisplay() {
        if (this.lastFinishedAt != null && this.stepStatus != StepStatusEnum.FINISHED) {
            return this.waitingTime + System.currentTimeMillis() - this.lastFinishedAt.getTime();
        } else {
            return this.waitingTime;
        }
    }
    

    @Override
    public String toString() {
        return "StepRunData{"
                + "runJobNodeStepKey=" + id.toString()
                + ", stepStatus=" + stepStatus
                + ", totalFsNodes=" + totalFsNodes
                + ", processedFsNodes=" + processedFsNodes
                + ", firstStartedAt=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firstStartedAt)
                + ", finishedAt=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(finishedAt)
                + ", lastFinishedAt=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastFinishedAt)
                + ", runningTime=" + execTime
                + ", waitingTime=" + waitingTime
                + ", minCpuLoad=" + minCpuLoad
                + ", maxCpuLoad=" + maxCpuLoad
                + ", avgCpuLoad=" + avgCpuLoad
                + ", minRamUsage=" + minRamUsage
                + ", maxRamUsage=" + maxRamUsage
                + ", avgRamUsage=" + avgRamUsage
                + ", avgRamCpuCounter=" + avgRamCpuCounter
                + ", minFileProcTime=" + minFileProcTime
                + ", minFilePath=" + minFilePath
                + ", minFileNormalizedProcTime=" + minFileNormalizedProcTime
                + ", minFileNormalizedPath=" + minFileNormalizedPath
                + ", maxFileProcTime=" + maxFileProcTime
                + ", maxFilePath=" + maxFilePath
                + ", maxFileNormalizedProcTime=" + maxFileNormalizedProcTime
                + ", maxFileNormalizedPath=" + maxFileNormalizedPath
                + ", avgFileProcTime=" + avgFileProcTime
                + ", avgFileProcCounter=" + avgFileProcCounter
                + ", runButtonPressed=" + runButtonPressed
                + ", rerunButtonPressed=" + rerunButtonPressed
                + '}';
    }
}
