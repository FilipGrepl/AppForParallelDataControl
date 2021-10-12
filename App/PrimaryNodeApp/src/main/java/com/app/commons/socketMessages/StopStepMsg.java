/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * Message, which is sent from Primary to Secondary node as acknowledgement to stop the execution of step.
 * @author Filip
 */
public class StopStepMsg extends GenericSocketMsg {
    public StopStepMsg() {
        super(MsgType.STOP_JOB_STEP);
    }
}