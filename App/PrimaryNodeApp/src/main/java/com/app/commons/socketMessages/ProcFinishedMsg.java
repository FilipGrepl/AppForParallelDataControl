/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;

import com.app.commons.socketMessages.sendingItems.FinishValues;

/**
 * Message, which is sent from Secondary to Primary node if an execution is ended.
 * @author Filip
 */
public class ProcFinishedMsg extends GenericParamSocketMsg<FinishValues> {
    public ProcFinishedMsg(FinishValues finishValues) {
        super(MsgType.PROC_FINISHED, finishValues);
    }
}
