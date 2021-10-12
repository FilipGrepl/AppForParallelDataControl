/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.commons.socketCommunicators;

import com.app.commons.eventLogger.SocketEventLogger;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketMessages.GenericSocketMsg.MsgType;
import com.app.commons.socketMessages.IsAliveMsg;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocket;

/**
 * Class for sending messages to Primary or Secondary node.
 * @author Filip
 * @param <T> Logger for logging events of socket communication.
 */
public abstract class SocketSender<T extends SocketEventLogger> {
    
    /** OBJECT PROPERTIES **/

    protected final T LOGGER;

    private final SSLSocket socket;
    private final ObjectOutputStream objectSender;
    
    private final Long startingPeriod;

    private ScheduledExecutorService execService;
    private ScheduledFuture<?> scheduledFuture;

    /** OBJECT METHODS **/
    
    /**
     * Constructor.
     * @param socket Socket for communication with Primary or Secondary node.
     * @param logger Logger for logging events of socket communication.
     * @param startingPeriod Period of starting sender in seconds.
     * @throws IOException If I/O error occurs.
     */
    protected SocketSender(SSLSocket socket, T logger, Long startingPeriod) throws IOException {
        this.socket = socket;
        this.objectSender = new ObjectOutputStream(socket.getOutputStream());
        this.LOGGER = logger;
        this.startingPeriod = startingPeriod;
        this.execService = null;
        this.scheduledFuture = null;
    }
    
    /**
     * Method, that sets name of node to the logger.
     * @param nodeName New value of name of node.
     */
    public void setLogNodeName(String nodeName) {
        LOGGER.setNodeName(nodeName);
    }

    /**
     * Method, which starts periodic sending the messages.*
     * @param sender The method, which specify, how messages is periodic sent to Primary or Secondary node.
     */
    protected void startSender(Runnable sender) {
        if (execService == null && scheduledFuture == null) {
            execService = Executors.newScheduledThreadPool(1);
            scheduledFuture = execService.scheduleAtFixedRate(sender, 0, startingPeriod, TimeUnit.SECONDS);
        }
    }

    /**
     * Stop periodic sending messages to Primary or Secondary node.
     */
    private void stopSender() {
        if (execService != null && scheduledFuture != null) {
            scheduledFuture.cancel(true);
            execService.shutdown();
        }
    }

    /**
     * Method, which sends the message to Primary or Secondary node.
     * @param message Message, which could be sent.
     * @throws IOException If an I/O exception occurs.
     */
    public synchronized void sendMessage(GenericSocketMsg message) throws IOException {
        try {
            this.objectSender.writeObject(message);
            this.objectSender.flush();
            if (message.getMessageType() != MsgType.IS_ALIVE && message.getMessageType() != MsgType.USAGE_OF_RESOURCES) {
                LOGGER.logSentMessage(message);
            }
        } catch (IOException e) {
            LOGGER.logSendError(message.getMessageType(), e);
            closeConnection();
            throw e;
        }
    }

    /**
     * Method, which sends the isAlive message to Primary or Secondary node.
     */
    public void sendIsAliveMsg() {
        try {
            sendMessage(new IsAliveMsg());
        }
        catch (IOException e) {
            closeConnection();
        }
    }
    
    /**
     * Method, that returns IP address of connected remote side.
     * @return The client IP address.
     */
    public String getRemoteIPAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    /**
     * Method, which ends the connection with Primary or Secondary node.
     */
    public void closeConnection() {
        stopSender();
        if (!this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                LOGGER.logCloseSocketErr(e);
            }
        }
    }
}
