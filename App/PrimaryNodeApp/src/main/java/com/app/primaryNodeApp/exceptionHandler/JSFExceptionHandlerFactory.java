/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.exceptionHandler;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 * Exception handler factory for creating JSF Exception handler.
 * @author filip
 */
public class JSFExceptionHandlerFactory extends ExceptionHandlerFactory {

    /** OBJECT PROPERTIES **/
    
    private final ExceptionHandlerFactory parent;
    
    /** OBJECT METHODS **/
          
    /**
     * Getter of parent exception handler factory.
     * @param parent Parent exception handler.
     */
    public JSFExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    /**
     * Getter of exception handler.
     * @return JSFExceptionHandler instance.
     */
    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = new JSFExceptionHandler(parent.getExceptionHandler());
        return result;
    }
}
