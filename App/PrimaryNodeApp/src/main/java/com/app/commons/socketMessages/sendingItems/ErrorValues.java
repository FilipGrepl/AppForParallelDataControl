/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketMessages.sendingItems;

import com.app.primaryNodeApp.model.database.entity.Error;

/**
 * The class, which represents the detail info about error which is occurs during execution on Secondary node.
 * @author Filip
 */
public class ErrorValues extends FinishValues {
    
    private Error error;

    public ErrorValues(Error error, FinishType finishType, long execTime, String inputPath, long inputSize) {
        super(finishType, execTime, inputPath, inputSize);
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
   
    @Override
    public String toString() {
        return "ErrorValues{" + "error=" + error.toString() + ", finishValues=" + super.toString() + '}';
    }
}
