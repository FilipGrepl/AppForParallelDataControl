/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer;

import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Class, which maintains data which corresponds with status between Primary node and one Secondary node.
 * @author filip
 */
public class PrimConnStatusMaintainer {
    
    /** OBJECT PROPERTY **/
    
    private PrimConnectionStatus connectionStatus;
    private Node secNode;
    private RunJobNodeStepKey RJNSkey;
    private final AtomicBoolean isNextStep;         
    
    public enum PrimConnectionStatus {
        RUN_TASK, NOT_RUN_TASK, WAIT_TO_RUN_ACK, WAIT_TO_STOP_ACK 
    }
    
    /** OBJECT METHOD **/
    
    public PrimConnStatusMaintainer() {
        connectionStatus = PrimConnectionStatus.NOT_RUN_TASK;
        secNode = null;
        RJNSkey = null;
        this.isNextStep = new AtomicBoolean(true);
    }
        
    /** GETTERS AND SETTERS **/

    /**
     * Getter of Primary node connection status.
     * @return Actual connection status.
     */
    public PrimConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * Setter of Primary node connection status.
     * @param connectionStatus New connection status.
     */
    public void setConnectionStatus(PrimConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    /**
     * Getter of data about connected Secondary node.
     * @return Connected Secondary node.
     */
    public Node getSecNode() {
        return secNode;
    }
    
    /**
     * Setter of data about connected Secondary node.
     * @param secNode Connected secondary node.
     */
    public void setSecNode(Node secNode) {
        this.secNode = secNode;
    }

    /**
     * Getter of Run_job-Node-Step key of current executing step on Secondary node. 
     * @return Run_job-Node-Step key of current executing step on Secondary node.
     */
    public RunJobNodeStepKey getRJNSkey() {
        return RJNSkey;
    }

    /**
     * Setter of Run_job-Node-Step key of current executing step on Secondary node. 
     * @param RJNSkey Run_job-Node-Step key of current executing step on Secondary node.
     */
    public void setRJNSkey(RunJobNodeStepKey RJNSkey) {
        this.RJNSkey = RJNSkey;
    }
    
    /**
     * Setter of IsNextStep flag - it represents he information if there is next feasible step to specific Secondary node.
     * @return True if there is next feasible step to specific concrete Secondary node. False otherwise.
     */
    public boolean getIsNextStep() {
        return isNextStep.get();
    }
    
    /**
     * Setter of IsNextStep flag - it represents the information if there is next feasible step to specific Secondary node.
     * @param newNalue New value of isNextStep flag.
     */
    public void setIsNextStep(boolean newNalue) {
        isNextStep.set(newNalue);
    }
}
