/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages.sendingItems;

import java.text.MessageFormat;

/**
 * The class with content of decline messages.
 * @author filip
 */
public class DeclineValues {
    
    /** STATIC PROPERTY **/ 
    
    private static enum DeclineErrorsContent {
        DIFFERENT_NAME("This IP address is saved at node with name {0}. Change the name of node in primary node application."),
        ALREADY_CONNECTED("Node with this name is already connected from different IP address."),
        DIFFERENT_IP("Node with this name is connected from different IP than expected. Your IP: {0}. Expected IP is: {1}. Reset IP address in primary node application.");
        
        private final String message;

        DeclineErrorsContent(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }
    }
    
    /** STATIC METHODS **/
    
    public static String getDifferentNameErr(String savedNodeName) {
        return MessageFormat.format(DeclineErrorsContent.DIFFERENT_NAME.getMessage(), savedNodeName);
    }
    
    public static String getAlreadyConnectedErr() {
        return DeclineErrorsContent.ALREADY_CONNECTED.getMessage();
    }
    
    public static String getDifferentIPErr(String remoteIP, String savedIP) {
        return MessageFormat.format(DeclineErrorsContent.DIFFERENT_IP.getMessage(), remoteIP, savedIP);
    }
}
