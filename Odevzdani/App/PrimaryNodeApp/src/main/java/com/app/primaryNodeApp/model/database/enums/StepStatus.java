/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.enums;

/**
 * The enumerator which represents statuses of task.
 * @author Filip
 */
public class StepStatus {
    
    public static enum StepStatusEnum {
        RUNNING("Běží"),
        RUNNING_ERROR("Běží/chyba"),
        WAITING("Čeká"),
        PAUSED("Pozastaveno"),
        ERROR("Chyba"), 
        FINISHED("Dokončeno");


        private final String message;

        StepStatusEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}