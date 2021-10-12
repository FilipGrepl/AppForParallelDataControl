/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.primaryNode.primSocketCommunicators;

import com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer.PrimConnStatusMaintainer;
import com.app.primaryNodeApp.model.database.dao.StepRunDataDao;
import com.app.commons.socketCommunicators.SocketSender;
import com.app.commons.socketMessages.RerunMsg;
import com.app.commons.socketMessages.RunStepMsg;
import com.app.commons.socketMessages.StopStepMsg;
import com.app.commons.socketMessages.sendingItems.RerunValues;
import com.app.commons.socketMessages.sendingItems.RunValues;
import com.app.commons.socketMessages.sendingItems.RunValues.RunType;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.primaryNode.primConnStatusMaintainer.PrimConnStatusMaintainer.PrimConnectionStatus;
import com.app.primaryNodeApp.model.primaryNode.secNodeManager.SecNodeManager;
import com.app.primaryNodeApp.primLoggers.PrimSocketEventLogger;
import java.io.IOException;
import javax.net.ssl.SSLSocket;

/**
 * Class for sending messages to Secondary node.
 * @author Filip
 */
public class PrimSocketSender extends SocketSender<PrimSocketEventLogger> {   
    
    /** OBJECT PROPERTIES **/
    
    private final PrimConnStatusMaintainer primStatusMaintainer;
    private final StepRunDataDao stepRunDataDao;
    
    /** OBJECT METHOD **/
    
    /**
     * Method for sending feasible steps and isAlive messages to Secondary node.
     */
    private final Runnable stepSender = () -> {
        try {
            sendNextStep();
            sendIsAliveMsg();
        } catch(IOException e) {
            // if some error occurs during sending next step, isAliveMsg is not sent
        }
    };
    
    /**
     * Constructor
     * @param socket Socket, where the connection with Secondary node is established.
     * @param primStatusMaintainer Object, in which the state of connection is maintained.
     * @throws IOException If an I/O exception occurs.
     */
    public PrimSocketSender(final SSLSocket socket, final PrimConnStatusMaintainer primStatusMaintainer) throws IOException {
        super(socket, new PrimSocketEventLogger(PrimSocketSender.class, socket.getInetAddress().getHostAddress(), socket.getPort()), SecNodeManager.SENDER_STARTING_PERIOD);    
        this.primStatusMaintainer = primStatusMaintainer;
        this.stepRunDataDao = new StepRunDataDao();
    }
    
    /**
     * Method, that starts sending steps of task to process on secondary node and starts sending isAlive messages
     * to Secondary node in regular intervals.
     */
    public void startSendingStepsAndIsAliveMessages() {
        this.startSender(stepSender);
    }
    
    /**
     * Method, that sends Rerun message on specific secondary node.
     * @param stepRunData The running data about specific step of task on specific node on which the Rerun message to be sent.
     * @throws IOException If an IOException occurs.
     */
    public void sendRerunMessage(StepRunData stepRunData) throws IOException {
        RerunValues rerunValues = new RerunValues();
        rerunValues.setStep(stepRunData.getStep());
        
        rerunValues.setErrInputs(stepRunDataDao.getErrorInputs(stepRunData.getId()));
        
        sendMessage(new RerunMsg(rerunValues));
    }
    
    /**
     * Method, that sends Stop message on specific secondary node.
     * @throws IOException If an IOException occurs.
     */
    public void sendStopMessage() throws IOException {
        primStatusMaintainer.setConnectionStatus(PrimConnectionStatus.WAIT_TO_STOP_ACK);
        sendMessage(new StopStepMsg());
    }
    
    /**
     * Method, which sends the feasible step to the Secondary node.
     * note: IsNextStep of specific ConnStatusMaintainer must be set after new task has been entered by user.
     * @return True is step has been succesfully sent. False otherwise.
     */
    private void sendNextStep() throws IOException {
        if (primStatusMaintainer.getConnectionStatus() == PrimConnectionStatus.NOT_RUN_TASK && primStatusMaintainer.getIsNextStep()) {
            // get feasible step
            StepRunData nextStepRunData = stepRunDataDao.getStepToRun(primStatusMaintainer.getSecNode().getId());
            
            // set RJNSkey to the feasible step or to null (if there is no feasible step)            
            if (nextStepRunData == null) {
                primStatusMaintainer.setRJNSkey(null);
                primStatusMaintainer.setIsNextStep(false);
            } else {
                RunJobNodeStepKey RJNSkey = nextStepRunData.getId();
                primStatusMaintainer.setRJNSkey(RJNSkey);
                
                RunValues runValues = new RunValues();
                runValues.setStep(nextStepRunData.getStep());
                runValues.setErrorInputs(stepRunDataDao.getErrorInputs(nextStepRunData.getId()));
                
                if (nextStepRunData.isRunButtonPressed() && nextStepRunData.isRerunButtonPressed())
                    runValues.setRunType(RunType.RUN_WITH_ERRORS);
                else if (nextStepRunData.isRerunButtonPressed())
                    runValues.setRunType(RunType.RUN_ONLY_ERRORS);
                else
                    runValues.setRunType(RunType.RUN_WITHOUT_ERRORS);
                
                primStatusMaintainer.setConnectionStatus(PrimConnectionStatus.WAIT_TO_RUN_ACK);
                sendMessage(new RunStepMsg(runValues));                
            }
        }
    }
}
