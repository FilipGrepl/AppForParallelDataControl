/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages.sendingItems;

import java.io.Serializable;

/**
 * The class, which represents the detail info about finished execution on Secondary node.
 * @author Filip
 */
public class FinishValues implements Serializable {
    
    
    private long execTime;
    private FinishType finishType;
    private String inputPath;
    private long inputSize;
    
    public enum FinishType {
        FINISH_FS_NODE, FINISH_ALL
    }
    
    public FinishValues(FinishType finishType, long execTime, String inputPath, long inputSize) {
        this.finishType = finishType;
        this.execTime = execTime;
        this.inputPath = inputPath;
        this.inputSize = inputSize;
    }
    
    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    public FinishType getFinishType() {
        return finishType;
    }

    public void setFinishType(FinishType finishType) {
        this.finishType = finishType;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public long getInputSize() {
        return inputSize;
    }

    public void setInputSize(long inputSize) {
        this.inputSize = inputSize;
    }

    @Override
    public String toString() {
        return "FinishValues{" + "execTime=" + execTime + ", finishType=" + finishType + ", inputPath=" + inputPath + ", inputSize=" + inputSize + '}';
    }
}
