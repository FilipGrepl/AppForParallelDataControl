/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;

import com.app.commons.socketMessages.sendingItems.RunValues;

/**
 * Message, which is sent from Primary to Secondary node to start execution of step.
 * @author Filip
 */
public class RunStepMsg extends GenericParamSocketMsg<RunValues> {
    public RunStepMsg(RunValues runValues) {
        super(MsgType.RUN_JOB_STEP, runValues);
    }
}
