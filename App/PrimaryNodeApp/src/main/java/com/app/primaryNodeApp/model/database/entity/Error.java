/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Error entity.
 * @author Filip
 */

@Entity
@Table(name = "Error")
public class Error implements Serializable, EntityInterface<Long> {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_errorID")
    private Long id;

    @Column(name = "pathToInputFile", length=1024)
    private String pathToInputFile;

    @Column(name = "logFile", columnDefinition="LONGTEXT")
    private String logFile;

    @Column(name = "stderr", columnDefinition="LONGTEXT")
    private String stderr;
    
    @Column(name = "description", columnDefinition="LONGTEXT")
    private String description;
    
    @Column(name = "command", length=1024)
    private String command;
    
    @Column(name = "exitCode")
    private int exitCode;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FK_runJobID", referencedColumnName = "runJob"),
        @JoinColumn(name = "FK_nodeID", referencedColumnName = "node"),
        @JoinColumn(name = "FK_stepID", referencedColumnName = "step")
    })
    private StepRunData stepRunData;
    
    public Error() {
        this.id = null;
        this.pathToInputFile = null;
        this.logFile = null;
        this.stderr = null;
        this.description = null;
    }

    public Error(String pathToInputFile, String logFile, String stderr, String description, String command, int exitCode, StepRunData stepRunData) {
        this.pathToInputFile = pathToInputFile;
        this.logFile = logFile;
        this.stderr = stderr;
        this.description = description;
        this.command = command;
        this.exitCode = exitCode;
        this.stepRunData = stepRunData;
    }

    public Long getId() {
        return id;
    }
    
    public String getPathToInputFile() {
        return pathToInputFile;
    }

    public void setPathToInputFile(String pathToInputFile) {
        this.pathToInputFile = pathToInputFile;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StepRunData getStepRunData() {
        return stepRunData;
    }

    public void setStepRunData(StepRunData stepRunData) {
        this.stepRunData = stepRunData;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }
    
    @Override
    public String toString() {
        return "Error{" + 
                "id=" + id + 
                ", pathToInputFile=" + pathToInputFile + 
                ", logFile=" + logFile + 
                ", stderr=" + stderr + 
                ", description=" + description + 
                ", command=" + command +
                ", exitCode=" + exitCode +
            '}';
    }
}
