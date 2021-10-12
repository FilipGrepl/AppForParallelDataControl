/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.primLoggers;

import com.app.commons.eventLogger.SocketEventLogger;
import java.io.IOException;
import java.util.logging.Level;

/**
 * The logger for logging the errors in socket communication with Secondary node.
 * @author Filip
 */
public class PrimSocketEventLogger extends SocketEventLogger {
    
    /** OBJECT PROPERTIES **/
    
    /**
     * Constructor
     * @param classType The type of class for which the logger to be created.
     * @param nodeIP IP address of primary ndoe. 
     * @param nodePort Port of primary node.
     * @throws IOException In an IOException occurs.
     */
    public PrimSocketEventLogger(final Class classType, final String nodeIP, final int nodePort) throws IOException {
        super(NodeType.PRIMARY_NODE,classType, nodeIP, nodePort);
    }
    
    public void logNodeDuplicateErr() {
        socketLogger.log(Level.WARNING, "Node with IP: {0} and port: {1} is tried connect with name of node {2}, which is already connected", 
                new Object[]{nodeIP, nodePort, nodeName});
    }
    
    public void logDifferentIPErr(String savedNodeName, String savedIP) {
        socketLogger.log(Level.WARNING, "Node with saved name: {0} and IP: {1} is trying to connect from different IP address: {2}", 
                new Object[]{savedNodeName, savedIP, nodeIP});
    }
    
    public void logDifferentNameErr(String savedNodeName, String savedIP) {
        socketLogger.log(Level.WARNING, "Node with saved IP: {0} and name: {1} is trying to connect with different name: {2}",
                new Object[]{savedIP, savedNodeName, nodeName});
    }
}

