/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.secLoggers;

import com.app.commons.eventLogger.EventLogger;
import com.app.commons.eventLogger.SocketEventLogger;
import java.util.logging.Level;

/**
 * Logger for logging the errors in socket communication with Primary node.
 * @author Filip
 */
public class SecSocketEventLogger extends SocketEventLogger {
    
    public SecSocketEventLogger(final Class classType, final String nodeIP, final int nodePort) {
        super(NodeType.SECONDARY_NODE, classType, nodeIP, nodePort);
    }
    
    public void logDeclineConnection(String messageContent) {
        socketLogger.log(Level.SEVERE, "The connection from primary Node {0} will be declined - DECLINE message has been received. The reason is: {1}",
                new Object[]{getNodeIdentification(), messageContent});
    }
    
    public void logErrorJoinExecThread(final Throwable ex) {
        socketLogger.log(Level.SEVERE, "The errror occurs during waiting to stopped all exec threads\n\n{1}\n\n{2}",
                new Object[]{ex.toString(), EventLogger.getErrorStackTrace(ex)});
    }
}
