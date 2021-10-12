/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages;


/**
 * Message, in which the Secondary node sends its name to Primary node when it is connecting to it.
 * @author Filip
 */
public class NodeNameMsg extends GenericParamSocketMsg<String> {
    public NodeNameMsg(String nodeName) {
        super(MsgType.NODE_NAME, nodeName);
    }
}
