/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * Message, which is periodically sent to Primary or Secondary node to check if second node is still connected.
 * @author Filip
 */
public class IsAliveMsg extends GenericSocketMsg {
    public IsAliveMsg() {
        super(MsgType.IS_ALIVE);
    }
}
