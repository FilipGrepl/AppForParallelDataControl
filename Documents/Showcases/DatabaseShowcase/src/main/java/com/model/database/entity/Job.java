/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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
 *
 * @author Filip
 */

@Entity
@Table(name = "Job")
public class Job implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_jobID")
    private Long jobID;
    
    @Column(name = "jobName")
    private String jobName;
    
    @OneToMany(mappedBy="job", cascade = CascadeType.ALL)
    Set<JobRunData> jobRunData = new HashSet<>();
    
    @OneToMany(mappedBy="job", cascade = CascadeType.ALL)
    Set<Step> jobSteps = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "Job_Node", 
        joinColumns = { @JoinColumn(name = "jobID") }, 
        inverseJoinColumns = { @JoinColumn(name = "nodeID") }
    )
    Set<Node> jobNodes = new HashSet<>();
    
    public Job () {
        
    }
    
    public Job (String jobName) {
        this.jobName = jobName;
    }

    public long getJobID() {
        return jobID;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Set<JobRunData> getJobRunData() {
        return jobRunData;
    }

    public void setJobRunData(Set<JobRunData> jobRunData) {
        this.jobRunData = jobRunData;
    }

    public Set<Step> getSteps() {
        return jobSteps;
    }

    public void setSteps(Set<Step> steps) {
        this.jobSteps = steps;
    }

    public Set<Node> getNodes() {
        return jobNodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.jobNodes = nodes;
    }
}
