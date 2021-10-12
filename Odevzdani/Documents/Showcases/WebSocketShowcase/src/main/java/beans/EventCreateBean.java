/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 *
 * @author Filip
 */
@Singleton
@Startup
public class EventCreateBean {

    private static final Logger LOG = Logger.getLogger(EventCreateBean.class.getName());
    
    @Inject
    Event<String> event;
     
     public void Hello() {
         LOG.log(Level.INFO, "printed hello");
     }

    public void StartEvents() {
        new Thread (() -> {
            try {
                while (true) {
                    event.fire("Event from server!");
                    LOG.log(Level.INFO, "EventCreateBean: Event created!");
                    Thread.sleep(3000);
                }
            }catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }).start();
    }
}
