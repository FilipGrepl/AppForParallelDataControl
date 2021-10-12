/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * TODO
 * @author Filip
 */
public class RerunAckMsg extends GenericSocketMsg {
    public RerunAckMsg() {
        super(MsgType.RERUN_JOB_STEP_ACK);
    }
}
