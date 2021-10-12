/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses.enums;

/**
 * The enumerator of color of task status font.
 * @author filip
 */
public class StatusClass {

    /**
     * STATIC PROPERTY *
     */
    public static enum StatusClassEnum {
        GREEN_COLOR("text-green"),
        ORANGE_COLOR("text-orange"),
        BROWN_COLOR("text-brown"),
        BLUE_COLOR("text-blue"),
        RED_COLOR("text-red");

        private final String message;

        StatusClassEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
