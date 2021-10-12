/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.secNodeManager;

import com.app.commons.eventLogger.EventLogger;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer.PrimConnStatusMaintainer;
import com.app.primaryNodeApp.model.primaryNode.primSocketCommunicators.SecNodeCommunicator;
import com.app.primaryNodeApp.model.primaryNode.serverPushBean.enums.FirePushEvents.FirePushEventsEnum;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 * Class for communication witch all connected Secondary nodes.
 * @author Filip
 */
@Singleton
@Startup
public class SecNodeManager {
    
    /** STATIC PROPERTIES **/
    
    private static final Logger LOGGER = EventLogger.getInstance().getLogger(SecNodeManager.class.getName());
    public static Long SENDER_STARTING_PERIOD;
    
    /** OBJECT PROPERTIES **/

    private final Map<String, SecNodeCommunicator> socketCommunicators = new ConcurrentHashMap<>();
    private SSLServerSocket listener;

    @Inject
    Event<FirePushEventsEnum> event;
    
    @Resource(name="SOCKET_LISTENING_PORT")
    private Integer socketListeningPort;
    
    @Resource(name="IS_ALIVE_SENDING_PERIOD")
    private Long isAliveSendingPeriod;
    
    /** OBJECT METHODS **/
   
    /**
     * Method, that creates global NODE_EVENT.
     */
    public void updateNodeStatusEvent() {
        event.fire(FirePushEventsEnum.NODE_EVENT);
    }
    
   /**
    * Method, that creates global RUN_EVENT.
    */
    public void updateTaskStatusEvent() {
        event.fire(FirePushEventsEnum.RUN_TASK_EVENT);
    }

    /**
     * Getter of PrimaryNode IP address.
     * @return String, which represents IP address of PrimaryNode.
     * @throws java.net.SocketException If a SocketException occurs.
     * @throws java.net.UnknownHostException If an UnknownHostExceptio occurs.
     */
    public static String getPrimaryNodeIP() throws SocketException, UnknownHostException {
        DatagramSocket socket = new DatagramSocket();
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
        String ip = socket.getLocalAddress().getHostAddress();
        socket.close();
        return ip;
    }

    /**
     * Method, that stops executing any steps of any task on all Secondary nodes.
     */
    public void stopExecutingOnAllNodes() {
        socketCommunicators.keySet().forEach((nodeIP) -> {
            stopExecutingOnNode(nodeIP, 0);
        });
    }

    /**
     * Method, that stops executing any step of any task on specific node.
     * @param nodeIP IP of Secondary node on which the executing to be stopped.
     */
    @Lock(READ)
    public void stopExecutingOnNode(String nodeIP, long start) {
        SecNodeCommunicator secNodeCommunicator = socketCommunicators.get(nodeIP);
        if (secNodeCommunicator != null && secNodeCommunicator.getPrimConnStatusMaintainer().getConnectionStatus() == PrimConnStatusMaintainer.PrimConnectionStatus.RUN_TASK) {
            try {
                secNodeCommunicator.getPrimSocketSender().sendStopMessage();
                synchronized (secNodeCommunicator.getPrimSocketReceiver()) {
                    secNodeCommunicator.getPrimSocketReceiver().wait();
                }
            } catch (IOException | InterruptedException ex) {
                // todo
                Logger.getLogger(SecNodeCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Method, that sets flag about new feasible step on specific secondary node.
     * @param nodeIP IP address of node for which the flag to be set.
     */
    public void setNewFeasibleStep(String nodeIP) {
        SecNodeCommunicator secNodeCommunicator = socketCommunicators.get(nodeIP);
        if (secNodeCommunicator != null) {
            secNodeCommunicator.getPrimConnStatusMaintainer().setIsNextStep(true);
        }
    }

    /**
     * Method, that send Rerun message to specific secondary node.
     * @param stepRunData The running data about specific step of task on specific node on which the Rerun message to be sent.
     */
    public void sendRerunMessage(StepRunData stepRunData) {
        SecNodeCommunicator secNodeCommunicator = socketCommunicators.get(stepRunData.getNode().getNodeIP());
        if (secNodeCommunicator != null && secNodeCommunicator.getPrimConnStatusMaintainer().getConnectionStatus() == PrimConnStatusMaintainer.PrimConnectionStatus.RUN_TASK) {
            try {
                secNodeCommunicator.getPrimSocketSender().sendRerunMessage(stepRunData);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Cannot send {0} message to Secondary node {1}\n\n{2}", new Object[]{GenericSocketMsg.MsgType.RERUN_JOB_STEP, ex.toString(), EventLogger.getErrorStackTrace(ex)});
            }
        }
    }

    /**
     * Method which open the server socket for connecting the Secondary nodes to Primary node.
     */
    @PostConstruct
    private void initialize() {
        SENDER_STARTING_PERIOD = this.isAliveSendingPeriod;
        new Thread(() -> {
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            try {
                listener = (SSLServerSocket) sslServerSocketFactory.createServerSocket(socketListeningPort);
                LOGGER.log(Level.INFO, "\r\nRunning Server: Server adress = {0} , port = {1}",
                        new Object[]{listener.getInetAddress().getHostAddress(), listener.getLocalPort()});
                while (true) {
                    try {
                        SSLSocket sslSocket = (SSLSocket) listener.accept();
                        SecNodeCommunicator secNodeCommunicator = new SecNodeCommunicator(sslSocket, this);
                        this.socketCommunicators.put(sslSocket.getInetAddress().getHostAddress(), secNodeCommunicator);
                    } catch (SSLException e) {
                        LOGGER.log(Level.WARNING, "SecNodeManager error: {0}\n\n{1}", new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
                    }
                }
            } catch (IOException e) {
                if (listener != null && !listener.isClosed()) {
                    LOGGER.log(Level.SEVERE, "SecNodeManager error: {0}\n\n{1}", new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
                }
            } finally {
                try {
                    if (listener != null && !listener.isClosed()) {
                        listener.close();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error in closing socket listener.\n\n{0}\n\n{1}",
                            new Object[]{e.toString(), EventLogger.getErrorStackTrace(e)});
                }
            }
        }).start();
    }

    /**
     * Remove socket from pool of connected Secondary nodes.
     * @param nodeIP IP address of Secondary node to be removed.
     */
    public void removeFromSocketCommunicators(String nodeIP) {
        socketCommunicators.remove(nodeIP);
    }

    /**
     * Merhod, that closes server socket for communicating with Secondary nodes.
     * @throws IOException If an I/O exception occurs.
     */
    public void closeListener() throws IOException {
        if (this.listener != null && !this.listener.isClosed()) {
            this.listener.close();
        }
    }
}
