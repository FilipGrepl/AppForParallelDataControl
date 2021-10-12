/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

import com.model.database.status.StepStatus;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 *
 * @author Filip
 */

@Entity
@Table(name = "JobRunData")
public class JobRunData implements Serializable{
    
    @EmbeddedId
    private JobNodeStepKey jobNodeStepKey;
    
    @ManyToOne
    @JoinColumn(name = "job", updatable = false, insertable = false)
    private Job job;
    
    @ManyToOne
    @JoinColumn(name = "node", updatable = false, insertable = false)
    private Node node;
    
    @ManyToOne
    @JoinColumn(name = "step", updatable = false, insertable = false)
    private Step step;
    
    @OneToMany(mappedBy="jobRunData", cascade = CascadeType.ALL)
    Set<Error> errors = new HashSet<>();

    @Column(name = "stepStatus")
    @Enumerated(EnumType.STRING)
    private StepStatus stepStatus;
    
    @Column(name = "totalFiles")
    @Min(0)
    private long totalFiles;
    
    @Column(name = "processedFiles")
    @Min(0)
    private long processedFiles;
    
    @Column(name = "startedAt")
    private Timestamp startedAt;
    
    @Column(name = "finishedAt")
    private Timestamp finishedAt;
    
    @Column(name = "runningTime")
    @Min(0)
    private long runningTime;
    
    /* CPU */
    
    @Column(name = "minCpuLoad")
    @Min(0)
    @Max(100)
    private int minCpuLoad;
    
    @Column(name = "maxCpuLoad")
    @Min(0)
    @Max(100)
    private int maxCpuLoad;
    
    @Column(name = "avgCpuLoad")
    @Min(0)
    @Max(100)
    private int avgCpuLoad;
    
    /* RAM */
    
    @Column(name = "minRamUsage")
    @Min(0)
    @Max(100)
    private int minRamUsage;
    
    @Column(name = "maxRamUsage")
    @Min(0)
    @Max(100)
    private int maxRamUsage;
    
    @Column(name = "avgRamUsage")
    @Min(0)
    @Max(100)
    private int avgRamUsage;
    
    /* Loaded value counter */
    
    @Column(name = "avgRamCpuCounter")
    @Min(0)
    private long avgRamCpuCounter;
    
    /* File processing */
    
    @Column(name = "minFileProcTime")
    @Min(0)
    private int minFileProcTime;
    
    @Column(name = "maxFileProcTime")
    @Min(0)
    private int maxFileProcTime;
    
    @Column(name = "avgFileProcTime")
    @Min(0)
    private int avgFileProcTime;

    public JobRunData() {
        
    }
    
    public JobRunData(JobNodeStepKey jobNodeStepKey, StepStatus stepStatus, long totalFiles, long processedFiles, Timestamp startedAt, Timestamp finishedAt, long runningTime, int minCpuLoad, int maxCpuLoad, int avgCpuLoad, int minRamUsage, int maxRamUsage, int avgRamUsage, long avgRamCpuCounter, int minFileProcTime, int maxFileProcTime, int avgFileProcTime) {
        this.jobNodeStepKey = jobNodeStepKey;
        this.stepStatus = stepStatus;
        this.totalFiles = totalFiles;
        this.processedFiles = processedFiles;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.runningTime = runningTime;
        this.minCpuLoad = minCpuLoad;
        this.maxCpuLoad = maxCpuLoad;
        this.avgCpuLoad = avgCpuLoad;
        this.minRamUsage = minRamUsage;
        this.maxRamUsage = maxRamUsage;
        this.avgRamUsage = avgRamUsage;
        this.avgRamCpuCounter = avgRamCpuCounter;
        this.minFileProcTime = minFileProcTime;
        this.maxFileProcTime = maxFileProcTime;
        this.avgFileProcTime = avgFileProcTime;
    }
    
    public JobNodeStepKey getJobNodeStepKey() {
        return jobNodeStepKey;
    }

    public void setJobNodeStepKey(JobNodeStepKey jobNodeStepKey) {
        this.jobNodeStepKey = jobNodeStepKey;
    }

    public Job getJob() {
        return job;
    }

    public Node getNode() {
        return node;
    }

    public Step getStep() {
        return step;
    }

    public Set<Error> getErrors() {
        return errors;
    }

    public void setErrors(Set<Error> errors) {
        this.errors = errors;
    }

    public StepStatus getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(StepStatus stepStatus) {
        this.stepStatus = stepStatus;
    }

    public long getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(long totalFiles) {
        this.totalFiles = totalFiles;
    }

    public long getProcessedFiles() {
        return processedFiles;
    }

    public void setProcessedFiles(long processedFiles) {
        this.processedFiles = processedFiles;
    }

    public Timestamp getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public Timestamp getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }

    public long getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(long runningTime) {
        this.runningTime = runningTime;
    }

    public int getMinCpuLoad() {
        return minCpuLoad;
    }

    public void setMinCpuLoad(int minCpuLoad) {
        this.minCpuLoad = minCpuLoad;
    }

    public int getMaxCpuLoad() {
        return maxCpuLoad;
    }

    public void setMaxCpuLoad(int maxCpuLoad) {
        this.maxCpuLoad = maxCpuLoad;
    }

    public int getAvgCpuLoad() {
        return avgCpuLoad;
    }

    public void setAvgCpuLoad(int avgCpuLoad) {
        this.avgCpuLoad = avgCpuLoad;
    }

    public int getMinRamUsage() {
        return minRamUsage;
    }

    public void setMinRamUsage(int minRamUsage) {
        this.minRamUsage = minRamUsage;
    }

    public int getMaxRamUsage() {
        return maxRamUsage;
    }

    public void setMaxRamUsage(int maxRamUsage) {
        this.maxRamUsage = maxRamUsage;
    }

    public int getAvgRamUsage() {
        return avgRamUsage;
    }

    public void setAvgRamUsage(int avgRamUsage) {
        this.avgRamUsage = avgRamUsage;
    }

    public long getAvgRamUsageCounter() {
        return avgRamCpuCounter;
    }

    public void setAvgRamUsageCounter(long avgRamCpuCounter) {
        this.avgRamCpuCounter = avgRamCpuCounter;
    }

    public int getMinFileProcTime() {
        return minFileProcTime;
    }

    public void setMinFileProcTime(int minFileProcTime) {
        this.minFileProcTime = minFileProcTime;
    }

    public int getMaxFileProcTime() {
        return maxFileProcTime;
    }

    public void setMaxFileProcTime(int maxFileProcTime) {
        this.maxFileProcTime = maxFileProcTime;
    }

    public int getAvgFileProcTime() {
        return avgFileProcTime;
    }

    public void setAvgFileProcTime(int avgFileProcTime) {
        this.avgFileProcTime = avgFileProcTime;
    }
}
