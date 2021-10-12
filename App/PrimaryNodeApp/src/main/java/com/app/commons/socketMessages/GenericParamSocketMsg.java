/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * Class, which represents the Generic message with uknown data.
 * @author Filip
 * @param <T> Type of message content.
 */

public class GenericParamSocketMsg<T> extends GenericSocketMsg {
    
    private final T message;
    
    public GenericParamSocketMsg(MsgType msgType, T message) {
        super(msgType);
        this.message = message;
    }
    
    public T getMessage() {
        return this.message;
    }
}