/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.secConnStatusMaintainer;

import java.util.Stack;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Filip
 */
public class SecStatusMaintainer {
    /** STATIC PROPERY **/
    
    private static SecStatusMaintainer secStatusMaintainer = null;
    
    public static enum SecConnectionStatus {
        WAIT_TO_CONN_ACCEPT, WAIT_TO_RUN_TASK, RUN_TASK
    }
    
    /** OBJECT PROPERTY **/
    
    private final ReadWriteLock lockStopExecuting;
    private final ReadWriteLock lockConnStatus;
    
    private boolean stopExecuting;
    private SecConnectionStatus secConnStatus;
    
    
    private final Stack<String> inputs;                     // all input files or folders of actual executing step
       
    /** STATIC METHODS **/
    
    /**
     * @return Return the instance of singleton object of SecStatusMaintainer class.
     */
    public static SecStatusMaintainer getInstance() {
        if (secStatusMaintainer == null) {
            secStatusMaintainer = new SecStatusMaintainer();
        }
        return secStatusMaintainer;
    }
    
    /** OBJECT METHODS **/
   /**
    * Private constructor.
    */
    private SecStatusMaintainer() {
        lockStopExecuting = new ReentrantReadWriteLock();
        lockConnStatus = new ReentrantReadWriteLock();
        stopExecuting = true;
        secConnStatus = SecConnectionStatus.WAIT_TO_CONN_ACCEPT;
        inputs = new Stack<>();
    }
    
    /**
     * Check if it is request to stop executing of running step.
     * @return True if it is request to stop executing the running step. False otherwise.
     */
    public boolean isStopExecuting() {
        lockStopExecuting.readLock().lock();
        try {
            return stopExecuting;
        } finally {
            lockStopExecuting.readLock().unlock();
        }
    }
    
    /**
     * Setter of stop executing flag.
     * @param stopExecuting New value of stop executing flag.
     */
    public void setStopExecuting(boolean stopExecuting) {
        lockStopExecuting.writeLock().lock();
        try {
            this.stopExecuting = stopExecuting;
        } finally {
            lockStopExecuting.writeLock().unlock();
        }
    }

    /**
     * Getter of connection status.
     * @return The connection status.
     */
    public SecConnectionStatus getSecConnStatus() {
        lockConnStatus.readLock().lock();
        try {
            return secConnStatus;
        } finally {
            lockConnStatus.readLock().unlock();
        }
    }

    /**
     * Setter of connection status
     * @param secConnStatus New connection status.
     */
    public void setSecConnStatus(SecConnectionStatus secConnStatus) {
        lockConnStatus.writeLock().lock();
        try {
            this.secConnStatus = secConnStatus;
        } finally {
            lockConnStatus.writeLock().unlock();
        }
    }
    
    /**
     * Getter next input from all inputs of the executing step.
     * @return If the inputs stack is not empty, the next input from all inputs of the executing step is returned. Null otherwise.
     */
    public String getInput() {
        synchronized(inputs) {
            if (inputs.empty())
                return null;
            else
                return inputs.pop();
        }
    }
    
    /**
     * Add new input of the executing step to the inputs stack.
     * @param input New input file/folder.
     */
    public void addInput(String input) {
        synchronized(inputs) {
            inputs.push(input);
        }
    }
    
    /**
     * Check if inputs list is empty.
     * @return True if list is empty. False otherwise.
     */
    public boolean isEmptyInputs() {
        synchronized(inputs) {
            return inputs.isEmpty();
        }
    }
        
    /**
     * Clear inputs list.
     */
    public void clearInputs() {
        synchronized(inputs) {
            inputs.clear();
        }
    }
    
    public Stack<String> getCopyOfInputs() {
        synchronized(inputs) {
            return (Stack<String>)inputs.clone();
        }
    }
}
