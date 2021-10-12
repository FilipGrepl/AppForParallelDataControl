package beans;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.cdi.Eager;

@Named
@ApplicationScoped
@Eager
public class PushBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(PushBean.class.getName());
    private static final String MESSAGE = "This message has been pushed by websocket :)";
    
    @Inject
    @Push(channel = "eventChannel")
    private PushContext push;

    @Inject
    EventCreateBean EventBean;
    
    @PostConstruct
    void initialize() {
        EventBean.StartEvents();
    }
    
    public void eventAction() {
        
        LOG.log(Level.INFO, MESSAGE);
        push.send(MESSAGE);
    }
    
    public void update(@Observes String arg) {
        push.send("-----> Recieved event from Singleton bean! -> Then this message has been pushed! <-----");
    }

}
