/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.primLoggers;

import com.app.commons.eventLogger.EventLogger;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for logging events in Dao objects.
 * @author Filip
 */
public class DaoLogger {
    
    /** OBJECT PROPERTIES **/
    
    private final Logger logger;
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor
     * @param classType The type of class for which the logger to be created.
     */
    public DaoLogger(final Class classType) {
        logger = EventLogger.getInstance().getLogger(classType.getName());
    }
    
    public void logDuplicateNodeNameErr(final String nodeName, final String IDs) {
        logger.log(Level.SEVERE, "There is duplicate node in database with the same name: {0} with IDs: {1}",
                new Object[]{nodeName, IDs});
    }
    
    public void logDuplicateStepOrderErr(final long jobID, final String IDs) {
        logger.log(Level.SEVERE, "There is duplicate step in database with the same step order at job with ID: {0} step IDs: {1}", 
                    new Object[]{jobID, IDs});
    }
    
    public void logDuplicateErrStatus(final String nodeName, final String IDs) {
        logger.log(Level.SEVERE, "There is more than one step which has status {0} or {1} on {2} node. {3} IDs: {4}", 
                new Object[]{StepStatusEnum.ERROR, StepStatusEnum.RUNNING_ERROR, nodeName, StepRunData.class.getName(), IDs});
    }
    
    public void logDuplicateRunningStepOnOneNode(final String nodeName, final String IDs) {
        logger.log(Level.SEVERE, "There is more than one step which has status {0} or {1} on {2} node. {3} IDs: {4}", 
                new Object[]{StepStatusEnum.RUNNING, StepStatusEnum.RUNNING_ERROR, nodeName, StepRunData.class.getName(), IDs});
    }
    
    public void logMissingStepRunDataRecErr(final RunJobNodeStepKey RJNSkey) {
        logger.log(Level.SEVERE, "Step with ID: {0} and Node with ID: {1} and RunJob with ID: {2} hasn't record in StepRunData table", 
                new Object[]{RJNSkey.getStepID(), RJNSkey.getNodeID(), RJNSkey.getRunJobID()});
    }
    
    public void logInternalDatabaseErr(final Throwable e) {
        logger.log(Level.SEVERE, "Internal communication with database:\n\n{0}\n\n{1}", 
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
    
    public void logInterruptedDatabaseThreadException(final Throwable e) {
        logger.log(Level.SEVERE, "Thread that waits to available connection for opening the session was interrupted. Error is:\n\n{0}\n\n{1}", 
                new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
    }
}
