/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages.sendingItems;

import java.io.Serializable;

/**
 * The class, which represents the detail info about usage of system resources during execution on Secondary node.
 * @author Filip
 */
public class UsageValues implements Serializable {
        
    private double cpuLoad;
    private double ramUsage;

    public UsageValues(double cpuLoad, double ramUsage) {
        this.cpuLoad = cpuLoad;
        this.ramUsage = ramUsage;
    }
    
    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public double getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(double ramUsage) {
        this.ramUsage = ramUsage;
    }
    
    @Override
    public String toString() {
        return "UsageValues{" + "cpuLoad=" + cpuLoad + ", ramUsage=" + ramUsage + '}';
    }    
}
