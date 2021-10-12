
package com.app.secondaryNodeApp.secSocketCommunicators;


import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketCommunicators.SocketReceiver;
import com.app.commons.socketMessages.AcceptConnMsg;
import com.app.commons.socketMessages.DeclineConnMsg;
import com.app.commons.socketMessages.RerunAckMsg;
import com.app.commons.socketMessages.RerunMsg;
import com.app.commons.socketMessages.RunStepAckMsg;
import com.app.commons.socketMessages.RunStepMsg;
import com.app.commons.socketMessages.StopStepAckMsg;
import com.app.commons.socketMessages.StopStepMsg;
import com.app.secondaryNodeApp.errMessages.IOErrorMsg;
import com.app.secondaryNodeApp.stepExecution.StepExecutor;
import com.app.secondaryNodeApp.secLoggers.SecSocketEventLogger;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer.SecConnectionStatus;
import java.io.IOException;
import javax.net.ssl.SSLSocket;
/**
 *  Class for receiving messages from Primary node.
 *  @author Filip
 */
public class SecSocketReceiver extends SocketReceiver<SecSocketEventLogger> {        
    
    /** STATIC PROPERY **/
    
    private static SecSocketReceiver secSocketReceiver = null;
    
    private static SSLSocket socket = null;
    private static SecSocketSender secSocketSender = null;
    
    /** OBJECT PROPERTY **/
    
    private final SecStatusMaintainer secStatusMaintainer;
    
    /** STATIC METHODS **/
    
    /**
     * Initializing static attributes of SecSockerReceiver.
     * @param socket Socket, which is connected to Primary Node.
     * @param secSocketSender Object for sending messages to Primary node.
     */
    public static void initialize(SSLSocket socket, SecSocketSender secSocketSender) {
        SecSocketReceiver.socket = socket;
        SecSocketReceiver.secSocketSender = secSocketSender;
    }
    
    /**
     * Method which return reference to singleton SecSocketReceiver object.
     * @return Reference to singleton SecSocketReceiver object.
     * @throws IOException If an I/O error occurs.
     */
    public static synchronized SecSocketReceiver getInstance() throws IOException {
        if (secSocketReceiver == null) {
            secSocketReceiver = new SecSocketReceiver();
        }
        return secSocketReceiver;
    }
    
    /** OBJECT METHODS **/
    
    /**
     * Private constructor.
     * @throws IOException If an I/O error occurs.
     */
    private SecSocketReceiver() throws IOException {
        super(socket.getInputStream(), new SecSocketEventLogger(SecSocketReceiver.class, socket.getInetAddress().getHostAddress(), socket.getPort()));
        secStatusMaintainer = SecStatusMaintainer.getInstance();
    }
    
    /**
     * Method which extract the type of message and call the corresponding menu for each processing.
     * @param message Received message.
     * @throws IOException If an I/O error occurs.
     * @throws ClassNotFoundException If an ClassNotFoundException error occurs.
     * @throws InterruptedException If an InterruptedException error occurs.
     */
    @Override
    protected void processMessage(GenericSocketMsg message) throws IOException, ClassNotFoundException, InterruptedException {
        switch(message.getMessageType()) {
            case ACCEPT_CONNECTION:
                procAcceptConnMsg((AcceptConnMsg) message);
                break;
            case DECLINE_CONNECTION:
                procDeclineConnMsg((DeclineConnMsg) message);
                break;
            case RUN_JOB_STEP:
                procRunStepMsg((RunStepMsg)message);
                break;
            case STOP_JOB_STEP:
                procStopStepMsg((StopStepMsg)message);
                break;
            case RERUN_JOB_STEP:
                procRerunMsg((RerunMsg)message);
                break;
            case IS_ALIVE:
                break;
            default:
                logger.logUnknownMsgTypeErr(message);
                break;
        }
    }
    
    /**
     * Process accept connection message.
     * @param message Received message.
     */
    private void procAcceptConnMsg(AcceptConnMsg message) {
        if (secStatusMaintainer.getSecConnStatus() == SecConnectionStatus.WAIT_TO_CONN_ACCEPT) {
            logger.logReceivedMessage(message.getMessageType(), "");
            secStatusMaintainer.setSecConnStatus(SecConnectionStatus.WAIT_TO_RUN_TASK);
            secSocketSender.startSendingIsAliveMessages();
        }
        else {
            logger.logUnexpMsgErr(message.getMessageType(), "");
        }
    }
    
