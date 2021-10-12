/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.enums;

/**
 * The enumerator which represents status of node.
 * @author Filip
 */
public class NodeStatus {

    /**
     * STATIC PROPERTY *
     */
    public static enum NodeStatusEnum {
        OCCUPIED("Obsazen"),
        FREE("Volný"),
        DISCONNECTED("Odpojen");
        
        private final String message;

        NodeStatusEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

}