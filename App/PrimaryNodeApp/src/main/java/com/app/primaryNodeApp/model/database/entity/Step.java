/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 * The Step entity.
 * @author Filip
 */
@Entity
@Table(name = "Step")
public class Step implements Serializable, EntityInterface<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_stepID")
    private Long id;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    List<StepRunData> stepRunData = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "FK_jobID", nullable = false)
    private Job job;

    @Expose
    @Column(name = "stepName", length=1024)
    private String stepName;

    @Expose
    @Column(name = "commandPrefix", length=1024)
    private String commandPrefix;

    @Expose
    @Column(name = "inputType")
    @Enumerated(EnumType.STRING)
    private IOtypesEnum inputType;

    @Expose
    @Column(name = "inputArgument", length=1024)
    private String inputArgument;

    @Expose
    @Column(name = "inputPath", length=1024)
    private String inputPath;

    @Expose
    @Column(name = "inputRegex", length=1024)
    private String inputRegex;

    @Expose
    @Column(name = "processes")
    private int processes;

    @Expose
    @Column(name = "outputType")
    @Enumerated(EnumType.STRING)
    private IOtypesEnum outputType;

    @Expose
    @Column(name = "outputArgument", length=1024)
    private String outputArgument;

    @Expose
    @Column(name = "outputPath", length=1024)
    private String outputPath;

    @Expose
    @Column(name = "commandSuffix", length=1024)
    private String commandSuffix;

    @Expose
    @Column(name = "timeout")
    @Min(0)
    private long timeout;

    @Expose
    @Column(name = "ExistNoEmptyOutputFsNode")
    private boolean ExistNoEmptyOutputFsNode;

    @Expose
    @Column(name = "EmptyStderr")
    private boolean EmptyStderr;

    @Expose
    @Column(name = "CheckLogFileSize")
    private boolean CheckLogFileSize;

    @Expose
    @Column(name = "pathToLog", length=1024)
    private String pathToLog;

    @Expose
    @Column(name = "logSizeLessThan")
    private long logSizeLessThan;

    @Expose
    @Column(name = "EqualInToOutFiles")
    private boolean EqualInToOutFiles;

    @Expose
    @Column(name = "EqualInToOutFilesSecondOutput")
    private boolean EqualInToOutFilesSecondOutput;

    @Expose
    @Column(name = "pathToSecondOutputFolder", length=1024)
    private String pathToSecondOutputFolder;

    /*IF ERROR*/
    @Expose
    @Column(name = "saveStderr")
    private boolean saveStderr;

    @Expose
    @Column(name = "saveErrLog")
    private boolean saveErrLog;

    @Expose
    @Column(name = "pathToErrLog", length=1024)
    private String pathToErrLog;

    @Expose
    @Column(name = "isSynchonizedStep")
    private boolean SynchronizedStep;

    @Column(name = "stepOrder")
    @Min(0)
    private int stepOrder;

    public static final long MILLIS_IN_DAY = 60 * 60 * 24 * 1000;     // milliseconds in one day
    public static final long MILLIS_IN_HOUR = 60 * 60 * 1000;        // millliseconds in one hour
    public static final long MILLIS_IN_MINUTE = 60 * 1000;           // milliseconds in one minutes
    public static final long HOURS_IN_DAY = 24;

    public static final long BYTES_IN_KB = 1024;
    public static final long BYTES_IN_MB = BYTES_IN_KB * BYTES_IN_KB;

    public Step() {
         this.job = null;
        this.stepName = null;
        this.commandPrefix = null;
        this.inputType = null;
        this.inputArgument = null;
        this.inputPath = null;
        this.inputRegex = null;
        this.processes = -1;
        this.outputType = null;
        this.outputArgument = null;
        this.outputPath = null;
        this.commandSuffix = null;
        this.timeout = -1;
        this.ExistNoEmptyOutputFsNode = false;
        this.EmptyStderr = false;
        this.CheckLogFileSize = false;
        this.pathToLog = null;
        this.logSizeLessThan = -1;
        this.EqualInToOutFiles = false;
        this.EqualInToOutFilesSecondOutput = false;
        this.pathToSecondOutputFolder = null;
        this.saveStderr = false;
        this.saveErrLog = false;
        this.pathToErrLog = null;
        this.SynchronizedStep = false;
        this.stepOrder = -1;
    }

    public Step(Job job, String stepName, String commandPrefix, IOtypesEnum inputType, String inputArgument, String inputPath, String inputRegex, int processes, IOtypesEnum outputType, String outputArgument, String outputPath, String commandSuffix, long timeout, boolean ExistNoEmptyOutputFsNode, boolean EmptyStderr, boolean CheckLogFileSize, String pathToLog, long logSizeLessThan, boolean EqualInToOutFiles, boolean EqualInToOutFilesSecondOutput, String pathToSecondOutputFolder, boolean saveStderr, boolean saveErrLog, String pathToErrLog, boolean SynchronizedStep, int stepOrder) {
        this.job = job;
        this.stepName = stepName;
        this.commandPrefix = commandPrefix;
        this.inputType = inputType;
        this.inputArgument = inputArgument;
        this.inputPath = inputPath;
        this.inputRegex = inputRegex;
        this.processes = processes;
        this.outputType = outputType;
        this.outputArgument = outputArgument;
        this.outputPath = outputPath;
        this.commandSuffix = commandSuffix;
        this.timeout = timeout;
        this.ExistNoEmptyOutputFsNode = ExistNoEmptyOutputFsNode;
        this.EmptyStderr = EmptyStderr;
        this.CheckLogFileSize = CheckLogFileSize;
        this.pathToLog = pathToLog;
        this.logSizeLessThan = logSizeLessThan;
        this.EqualInToOutFiles = EqualInToOutFiles;
        this.EqualInToOutFilesSecondOutput = EqualInToOutFilesSecondOutput;
        this.pathToSecondOutputFolder = pathToSecondOutputFolder;
        this.saveStderr = saveStderr;
        this.saveErrLog = saveErrLog;
        this.pathToErrLog = pathToErrLog;
        this.SynchronizedStep = SynchronizedStep;
        this.stepOrder = stepOrder;
    }

    public Long getId() {
        return id;
    }

    public List<StepRunData> getStepRunData() {
        return stepRunData;
    }

    public void setStepRunData(List<StepRunData> StepRunData) {
        this.stepRunData = StepRunData;
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

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }

    public IOtypesEnum getInputType() {
        return inputType;
    }

    public void setInputType(IOtypesEnum inputType) {
        this.inputType = inputType;
    }

    public String getInputArgument() {
        return inputArgument;
    }

    public void setInputArgument(String inputArgument) {
        this.inputArgument = inputArgument;
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

    public int getProcesses() {
        return processes;
    }

    public void setProcesses(int processes) {
        this.processes = processes;
    }

    public IOtypesEnum getOutputType() {
        return outputType;
    }

    public void setOutputType(IOtypesEnum outputType) {
        this.outputType = outputType;
    }

    public String getOutputArgument() {
        return outputArgument;
    }

    public void setOutputArgument(String outputArgument) {
        this.outputArgument = outputArgument;
    }    

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getCommandSuffix() {
        return commandSuffix;
    }

    public void setCommandSuffix(String commandSuffix) {
        this.commandSuffix = commandSuffix;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isExistNoEmptyOutputFsNode() {
        return ExistNoEmptyOutputFsNode;
    }

    public void setExistNoEmptyOutputFsNode(boolean ExistNoEmptyOutputFsNode) {
        this.ExistNoEmptyOutputFsNode = ExistNoEmptyOutputFsNode;
    }

    public boolean isEmptyStderr() {
        return EmptyStderr;
    }

    public void setEmptyStderr(boolean EmptyStderr) {
        this.EmptyStderr = EmptyStderr;
    }

    public boolean isCheckLogFileSize() {
        return CheckLogFileSize;
    }

    public void setCheckLogFileSize(boolean CheckLogFileSize) {
        this.CheckLogFileSize = CheckLogFileSize;
    }

    public String getPathToLog() {
        return pathToLog;
    }

    public void setPathToLog(String pathToLog) {
        this.pathToLog = pathToLog;
    }

    public long getLogSizeLessThan() {
        return logSizeLessThan;
    }

    public void setLogSizeLessThan(long logSizeLessThan) {
        this.logSizeLessThan = logSizeLessThan;
    }

    public boolean isEqualInToOutFiles() {
        return EqualInToOutFiles;
    }

    public void setEqualInToOutFiles(boolean EqualInToOutFiles) {
        this.EqualInToOutFiles = EqualInToOutFiles;
    }

    public boolean isEqualInToOutFilesSecondOutput() {
        return EqualInToOutFilesSecondOutput;
    }

    public void setEqualInToOutFilesSecondOutput(boolean EqualInToOutFilesSecondOutput) {
        this.EqualInToOutFilesSecondOutput = EqualInToOutFilesSecondOutput;
    }

    public String getPathToSecondOutputFolder() {
        return pathToSecondOutputFolder;
    }

    public void setPathToSecondOutputFolder(String pathToSecondOutputFolder) {
        this.pathToSecondOutputFolder = pathToSecondOutputFolder;
    }

    public boolean isSaveStderr() {
        return saveStderr;
    }

    public void setSaveStderr(boolean saveStderr) {
        this.saveStderr = saveStderr;
    }

    public boolean isSaveErrLog() {
        return saveErrLog;
    }

    public void setSaveErrLog(boolean saveErrLog) {
        this.saveErrLog = saveErrLog;
    }

    public String getPathToErrLog() {
        return pathToErrLog;
    }

    public void setPathToErrLog(String pathToErrLog) {
        this.pathToErrLog = pathToErrLog;
    }

    public boolean isSynchronizedStep() {
        return SynchronizedStep;
    }

    public void setSynchronizedStep(boolean SynchronizedStep) {
        this.SynchronizedStep = SynchronizedStep;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }    

    @Override
    public String toString() {
        return "Step{" + "id=" + id
                + ", stepName=" + stepName
                + ", commandPrefix=" + commandPrefix
                + ", inputType=" + inputType
                + ", inputArgument=" + inputArgument
                + ", inputPath=" + inputPath
                + ", inputRegex=" + inputRegex
                + ", processes=" + processes
                + ", outputType=" + outputType
                + ", outputArgument=" + outputArgument
                + ", outputPath=" + outputPath
                + ", commandSuffix=" + commandSuffix
                + ", timeout=" + timeout
                + ", CheckExistNoEmptyOutputFsNode=" + ExistNoEmptyOutputFsNode
                + ", EmptyStderr=" + EmptyStderr
                + ", CheckLogFileSize=" + CheckLogFileSize
                + ", pathToLog=" + pathToLog
                + ", logSizeLessThan=" + logSizeLessThan
                + ", EqualInToOutFiles=" + EqualInToOutFiles
                + ", EqualInToOutFilesSecondOutput=" + EqualInToOutFilesSecondOutput
                + ", pathToSecondOutputFolder=" + pathToSecondOutputFolder
                + ", saveStderr=" + saveStderr
                + ", saveErrLog=" + saveErrLog
                + ", pathToErrLog=" + pathToErrLog
                + ", SynchronizedStep=" + SynchronizedStep
                + ", stepOrder=" + stepOrder
                + '}';
    }
}
