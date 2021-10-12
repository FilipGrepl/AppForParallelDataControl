/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;

import com.app.commons.socketMessages.sendingItems.ErrorValues;

/**
 * Message, which is sent from Secondary to Primary node if an error during execution occurs.
 * @author Filip
 */
public class ProcErrorMsg extends GenericParamSocketMsg<ErrorValues> {
    
    public ProcErrorMsg (ErrorValues errorValues) {
        super(MsgType.PROC_ERROR, errorValues);
    }
}