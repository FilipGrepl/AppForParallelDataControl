/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.services.dataClasses.interfaces.EnumInterface;
import com.app.primaryNodeApp.model.database.entity.Step;
import java.io.Serializable;

/**
 * The data about new step of task.
 * @author filip
 */
public class NewTaskStepValues implements Serializable {
        
    /** STATIC PROPERTIES **/

    public static enum TimeoutUnitEnum implements EnumInterface {
        MINUTES("min"),
        HOURS("hod"),
        DAYS("dní");

        private final String message;

        TimeoutUnitEnum(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static enum LogSizeUnitEnum implements EnumInterface {
        B("B"),
        KB("kB"),
        MB("MB");

        private final String message;

        LogSizeUnitEnum(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    /** OBJECT PROPERTIES **/
    
    Long timeout;
    TimeoutUnitEnum timeoutUnit;

    Long logSize;
    LogSizeUnitEnum logSizeUnit;
    
    Step step;
    
    /** OBJECT METHODS **/

    public NewTaskStepValues() {
        timeout = null;
        timeoutUnit = TimeoutUnitEnum.MINUTES;
        logSize = null;
        logSizeUnit = LogSizeUnitEnum.B;
        step = null;
    }
    
    /** GETTERS AND SETTERS **/

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public TimeoutUnitEnum getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setTimeoutUnit(TimeoutUnitEnum timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

    public Long getLogSize() {
        return logSize;
    }

    public void setLogSize(Long logSize) {
        this.logSize = logSize;
    }

    public LogSizeUnitEnum getLogSizeUnit() {
        return logSizeUnit;
    }

    public void setLogSizeUnit(LogSizeUnitEnum logSizeUnit) {
        this.logSizeUnit = logSizeUnit;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }    
}
