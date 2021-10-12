
package com.app.secondaryNodeApp.secSocketCommunicators;

import com.app.commons.socketCommunicators.SocketSender;
import com.app.commons.socketMessages.ProcErrorMsg;
import com.app.commons.socketMessages.ProcFinishedMsg;
import com.app.commons.socketMessages.NodeNameMsg;
import com.app.commons.socketMessages.sendingItems.ErrorValues;
import com.app.commons.socketMessages.sendingItems.FinishValues.FinishType;
import com.app.commons.socketMessages.sendingItems.FinishValues;
import com.app.secondaryNodeApp.secLoggers.SecSocketEventLogger;
import com.app.primaryNodeApp.model.database.entity.Error;
import com.app.secondaryNodeApp.configuration.Configuration;
import java.io.IOException;
import javax.net.ssl.SSLSocket;

/**
 * Class for sending messages to Primary Node.
 * @author Filip
 */
public class SecSocketSender extends SocketSender<SecSocketEventLogger> {
    
    /** STATIC PROPERTY **/
    private static SecSocketSender secSocketSender = null;
    private static SSLSocket socket = null;
    private static String nodeName = null;
    
    /** OBJECT METHODS **/
    
    private final Runnable isAliveSender = () -> {
        sendIsAliveMsg();
    };   
    
    /** STATIC METHODS **/
    
    /**
     * Initializing static attributes of SecSockerReceiver.
     * @param socket Socket, which is connected to Primary Node.
     * @param nodeName Name of the Secondary node.
     */
    public static void initialize(SSLSocket socket, String nodeName) {
        SecSocketSender.socket = socket;
        SecSocketSender.nodeName = nodeName;
    }
    
    /**
     * Method, which destroy the singleton instance - sets it to the null (for testing).
     */
     public static void destroySingleton() {
        secSocketSender = null;
    }
    
    /**
     *  Method which return reference to singleton SecSocketSender object.
     * @return Reference to singleton SecSocketSender object.
     * @throws java.io.IOException If an I/O exception occurs.
     */
    public static synchronized SecSocketSender getInstance() throws IOException {
        if (secSocketSender == null) {
            secSocketSender = new SecSocketSender();
        }
        return secSocketSender;
    }
    
    
    /** OBJECT METHODS **/
    
    /**
     * Private constructor.
     * @param socket Socket, which is connected to Primary node.
     * @param nodeName Name of the Secondary node.
     * @throws IOException If an I/O exception occurs.
     */
    private SecSocketSender() throws IOException {
        super(socket, new SecSocketEventLogger(SecSocketSender.class, socket.getInetAddress().getHostAddress(), socket.getPort()), Configuration.getInstance().getIsAliveSendingPeriod());
        // send entered nodeName as message to Primary node
        sendMessage(new NodeNameMsg(nodeName));
    }
    
    /**
     * Method to start sending of isAliveMessages
     */
    public void startSendingIsAliveMessages() {
        // start sending is alive messages to Primary node
        startSender(isAliveSender);
    }
    
    /**
     * Method, which send an I/O error message to Primary node during loading inputs or creating output folder.
     * @param description The description of occurred I/O error.
     * @throws IOException If an I/O exception occurs.
     */
    public void sendIOError(String description) throws IOException {
        Error error = new Error();
        ErrorValues errorValues = new ErrorValues(error, FinishType.FINISH_ALL, 0, null, 0);
        error.setDescription(description);
        secSocketSender.sendMessage(new ProcErrorMsg(errorValues));
        
    }
        
    /**
     * Method, which send a processing error to Primary node.
     * @param finishType Type of error message - if there is no other input and no other thread on which the step is running or not.
     * @param execTime The running time of step execution.
     * @param inputPath Path to input.
     * @param inputLength Length of input.
     * @param error Object which contains detail about error - content of log file, content of stderr etc.
     * @throws IOException If an I/O exception occurs.
     */
    public void sendErrProcMessage(FinishType finishType, long execTime, String inputPath, long inputLength, Error error) throws IOException {        
        ErrorValues errorValues = new ErrorValues(error, finishType, execTime, inputPath, inputLength);
        secSocketSender.sendMessage(new ProcErrorMsg(errorValues));
    }
    
    /**
     * Method, whcih send a finish message to Primary node.
     * @param finishType Type of error message - if there is no other input and no other thread on which the step is running or not.
     * @param execTime The running time of step execution.
     * @param inputPath Path to input.
     * @param inputLength Length of input.
     * @throws IOException If an I/O exception occurs.
     */
    public void sendFinishedMsg(FinishType finishType, long execTime, String inputPath, long inputLength) throws IOException {
        secSocketSender.sendMessage(new ProcFinishedMsg(new FinishValues(finishType, execTime, inputPath, inputLength)));
    }
}
