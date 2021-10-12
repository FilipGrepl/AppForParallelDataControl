/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.model.database.entity.Error;
import java.io.Serializable;


/**
 * The data of error detail which occurs on secondary node.
 * @author filip
 */
public class ErrorDetail implements Serializable {
    
    /** OBJECT PROPERTIES **/
    
    private final Error error;
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor.
     * @param error The error to be displayed.
     */
    public ErrorDetail(Error error) {
        this.error = error;
    }
  
    /**
     * Method, which returns true if the error contains content of log.
     * @return True if the error contains content of log. False otherwise.
     */
    public boolean isRenderLogContentButton() {
        if (error.getLogFile() == null)
            return false;
        else
            return true;
    }
    
    /**
     * Method, which returns true if the error contains content of stderr.
     * @return True if the error contains content of stderr. False otherwise.
     */
    public boolean isRenderStderrContentButton() {
        if (error.getStderr() == null)
            return false;
        else
            return true;
    }
    
    /** GETTERS AND SETTERS **/
    
    public Error getError() {
        return error;
    } 
}
