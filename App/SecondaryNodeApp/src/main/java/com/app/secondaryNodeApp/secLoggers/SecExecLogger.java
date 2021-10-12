/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.secLoggers;

import com.app.commons.eventLogger.EventLogger;
import com.app.primaryNodeApp.model.database.entity.Step;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger for logging the error during the step execution.
 * @author Filip
 */
public class SecExecLogger {
    
    private final Logger execLogger;
    
    public SecExecLogger(final Class classType) {
        execLogger = EventLogger.getInstance().getLogger(classType.getName());
    }
    
     public void logErrCatchExecStep(final Step s, Throwable e) {
        execLogger.log(Level.SEVERE, "Error in sending IO error message to secondaryNode: {0}. Error is: {1}\n\n{2}", 
                new Object[]{s.toString(), e.toString(), EventLogger.getErrorStackTrace(e)});
    }
    
    public void logErrExecStep(final Step s, final String inputFsNode, Throwable e) {
        execLogger.log(Level.SEVERE, "Error in executing step: {0} on input: {1}. Error is: {2}\n\n{3}", 
                new Object[]{s.toString(), inputFsNode, e.toString(), EventLogger.getErrorStackTrace(e)});
    }
    
    public void logErrWaitToUsageThread(final Throwable e) {
        execLogger.log(Level.SEVERE, "Error in waiting to end usageThread when the step executing has been stopped. Error is: {0}\n\n{1}",
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
    
    public void logErrUsageCmdExec(final String stderr, final long exitValue) {
        execLogger.log(Level.SEVERE, "Error occurs during executing UsageCmd. Stderr is: {0} and exit value is {1}.", new Object[]{stderr, exitValue});
    }
           
    public void logErrInterruptUsageThread(final Throwable e) {
        execLogger.log(Level.INFO, "Usage thread has been interrupted during executing UsageCmd. Error is: {0}\n\n{1}",
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
    
    public void logErrUsageConvert(final Throwable e) {
        execLogger.log(Level.SEVERE, "Error in formating usage data (RAM and CPU) from String to Double. Error is: {0}\n\n{1}",
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
            
    public void logIOErrUsageThread(final Throwable e) {
        execLogger.log(Level.SEVERE, "Error occurs in Usage Exec thread. Error is: {0}\n\n{1}",
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
            
    public void logErrGetPid(final Throwable e) {
        execLogger.log(Level.SEVERE, "Error occurs when the PID of Usage process has been extracted. Error is: {0}\n\n{1}",
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
}
