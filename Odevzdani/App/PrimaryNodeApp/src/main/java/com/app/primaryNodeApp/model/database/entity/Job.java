/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The Job entity.
 * @author Filip
 */
@Entity
@Table(name = "Job")
public class Job implements Serializable, EntityInterface<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_jobID")
    private Long id;

    @Expose
    @Column(name = "jobName")
    private String jobName;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    List<RunJob> runJobs = new ArrayList<>();

    @Expose
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Step> jobSteps = new ArrayList<>();

    @Expose
    @ManyToMany
    @JoinTable(
            name = "Job_Node",
            joinColumns = {
                @JoinColumn(name = "jobID")},
            inverseJoinColumns = {
                @JoinColumn(name = "nodeID")}
    )
    List<Node> jobNodes = new ArrayList<>();

    public Job() {

    }

    public Job(String jobName) {
        this.jobName = jobName;
    }

    public Long getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public List<RunJob> getRunJobs() {
        return runJobs;
    }

    public void setRunJobs(List<RunJob> runJobs) {
        this.runJobs = runJobs;
    }

    public List<Step> getJobSteps() {
        return jobSteps;
    }

    public void setJobSteps(List<Step> jobSteps) {
        this.jobSteps = jobSteps;
    }

    public List<Node> getJobNodes() {
        return jobNodes;
    }

    public void setJobNodes(List<Node> jobNodes) {
        this.jobNodes = jobNodes;
    }

    @Override
    public String toString() {
        return "Job{" + "id=" + id + ", jobName=" + jobName + '}';
    }

}
