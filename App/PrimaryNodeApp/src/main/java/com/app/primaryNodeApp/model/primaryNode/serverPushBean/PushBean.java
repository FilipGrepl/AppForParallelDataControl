/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.serverPushBean;

import com.app.primaryNodeApp.model.primaryNode.serverPushBean.enums.FirePushEvents.FirePushEventsEnum;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.Eager;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;

/**
 * The bean for pushing events from backend to client side.
 * @author filip
 */
@Named
@Eager
@ApplicationScoped
public class PushBean implements Serializable, Runnable {
    
    /** STATIC PROPERTIES **/
    public static final int PUSH_REPEAT_DELAY = 1000;

    /** OBJECT PROPERTIES **/
    
    @Inject
    @Push(channel = "nodeEventChannel")
    private PushContext nodeEventPush;
    
    @Inject
    @Push(channel = "runTaskEventChannel")
    private PushContext runTaskPush;
    
    private final AtomicBoolean nodeEventUpdateRequest;       
    private final AtomicBoolean runTaskEventUpdateRequest; 
    
    private ScheduledExecutorService execService;
    private ScheduledFuture<?> scheduledFuture;
    
    
    /** OBJECT METHODS **/
    public PushBean() {
        this.nodeEventUpdateRequest = new AtomicBoolean(false);
        this.runTaskEventUpdateRequest = new AtomicBoolean(false);
    }
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    public void postConstruct() {
        execService = Executors.newScheduledThreadPool(1);
        scheduledFuture = execService.scheduleAtFixedRate(this, 0,  PUSH_REPEAT_DELAY, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Method which is called before the bean is unset from scope.
     */
    @PreDestroy
    public void preDestroy() {
        scheduledFuture.cancel(true);
        execService.shutdown();
    }
    
    
    /**
     * Method, that sends update event to client side by push channel.
     * @param event Event to be processed.
     */
    public void update(@Observes FirePushEventsEnum event) {
        switch(event) {
            case NODE_EVENT:
                this.nodeEventUpdateRequest.set(true);
                break;
            case RUN_TASK_EVENT:
                this.runTaskEventUpdateRequest.set(true);
                break;
        }
    }
    
    /**
     * Method, that periodically checks if there is request to send push event.
     */
    @Override
    public void run() {
        if (this.nodeEventUpdateRequest.getAndSet(false)) {
            nodeEventPush.send(FirePushEventsEnum.NODE_EVENT.getMessage());
        }
        if (this.runTaskEventUpdateRequest.getAndSet(false)) {
            runTaskPush.send(FirePushEventsEnum.RUN_TASK_EVENT.getMessage());
        }
    }
    
}
