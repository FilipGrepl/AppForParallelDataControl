/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

import com.model.database.status.IOtypes;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 *
 * @author Filip
 */
@Entity
@Table(name = "Step")
public class Step implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_stepID")
    private Long stepID;
    
    @OneToMany(mappedBy="step", cascade = CascadeType.ALL)
    Set<JobRunData> StepRunData = new HashSet<>();
    
    @ManyToOne
    @JoinColumn(name = "FK_jobID", nullable = false)
    private Job job;
    
    @Column(name = "stepName")
    private String stepName;
    
    @Column(name = "programName")
    private String programName;
    
    @Column(name = "inputType")
    @Enumerated(EnumType.STRING)
    private IOtypes inputType; 
    
    @Column(name = "inputPrefix")
    private String inputPrefix;
    
    @Column(name = "inputPath")
    private String inputPath;
    
    @Column(name = "inputRegex")
    private String inputRegex;
    
    @Column(name = "threads")
    private int threads;
    
    @Column(name = "outputType")
    @Enumerated(EnumType.STRING)
    private IOtypes outputType; 
    
    @Column(name = "outputPrefix")
    private String outputPrefix;
    
    @Column(name = "outputPath")
    private String outputPath;
    
    @Column(name = "otherParameters")
    private String otherParameters;
    
    @Column(name = "timeout")
    @Min(0)
    private long timeout;
    
    @Column(name = "noEmptyOutputDir")
    private boolean noEmptyOutputDir;
    
    @Column(name = "isEmptyStderr")
    private boolean isEmptyStderr;
    
    @Column(name = "existOutToInFileOrDir")
    private boolean existOutToInFileOrDir;
    
    @Column(name = "logSizeLessThan")
    private int logSizeLessThan;
    
    @Column(name = "pathToErrLog")
    private String pathToErrLog;
    
    @Column(name = "isSynchonizedStep")
    private boolean isSynchronizedStep;
    
    @Column(name = "stepOrder")
    @Min(0)
    private int stepOrder;
    
    public Step() {
        
    }

    public Step(Job job, String stepName, String programName, IOtypes inputType, String inputPrefix, String inputPath, String inputRegex, int threads, IOtypes outputType, String outputPrefix, String outputPath, String otherParameters, long timeout, boolean noEmptyOutputDir, boolean isEmptyStderr, boolean existOutToInFileOrDir, int logSizeLessThan, String pathToErrLog, boolean isSynchronizedStep, int stepOrder) {
        this.job = job;
        this.stepName = stepName;
        this.programName = programName;
        this.inputType = inputType;
        this.inputPrefix = inputPrefix;
        this.inputPath = inputPath;
        this.inputRegex = inputRegex;
        this.threads = threads;
        this.outputType = outputType;
        this.outputPrefix = outputPrefix;
        this.outputPath = outputPath;
        this.otherParameters = otherParameters;
        this.timeout = timeout;
        this.noEmptyOutputDir = noEmptyOutputDir;
        this.isEmptyStderr = isEmptyStderr;
        this.existOutToInFileOrDir = existOutToInFileOrDir;
        this.logSizeLessThan = logSizeLessThan;
        this.pathToErrLog = pathToErrLog;
        this.isSynchronizedStep = isSynchronizedStep;
        this.stepOrder = stepOrder;
    }

    public long getStepID() {
        return stepID;
    }

    public Set<JobRunData> getStepRunData() {
        return StepRunData;
    }

    public void setStepRunData(Set<JobRunData> StepRunData) {
        this.StepRunData = StepRunData;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public IOtypes getInputType() {
        return inputType;
    }

    public void setInputType(IOtypes inputType) {
        this.inputType = inputType;
    }

    public String getInputPrefix() {
        return inputPrefix;
    }

    public void setInputPrefix(String inputPrefix) {
        this.inputPrefix = inputPrefix;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getInputRegex() {
        return inputRegex;
    }

    public void setInputRegex(String inputRegex) {
        this.inputRegex = inputRegex;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
    
    public IOtypes getOutputType() {
        return outputType;
    }

    public void setOutputType(IOtypes outputType) {
        this.outputType = outputType;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getOtherParameters() {
        return otherParameters;
    }

    public void setOtherParameters(String otherParameters) {
        this.otherParameters = otherParameters;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isNoEmptyOutputDir() {
        return noEmptyOutputDir;
    }

    public void setNoEmptyOutputDir(boolean noEmptyOutputDir) {
        this.noEmptyOutputDir = noEmptyOutputDir;
    }

    public boolean isIsEmptyStderr() {
        return isEmptyStderr;
    }

    public void setIsEmptyStderr(boolean isEmptyStderr) {
        this.isEmptyStderr = isEmptyStderr;
    }

    public boolean isExistOutToInFileOrDir() {
        return existOutToInFileOrDir;
    }

    public void setExistOutToInFileOrDir(boolean existOutToInFileOrDir) {
        this.existOutToInFileOrDir = existOutToInFileOrDir;
    }

    public int getLogSizeLessThan() {
        return logSizeLessThan;
    }

    public void setLogSizeLessThan(int logSizeLessThan) {
        this.logSizeLessThan = logSizeLessThan;
    }

    public String getPathToErrLog() {
        return pathToErrLog;
    }

    public void setPathToErrLog(String pathToErrLog) {
        this.pathToErrLog = pathToErrLog;
    }

    public boolean isIsSynchronizedStep() {
        return isSynchronizedStep;
    }

    public void setIsSynchronizedStep(boolean isSynchronizedStep) {
        this.isSynchronizedStep = isSynchronizedStep;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    
}
