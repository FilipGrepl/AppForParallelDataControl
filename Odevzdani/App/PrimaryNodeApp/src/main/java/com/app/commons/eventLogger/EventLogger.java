/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.eventLogger;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 * Event logger for logging all events on primary and secondary nodes.
 * @author Filip
 */
public class EventLogger {
    
    /** STATIC PROPERTIES **/
    
    private static FileHandler handler = null;
    private static EventLogger eventLogger = null;
    
    /** STATIC METHOD **/
    
    /**
     * 
     * @return The instance of singleton EventLogger object.
     */
    public static EventLogger getInstance() {
        if (eventLogger == null) {
            eventLogger = new EventLogger();
        }
        return eventLogger;
    }
    
    /**
     * Method for getting the error stack trace.
     * @param e Error for which the String stack trace could be returned.
     * @return Error stack trace as string.
     */
    public static String getErrorStackTrace(Throwable e) {
        StringWriter errorStack = new StringWriter();
        e.printStackTrace(new PrintWriter(errorStack));
        return errorStack.toString();
    }
    
    /** OBJECT METHOD **/
    
    private EventLogger() {      
    }
    
    /**
     * Method, that sets file handler.
     * @param pathToErrLog Path to log file.
     */
    public void addFileHandler(String pathToErrLog) {
        if (handler == null) {
            String pathToLogDir = pathToErrLog.contains("/") ? pathToErrLog.substring(0, pathToErrLog.lastIndexOf('/')) : pathToErrLog;
            File dir = new File(pathToLogDir);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Logger.getLogger(EventLogger.class.getName()).log(Level.SEVERE, "! ERROR ! - Path to error log doesn't exist and cannot be created.");
                    throw new RuntimeException("! ERROR ! - Path to error log doesn't exist and cannot be created.");
                }               
            }
            try {
                handler = new FileHandler(pathToErrLog);
            } catch (IOException | SecurityException e) {
                Logger.getLogger(EventLogger.class.getName()).log(Level.SEVERE, "! ERROR ! - Cannot open FileHandler to given path to log. Errors is: {0}"
                        + "toString: {1}", new Object[] {e.getMessage(), e.toString()});
                handler = null;
                throw new RuntimeException("! ERROR ! - Cannot open FileHandler to given path to log.");
            }
            Logger.getLogger("").addHandler(handler);
        }
    }
    
    /**
     * Method which returns the logger for specified class and sets its logger file.
     * @param classType Type of class which is logged.
     * @return Logger for specified class.
     */
    public Logger getLogger(String classType) {
        return Logger.getLogger(classType);        
    }
    
    /**
     * Closing the logger file.
     */
    public void closeFileHandler() {
        handler.close();
    }
}