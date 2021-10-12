/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.serverPushBean.enums;

/**
 * The enumerator of server push events.
 * @author filip
 */
public class FirePushEvents {
    
     /** STATIC PROPERTY **/
    
    public static enum FirePushEventsEnum {
        NODE_EVENT("NODE_EVENT"),
        RUN_TASK_EVENT("RUN_TASK_EVENT");

        private final String message;

        FirePushEventsEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

