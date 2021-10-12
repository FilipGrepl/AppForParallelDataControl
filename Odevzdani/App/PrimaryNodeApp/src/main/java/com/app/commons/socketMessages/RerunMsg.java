/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;

import com.app.commons.socketMessages.sendingItems.RerunValues;

/**
 * TODO
 * @author Filip
 */
public class RerunMsg extends GenericParamSocketMsg<RerunValues> {
    public RerunMsg(RerunValues rerunValues) {
        super(MsgType.RERUN_JOB_STEP, rerunValues);
    }
}
