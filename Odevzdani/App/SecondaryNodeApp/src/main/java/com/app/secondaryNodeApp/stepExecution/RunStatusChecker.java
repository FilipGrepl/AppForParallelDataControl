/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.stepExecution;

import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.secondaryNodeApp.errMessages.IOErrorMsg;
import com.app.secondaryNodeApp.errMessages.ValidateErrorMsg;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNodeApp.outputValidator.OutputValidator;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Filip
 */
public class RunStatusChecker implements Runnable {

    /**
     * OBJECT PROPERTY *
     */
    private EndStatus endStatus;
    private final Process stepRun;
    private final long maxEndedTime;
    private final long startedTime;
    private final SecStatusMaintainer secStatusMaintainer;
    private long execTime;
    private String errDescription;
    private String stderrContent;
    private final Step step;
    private final String actInputFsNodePath;
    private Thread readingThread;

    public enum EndStatus {
        STOPPED, TIMEOUT_EXPIRED, ERROR, OK
    };

    /**
     * OBJECT METHOD *
     */
    /**
     * Constructor
     *
     * @param stepRun
     * @param step
     * @param actInputFsNodePath
     */
    public RunStatusChecker(Process stepRun, Step step, String actInputFsNodePath) {
        this.stepRun = stepRun;
        this.step = step;
        this.actInputFsNodePath = actInputFsNodePath;
        this.startedTime = System.currentTimeMillis();
        this.maxEndedTime = startedTime + step.getTimeout(); // started time + timeout
        this.endStatus = null;
        this.execTime = 0;
        this.errDescription = null;
        this.stderrContent = "";
        this.secStatusMaintainer = SecStatusMaintainer.getInstance();
        this.createStderrReadingThread();
    }

    /**
     * Method which creates thread, that reads immediately every bytes, which is
     * wrote to stderr.
     */
    public final void createStderrReadingThread() {
        this.readingThread = new Thread(() -> {
            try {
                this.stderrContent = FileManager.getStreamContent(this.stepRun.getErrorStream());
            } catch (IOException ex) {
                synchronized (step) {
                    endStatus = EndStatus.ERROR;
                    errDescription = (errDescription == null ? "" : errDescription + "\n") + IOErrorMsg.getCannotReadStderrMsg(ex);
                }
            }
        });
        this.readingThread.start();
    }

    /**
     * Method which destroy running process and waiting for the process to complete
     */
    private void destroyStepRun() {
        try {
            if (!System.getProperty("os.name").contains("Windows")) {
                Runtime.getRuntime().exec("kill " + UsageValuesSender.getChildPID(stepRun)).waitFor();
            }
            stepRun.destroy();
            stepRun.waitFor();
        } catch (InterruptedException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | IOException ex) {
            Logger.getLogger(RunStatusChecker.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }

    /**
     * Method which is called every second to check the executing of the step on
     * specified input.
     */
    @Override
    public void run() {
        if (System.currentTimeMillis() > maxEndedTime) {    // check timeout
            synchronized (step) {
                endStatus = EndStatus.TIMEOUT_EXPIRED;
            }
            this.destroyStepRun();
        }
        if (secStatusMaintainer.isStopExecuting()) {        // stop executing request?            
            synchronized (step) {
                endStatus = EndStatus.STOPPED;
            }
            this.destroyStepRun();
        }

        // it is also executed if timeout is up or the step executing is stopped
        if (!stepRun.isAlive()) {
            execTime = System.currentTimeMillis() - startedTime;
            
            try {
                this.readingThread.join();              // waiting for the stderr reading thread to end
            } catch (InterruptedException ex) {
                Logger.getLogger(RunStatusChecker.class.getName()).log(Level.SEVERE, null, ex);
            }               
            
            if (endStatus == null) {    // not STOPPED or TIMEOUT_EXPIRED status
                String validateMsg;
                if (stepRun.exitValue() != 0) {
                    endStatus = EndStatus.ERROR;        // setting doesn't have to be synchronized because reading thread is ended (join waiting)
                } else if ((validateMsg = OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath)) != null) {
                    endStatus = EndStatus.ERROR;        // setting doesn't have to be synchronized because reading thread is ended (join waiting)
                    errDescription = (errDescription == null ? "" : errDescription + "\n\n") + validateMsg; 
                } else {
                    endStatus = EndStatus.OK;           // setting doesn't have to be synchronized because reading thread is ended (join waiting)
                }
            } else if (endStatus == EndStatus.TIMEOUT_EXPIRED) {
                errDescription = (errDescription == null ? "" : errDescription + "\n\n") + ValidateErrorMsg.getTimeoutExpiredMsg(step.getTimeout());
            }
            
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    /**
     * The end status of executed step on specified input.
     *
     * @return The end status of executed step on specified input.
     */
    public EndStatus getEndStatus() {
        return endStatus;
    }

    /**
     * Getter of executed time of step on specified input.
     *
     * @return The executed time of step on specified input.
     */
    public long getExecTime() {
        return execTime;
    }

    /**
     * Getter of error description.
     *
     * @return Error description.
     */
    public String getErrDescription() {
        return errDescription;
    }

    /**
     * Getter of stderr content.
     *
     * @return Content of stderr.
     */
    public String getStderrContent() {
        return stderrContent;
    }
    
    public int getExitCode() {
        return stepRun.exitValue();
    }
}
