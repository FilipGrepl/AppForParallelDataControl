/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketCommunicators;

import com.app.commons.eventLogger.SocketEventLogger;
import com.app.commons.socketMessages.GenericSocketMsg;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Common abstract class for receiving messages from Primary or Secondary node.
 * @author Filip
 * @param <T> Logger for logging events of socket communication.
 */
public abstract class SocketReceiver<T extends SocketEventLogger> extends Thread {
    
    protected final T logger;
    private final ObjectInputStream objectReceiver;
    
    /**
     * Constructor.
     * @param socketInputStream Socket input stream for communication with Primary or Secondary node.
     * @param logger Logger for logging events of socket communication.
     * @throws IOException  If I/O error occurs.
     */
    protected SocketReceiver(InputStream socketInputStream, T logger) throws IOException {
        this.objectReceiver = new ObjectInputStream(socketInputStream);
        this.logger = logger;
    }
    
    /**
     * Method, which returns the received message.
     * @return Object which contains the received message.
     * @throws IOException If an I/O exception occurs.
     * @throws ClassNotFoundException If a ClassNotFoundException occurs.
     */
    protected Object receiveMessage() throws IOException, ClassNotFoundException {
        return this.objectReceiver.readObject();
    }

    /**
     * Method, which process the received message.
     * @param message Received message.
     * @throws IOException If an I/O exception occurs.
     * @throws ClassNotFoundException If a ClassNotFoundException occurs.
     * @throws InterruptedException If a InterruptedException occurs.
     */
    abstract protected void processMessage(GenericSocketMsg message) throws IOException, ClassNotFoundException, InterruptedException;
}

