/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.primSocketCommunicators;

import com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer.PrimConnStatusMaintainer;
import com.app.primaryNodeApp.model.primaryNode.secNodeManager.SecNodeManager;
import java.io.IOException;
import javax.net.ssl.SSLSocket;

/**
 * Class for communication with Secondary Node.
 * @author Filip
 */
public class SecNodeCommunicator {
    
    /** OBJECT PROPERTIES **/
    
    private final PrimSocketReceiver primSocketReceiver;
    private final PrimSocketSender primSocketSender;
    private final PrimConnStatusMaintainer primStatusMaintainer;
    
    /** OBJECT METHOD **/
    
    /**
     * Constructor.
     * @param socket Socket, where the connection with Secondary node is established.
     * @param secNodeManager Communicator with all Secondary nodes.
     * @throws IOException If an I/O exception occurs.
     */
    public SecNodeCommunicator(final SSLSocket socket, final SecNodeManager secNodeManager) throws IOException  {
        primStatusMaintainer = new PrimConnStatusMaintainer();
        primSocketSender = new PrimSocketSender(socket, primStatusMaintainer);
        primSocketReceiver = new PrimSocketReceiver(socket, primSocketSender, primStatusMaintainer, secNodeManager); // this doesn't need to be synchronized, because this is only reference
        primSocketReceiver.start();
    }
    
    /**
     * Getter of Primary socket receiver.
     * @return Primary socket receiver instance.
     */
    public PrimSocketReceiver getPrimSocketReceiver() {
        return primSocketReceiver;
    }

    /**
     * Getter of Primary socket sender.
     * @return Primary socket sender instance.
     */
    public PrimSocketSender getPrimSocketSender() {
        return primSocketSender;
    }

    /**
     * Getter of Primary connection status maintainer.
     * @return Primary connection status maintainer instance.
     */
    public PrimConnStatusMaintainer getPrimConnStatusMaintainer() {
        return primStatusMaintainer;
    }
}
