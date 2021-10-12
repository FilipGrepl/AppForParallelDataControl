/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;

import java.io.Serializable;

/**
 * Class, which represents the message of uknown type without data.
 * @author Filip
 */
public abstract class GenericSocketMsg implements Serializable {
    public enum MsgType {
        NODE_NAME,
        ACCEPT_CONNECTION,
        DECLINE_CONNECTION,
        RUN_JOB_STEP,
        STOP_JOB_STEP,
        USAGE_OF_RESOURCES,
        RUN_JOB_STEP_ACK,
        STOP_JOB_STEP_ACK,
        PROC_FINISHED,
        PROC_ERROR,
        RERUN_JOB_STEP,
        RERUN_JOB_STEP_ACK,
        IS_ALIVE
    }
    
    private final MsgType msgType;
    
    /**
     * Constructor.
     * @param msgType Type of message. 
     */
    public GenericSocketMsg(MsgType msgType) {
        this.msgType = msgType;
    }
    
    /**
     * Getter of message type.
     * @return The type of message.
     */
    public MsgType getMessageType() {
        return this.msgType;
    }
}
