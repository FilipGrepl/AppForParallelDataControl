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
public class RunValues implements Serializable {
    
    
    public static enum RunType {
        RUN_WITH_ERRORS("RUN_WITH_ERRORS"),
        RUN_WITHOUT_ERRORS("RUN_WITHOUT_ERRORS"),
        RUN_ONLY_ERRORS("RUN_ONLY_ERRORS");
        
        private final String message;

        RunType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    
    private Step step;
    private List<String> errInputs;
    private RunType runType;

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public List<String> getErrInputs() {
        return errInputs;
    }

    public void setErrorInputs(List<String> errInputs) {
        this.errInputs = errInputs;
    }

    public RunType getRunType() {
        return runType;
    }

    public void setRunType(RunType runType) {
        this.runType = runType;
    }

    @Override
    public String toString() {
        return "RunValues{" + "step=" + step + ", errInputs=" + errInputs + ", runType=" + runType + '}';
    }
}
