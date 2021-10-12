/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.exceptionHandler;

import java.util.Iterator;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 * JSF exception handler for handling runtime exceptions and forward user to
 * error page, where the error descriptions are displayed.
 *
 * @author filip
 */
public class JSFExceptionHandler extends ExceptionHandlerWrapper {

    /**OBJECT PROPERTIES **/
    
    private final ExceptionHandler wrapped;

    /** OBJECT METHODS **/
    
    /**
     * Constructor.
     * @param wrapped Parent exception handler.
     */
    public JSFExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Getter of parent exception handler.
     * @return Parent exception handler.
     */
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    /**
     * Method, that is called for handling exceptions.
     * @throws FacesException If the FacesException occurs.
     */
    @Override
    public void handle() throws FacesException {
        Iterator iterator = getUnhandledExceptionQueuedEvents().iterator();

        while (iterator.hasNext()) {
            ExceptionQueuedEvent event = (ExceptionQueuedEvent) iterator.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            Throwable throwable = context.getException();
            System.out.println(throwable.getMessage());
            FacesContext fc = FacesContext.getCurrentInstance();

            try {
                Flash flash = fc.getExternalContext().getFlash();

                // Put the exception in the flash scope to be displayed in the error page
                flash.put("errorDetails", throwable.getMessage());

                NavigationHandler navigationHandler = fc.getApplication().getNavigationHandler();

                navigationHandler.handleNavigation(fc, null, "errorPage?faces-redirect=true");

                fc.renderResponse();
            } finally {
                iterator.remove();
            }
        }

        // Let the parent handle the rest
        getWrapped().handle();
    }
}
