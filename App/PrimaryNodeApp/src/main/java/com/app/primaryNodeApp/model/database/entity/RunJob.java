/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The RunJob entity.
 * @author Filip
 */

@Entity
@Table(name = "RunJob")
public class RunJob implements Serializable, EntityInterface<Long> {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_runJobID")
    private Long id;

    @OneToMany(mappedBy="runJob", cascade = CascadeType.ALL)
    List<StepRunData> stepRunData = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "FK_jobID", nullable = false)
    private Job job;

    @Column(name = "startedAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startedAt;
    
    @Column(name = "finishedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;
    
    public RunJob () {
        
    }
    
    public RunJob (Job job, Date startedAt, Date finishedAt) {
        this.job = job;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public Long getId() {
        return id;
    }
    
    public List<StepRunData> getStepRunData() {
        return stepRunData;
    }

    public void setStepRunData(List<StepRunData> stepRunData) {
        this.stepRunData = stepRunData;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }
    
    @Override
    public String toString() {
        return 
            "RunJob{" + 
                "id=" + id +
                ", startedAt=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startedAt) +
                ", finishedAt=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(finishedAt) +
            '}';
    }

    
    
}
