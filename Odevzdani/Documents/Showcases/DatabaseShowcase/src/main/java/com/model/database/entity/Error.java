/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

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
 *
 * @author Filip
 */

@Entity
@Table(name = "Error")
public class Error implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_errorID")
    private Long errorID;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "logFile", columnDefinition="LONGTEXT")
    private String logFile;

    @Column(name = "stderr", columnDefinition="LONGTEXT")
    private String stderr;
    
    @Column(name = "description", columnDefinition="LONGTEXT")
    private String description;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FK_jobID", referencedColumnName = "job"),
        @JoinColumn(name = "FK_nodeID", referencedColumnName = "node"),
        @JoinColumn(name = "FK_stepID", referencedColumnName = "step")
    })
    private JobRunData jobRunData;
    
    public Error() {

    }

    public Error(String fileName, String logFile, String stderr, String description, JobRunData jobRunData) {
        this.fileName = fileName;
        this.logFile = logFile;
        this.stderr = stderr;
        this.description = description;
        this.jobRunData = jobRunData;
    }

    public long getErrorID() {
        return errorID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public JobRunData getJobRunData() {
        return jobRunData;
    }

    public void setJobRunData(JobRunData JobRunData) {
        this.jobRunData = JobRunData;
    }
}