    /**
     * Process decline connection message.
     * @param message Received message.
     */
    private void procDeclineConnMsg(DeclineConnMsg message) {
        if (secStatusMaintainer.getSecConnStatus() == SecConnectionStatus.WAIT_TO_CONN_ACCEPT) {
            logger.logReceivedMessage(message.getMessageType(), message.getMessage());
            logger.logDeclineConnection(message.getMessage());
        }
        else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage());
        }
    }
    
    /**
     * Process run step message.
     * @param message Received message.
     * @throws IOException If an I/O error occurs.
     */
    private void procRunStepMsg(RunStepMsg message) throws IOException {
        if (secStatusMaintainer.getSecConnStatus() == SecConnectionStatus.WAIT_TO_RUN_TASK) {
            logger.logReceivedMessage(message.getMessageType(), message.getMessage().toString());
            secStatusMaintainer.setStopExecuting(false); //!!!!! 
            long numberOfFiles;
            try { // process running this step - create new process|es
                numberOfFiles = StepExecutor.execJobStep(message.getMessage());
            } catch(IOException e) {
                secSocketSender.sendIOError(IOErrorMsg.getIOErrorStartProgramMsg(message.getMessage().getStep().getInputPath(), e));
                return;
            }
            if (numberOfFiles > 0) { // error message has been sent if numberOfFiles == 0
                secStatusMaintainer.setSecConnStatus(SecConnectionStatus.RUN_TASK);
                secSocketSender.sendMessage(new RunStepAckMsg(numberOfFiles));
            }
        }
        else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage().toString());
        }
    }
    
    /**
     * Process stop step message.
     * @param message Received message.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If an InterruptedException occurs.
     */
    private void procStopStepMsg(StopStepMsg message) throws IOException, InterruptedException {
        if (secStatusMaintainer.getSecConnStatus() == SecConnectionStatus.RUN_TASK) {
            logger.logReceivedMessage(message.getMessageType(), "");
            stopAllStepExecutors();
            secSocketSender.sendMessage(new StopStepAckMsg());
            secStatusMaintainer.setSecConnStatus(SecStatusMaintainer.SecConnectionStatus.WAIT_TO_RUN_TASK);
        }
        else {
            logger.logUnexpMsgErr(message.getMessageType(), "");
        }
    }
    
    /**
     * Process rerun error inputs (files or folders) message.
     * @param message Received message.
     * @throws IOException If an I/O error occurs.
     */
    // TODO
    private void procRerunMsg(RerunMsg message) throws IOException {
        if (secStatusMaintainer.getSecConnStatus() == SecConnectionStatus.RUN_TASK) {
            logger.logReceivedMessage(message.getMessageType(), message.getMessage().toString());
            message.getMessage().getErrInputs().forEach(errInput -> {
                secStatusMaintainer.addInput(errInput);
            });
            
            secSocketSender.sendMessage(new RerunAckMsg());
        }
        else {
            logger.logUnexpMsgErr(message.getMessageType(), message.getMessage().toString());
        }
    }
    
    /**
     * Stop step in all threads.
     * @throws InterruptedException If an InterruptedException occurs.
     */
    private void stopAllStepExecutors() throws InterruptedException {
        secStatusMaintainer.setStopExecuting(true);
        StepExecutor.waitToFinishAllStepExecutors();
    }
    
    /**
     * Reading and processing received messages.
     */
    @Override
    public void run() {
        while(true) {
            Object o = null;
            try {
                    o = receiveMessage();
                    GenericSocketMsg message = (GenericSocketMsg) o;
                    processMessage(message);
            } catch (ClassNotFoundException cex) {
                logger.logUnknownObjectErr(o);
            } catch ( InterruptedException ie) {
                logger.logErrorJoinExecThread(ie);
                secSocketSender.closeConnection();
                return;
            } catch (IOException e) {
                try {
                    stopAllStepExecutors();
                } catch ( InterruptedException ie) {
                    logger.logErrorJoinExecThread(ie);
                }
                logger.logCloseConnErr(e);
                secSocketSender.closeConnection();
                return;
            }
        }
    }
}

