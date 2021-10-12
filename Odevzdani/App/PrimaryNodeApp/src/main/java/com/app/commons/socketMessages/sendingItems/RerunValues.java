/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages.sendingItems;

import com.app.primaryNodeApp.model.database.entity.Step;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author filip
 */
public class RerunValues implements Serializable {
    
    
    private List<String> errInputs;
    private Step step;
    
    public List<String> getErrInputs() {
        return errInputs;
    }
    
    public void setErrInputs(List<String> errInputs) {
        this.errInputs = errInputs;
    }
    
    public Step getStep() {
        return step;
    }
    
    public void setStep(Step step) {
        this.step = step;
    }
            
    @Override
    public String toString() {
        return "RerunValues{" + "errInputs=" + errInputs.toString() + ", step=" + step.toString() + '}';
    }
            
}
