/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * Message, which is sent to Secondary node, if connection with this node is accepted.
 * @author Filip
 */
public class AcceptConnMsg extends GenericSocketMsg {
    public AcceptConnMsg() {
        super(MsgType.ACCEPT_CONNECTION);
    }
}
