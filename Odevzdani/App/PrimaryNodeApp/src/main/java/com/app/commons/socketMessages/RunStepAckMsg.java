/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * Message, which is sent from Secondary to Primary node as acknowledgement, that the execution is started.
 * @author Filip
 */
public class RunStepAckMsg extends GenericParamSocketMsg<Long> {
    public RunStepAckMsg(long numberOfFsNodes) {
        super(MsgType.RUN_JOB_STEP_ACK, numberOfFsNodes);
    }
}
