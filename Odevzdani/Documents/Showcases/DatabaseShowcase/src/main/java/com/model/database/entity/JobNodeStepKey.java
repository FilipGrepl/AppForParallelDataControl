/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
/**
 *
 * @author Filip
 */

@Embeddable
public class JobNodeStepKey implements Serializable{
    
    private long job;
    private long node;
    private long step;
    
    public JobNodeStepKey () {
        
    }
    
    public JobNodeStepKey (long job, long node, long step) {
        this.job = job;
        this.node = node;
        this.step = step;
    }

    public long getJob() {
        return job;
    }

    public void setJob(long job) {
        this.job = job;
    }

    public long getNode() {
        return node;
    }

    public void setNode(long node) {
        this.node = node;
    }

    public long getStep() {
        return step;
    }

    public void setStep(long step) {
        this.step = step;
    } 
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobNodeStepKey)) return false;
        JobNodeStepKey that = (JobNodeStepKey) o;
        return Objects.equals(getJob(), that.getJob()) &&
                Objects.equals(getNode(), that.getNode()) &&
                Objects.equals(getStep(), that.getStep());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getJob(), getNode(), getStep());
    }
}
