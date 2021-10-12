/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.eventLogger;

import com.app.commons.socketMessages.GenericParamSocketMsg;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketMessages.GenericSocketMsg.MsgType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger for logging events of socket communication between Primary and Secondary nodes.
 * @author Filip
 */
public class SocketEventLogger {
    protected enum NodeType {
        PRIMARY_NODE("Secondary"), SECONDARY_NODE("Primary");
        
        private final String nodeType;
        
        NodeType(String nodeType) {
            this.nodeType = nodeType;
        }
        
        String getNodeType() {
            return nodeType;
        }
    }
        
    /** OBJECT PROPERTY **/
    
    protected final Logger socketLogger;
    private final String nodeType;    
    protected final String nodeIP;
    protected final int nodePort;
    protected String nodeName;
    
    /** OBJECT METHOD **/    
    
    /**
     * Constructor - only child can call it.
     * @param nodeType Type of node - Secondary or Primary node.
     * @param classType Class for logging.
     * @param nodeIP IP address of Primary or Secondary node.
     * @param nodePort Port of Primary or Secondary node.
     */
    protected SocketEventLogger(final NodeType nodeType, final Class classType, final String nodeIP, final int nodePort) {
        socketLogger = EventLogger.getInstance().getLogger(classType.getName());
        this.nodeIP = nodeIP;
        this.nodePort = nodePort;    
        this.nodeType = nodeType.getNodeType();
    }
    
    /**
     * Getter of node identification.
     * @return The string with node name or with node IP and port, if the node name is not specified.
     */
    protected String getNodeIdentification() {
        if (nodeName == null)
            return "with IP: "+nodeIP+" and Port: "+nodePort+".";
        else
            return nodeName+".";
    }
    
    /**
     * Setter of node name.
     * @param nodeName Name of node.
     */
    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }  
    
    /* COMMON ERRORS */
    
    public void logUnknownObjectErr(final Object o) {
        socketLogger.log(Level.WARNING, "Unknown object {0}{1} has been received from "+nodeType+" node {2}.", 
                new Object[]{
                    (o == null ? "" : "of class"+o.getClass()),
                    (o == null ? "" : " ("+o.toString()+")"), 
                    getNodeIdentification()});
    }
    
    public void logUnexpMsgErr(final MsgType msgType, final String msgText) {
        socketLogger.log(Level.WARNING, "Unexpected {0} message with text {1} has been received from "+nodeType+" node {2}", 
                new Object[]{msgType, msgText, getNodeIdentification()});
    }
    public void logUnknownMsgTypeErr(final GenericSocketMsg message) {
        socketLogger.log(Level.SEVERE, "Unknown message type {0} {1} has been received from "+nodeType+" node with  {2}",
                new Object[]{message.getMessageType(), message.toString(), getNodeIdentification()});
    }
    
    public void logNoReceivedMsgErr(final MsgType msgType) {
        socketLogger.log(Level.WARNING, "Message {0} hasn't been received from "+nodeType+" node with IP: {1} and Port: {2}", 
                new Object[]{msgType, nodeIP, nodePort});
    }
    
    public void logCloseSocketErr(final Throwable ex) {
        socketLogger.log(Level.SEVERE, "Error in closing socket communication with "+nodeType+" node {0}\n\n{1}\n\n{2}", 
                new Object[]{ getNodeIdentification(), ex.toString(), EventLogger.getErrorStackTrace(ex)});
    }
    
    public void logSendError(final MsgType msgType, final Throwable ex) {
        socketLogger.log(Level.SEVERE, "Cannot send {0} message to "+nodeType+" node {1}\n\n{2}\n\n{3}",
                new Object[]{ msgType, getNodeIdentification(), ex.toString(), EventLogger.getErrorStackTrace(ex)});
    }
    
    public void logCloseConnErr(final Throwable ex) {
        socketLogger.log(Level.SEVERE, "Communication with "+nodeType+" node {0} is closed\n\n{1}\n\n{2}",
                new Object[]{getNodeIdentification(), ex.toString(), EventLogger.getErrorStackTrace(ex)});
    }
    
    public void logReceivedMessage(final MsgType msgType, final String msgText) {
        socketLogger.log(Level.INFO, "Message of type {0} with text \"{1}\" has been received from "+nodeType+" node {2}", 
                new Object[]{msgType, msgText, getNodeIdentification()});
    }
    
    public void logSentMessage(GenericSocketMsg message) {
        String messageContent;
        switch(message.getMessageType()) {
            case DECLINE_CONNECTION:
            case NODE_NAME:
            case RUN_JOB_STEP:
            case USAGE_OF_RESOURCES:
            case PROC_FINISHED:
            case PROC_ERROR:
            case RERUN_JOB_STEP:
                messageContent = ((GenericParamSocketMsg)message).getMessage().toString();
                break;
            default:
                messageContent = "";
                break;
        }
        socketLogger.log(Level.INFO, "Message of type {0} with text \"{1}\" has been sent to "+nodeType+" node {2}", 
                new Object[]{message.getMessageType(), messageContent, getNodeIdentification()});
    }
}
