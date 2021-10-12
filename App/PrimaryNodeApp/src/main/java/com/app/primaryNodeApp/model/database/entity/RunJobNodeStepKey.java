/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 * The key for RunJobNode entity.
 * @author Filip
 */

@Embeddable
public class RunJobNodeStepKey implements Serializable{
        
    private Long runJob;
    private Long node;
    private Long step;
    
    public RunJobNodeStepKey () {
        
    }
    
    public RunJobNodeStepKey (Long runJobID, Long nodeID, Long stepID) {
        this.runJob = runJobID;
        this.node = nodeID;
        this.step = stepID;
    }

    public Long getRunJobID() {
        return runJob;
    }

    public void setRunJobID(Long runJobID) {
        this.runJob = runJobID;
    }

    public Long getNodeID() {
        return node;
    }

    public void setNodeID(Long nodeID) {
        this.node = nodeID;
    }

    public Long getStepID() {
        return step;
    }

    public void setStepID(Long stepID) {
        this.step = stepID;
    } 
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunJobNodeStepKey)) return false;
        RunJobNodeStepKey that = (RunJobNodeStepKey) o;
        return Objects.equals(getRunJobID(), that.getRunJobID()) &&
                Objects.equals(getNodeID(), that.getNodeID()) &&
                Objects.equals(getStepID(), that.getStepID());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getRunJobID(), getNodeID(), getStepID());
    }

    @Override
    public String toString() {
        return 
            "RunJobNodeStepKey{" + 
                "runJob=" + runJob + 
                ", node=" + node + 
                ", step=" + step + 
            '}'; 
    }
}
