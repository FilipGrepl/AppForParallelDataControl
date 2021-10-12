/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.stepExecution;

import com.app.commons.socketMessages.sendingItems.FinishValues.FinishType;
import com.app.commons.socketMessages.sendingItems.RunValues;
import com.app.commons.socketMessages.sendingItems.RunValues.RunType;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.secondaryNodeApp.errMessages.IOErrorMsg;
import com.app.primaryNodeApp.model.database.entity.Error;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNodeApp.secLoggers.SecExecLogger;
import com.app.secondaryNodeApp.secSocketCommunicators.SecSocketSender;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer.SecConnectionStatus;
import com.app.secondaryNodeApp.stepExecution.RunStatusChecker.EndStatus;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 *
 * @author Filip
 */
public class StepExecutor extends Thread {

    /**
     * STATIC PROPERTY *
     */
    private static final String PARAM_HOSTNAME = "$HOSTNAME";

    private static final long CHECKER_REPEAT_DELAY = 500; //miliseconds
    private static final long CHECKER_INIT_DELAY = 0;

    private static final List<StepExecutor> RUNNING_EXEC_THREADS = Collections.synchronizedList(new ArrayList<>());    // all running threads, which execute the step

    /**
     * OBJECT PROPERTY *
     */
    private final Step step;

    private final SecStatusMaintainer secStatusMaintainer;
    private final SecSocketSender secSocketSender;

    private String actInputFsNodePath;
    private String actOutputFsNodePath;
    private String actCommandPrefix;
    private String actCommandSuffix;
    
    private String actCommand;

    private final SecExecLogger secExecLogger;

    /**
     * STATIC METHOD *
     */
    /**
     * The method which execute the Step on all inputs in entered number of
     * processes.
     *
     * @param runValues The received information about running step of task.
     * @return Number of loaded inputs for executing.
     * @throws IOException If an I/O error occurs.
     */
    public static int execJobStep(final RunValues runValues) throws IOException {

        InputLoader inputLoader = InputLoader.getInstance();
        
        int numberOfInputs = inputLoader.checkAndLoadInputs(runValues);
        boolean isCreatedOutFolders = inputLoader.CreateAllOutputFolders(runValues.getStep());    
        
        // start executing step
        if (numberOfInputs != 0 && isCreatedOutFolders) {
            // task is started on entered number of threads
            for (int c = 0; c < runValues.getStep().getProcesses(); c++) {
                System.out.println("Starting thread: " + c);
                new StepExecutor(runValues.getStep()).start(); //  access to step doesn't need to be synchronized - step is only read
            }
        }
        return isCreatedOutFolders ? numberOfInputs : 0;
    }
    
    /**
     * Waiting for all threads to finish
     *
     * @throws InterruptedException If an InterruptedException occurs.
     */
    public static void waitToFinishAllStepExecutors() throws InterruptedException {
        StepExecutor runningStepExecutor;

        do {
            synchronized (RUNNING_EXEC_THREADS) {
                if (!RUNNING_EXEC_THREADS.isEmpty()) {
                    runningStepExecutor = RUNNING_EXEC_THREADS.get(0);
                } else {
                    runningStepExecutor = null;
                }
            }
            if (runningStepExecutor != null) {
                runningStepExecutor.join();       // waiting to finishing all running executing threads
            }
        } while (runningStepExecutor != null);
    }

    /**
     * OBJECT METHOD *
     */
    /**
     * Constructor.
     *
     * @param step The step to be executed.
     */
    public StepExecutor(final Step step) throws IOException {
        this.step = step;
        secStatusMaintainer = SecStatusMaintainer.getInstance();
        secSocketSender = SecSocketSender.getInstance();
        secExecLogger = new SecExecLogger(StepExecutor.class);
    }

    /**
     * Create and execute the step with specific input file/folder.
     *
     * @param outputFsNode Path to output file/folder
     * @throws IOException
     */
    private Process startExecutingStep() throws IOException {
        this.actCommand = actCommandPrefix + ' '
                + step.getInputArgument() + ' '
                + "\"" + actInputFsNodePath + "\"" + ' '
                + step.getOutputArgument() + ' '
                + "\"" + actOutputFsNodePath + "\"" + ' '
                + actCommandSuffix;
        if (System.getProperty("os.name").contains("Windows")) {
            return Runtime.getRuntime().exec(actCommand);
        } else {
            return Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", actCommand});
        }
    }

    /**
     * Method, which removes the thread from all running thread an if it is last
     * running thread, set the node status as free.
     */
    private void stopExecutingStep() {
        synchronized (RUNNING_EXEC_THREADS) {
            RUNNING_EXEC_THREADS.remove(this);
            if (RUNNING_EXEC_THREADS.isEmpty()) {
                secStatusMaintainer.setSecConnStatus(SecConnectionStatus.WAIT_TO_RUN_TASK);
            }
        }
    }

    /**
     * Method which create error messages.
     *
     * @param runStatusChecker The object, which checked the status about
     * running step and contains detailed informations about error.
     * @return @return Error object with detailed informations about error, wich
     * is occurs.
     */
    private Error createError(RunStatusChecker runStatusChecker) {
        Error error = new Error();
        error.setPathToInputFile(actInputFsNodePath);
        error.setDescription(runStatusChecker.getErrDescription());
        error.setCommand(this.actCommand);
        error.setExitCode(runStatusChecker.getExitCode());

        if (step.isSaveStderr()) {
            error.setStderr(runStatusChecker.getStderrContent());
        }
        if (step.isSaveErrLog()) {
            // the path to log must be created from inputFsNodePath on which the inputRegex must be applicated                
            String pathToErrLog = FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getPathToErrLog());
            try {
                String contentOfLogFile = FileManager.getFileContent(pathToErrLog);
                error.setLogFile(contentOfLogFile);
            } catch (FileNotFoundException e) {
                error.setLogFile(IOErrorMsg.getNonExistPathToFileMsg(pathToErrLog));
            } catch (IOException ex) {
                error.setLogFile(IOErrorMsg.getCannotReadFileMsg(pathToErrLog, ex));
            }
        }
        return error;
    }

    /**
     * Method, which sends the information about ended execution for each input
     * to the Primary node.
     *
     * @param finishType Type of finish/error message - if there is no other
     * input and no other thread on which the step is running or not.
     * @param runStatusChecker The object which monitored the execution and
     * contains the information about ended execution.
     * @throws IOException If an I/O error occurs.
     */
    private void sendFinishInfoToPrimNode(FinishType finishType, RunStatusChecker runStatusChecker) throws IOException {
        // send finish message
        switch (runStatusChecker.getEndStatus()) {
            case STOPPED:
                break;
            case TIMEOUT_EXPIRED:
            case ERROR:
                secSocketSender.sendErrProcMessage(
                        finishType,
                        runStatusChecker.getExecTime(),
                        this.actInputFsNodePath,
                        FileManager.getFsNodeLength(this.actInputFsNodePath),
                        createError(runStatusChecker));
                break;
            case OK:
                secSocketSender.sendFinishedMsg(finishType, 
                        runStatusChecker.getExecTime(), 
                        this.actInputFsNodePath,
                        FileManager.getFsNodeLength(this.actInputFsNodePath));
                break;
        }
    }

    private void sendExceptionErrorToPrimNode(RunStatusChecker runStatusChecker, String errDescription) {
        try {
            // check if next input is available and if some other thread is running (must be higher than 1, because this actual thread is in list)
            FinishType finishType = (RUNNING_EXEC_THREADS.isEmpty() ? FinishType.FINISH_ALL : FinishType.FINISH_FS_NODE);
            this.deleteOutputs();
            Error error = new Error();
            error.setDescription(errDescription);
            secSocketSender.sendErrProcMessage(
                    finishType,
                    runStatusChecker == null ? 0 : runStatusChecker.getExecTime(),
                    this.actInputFsNodePath,
                    FileManager.getFsNodeLength(this.actInputFsNodePath),
                    error);
        } catch (IOException ex) {
            secExecLogger.logErrCatchExecStep(step, ex);
        }
    }

    /**
     * Method, that deletes all outputs created by running program for specific
     * input.
     *
     * @return True if outputs was successfully deleted. False otherwise.
     */
    private boolean deleteOutputs() {
        if (!FileManager.deleteFsNode(actOutputFsNodePath)) {
            return false;
        }
        if (step.getPathToSecondOutputFolder() != null) {
            String actSecOutputFsNodePath = FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getPathToSecondOutputFolder());
            if (!FileManager.deleteFsNode(actSecOutputFsNodePath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method, that sets hostname of node to command.
     * 
     * @throws UnknownHostException If an UnknownHostE occurs.
     */
    private void replaceHostname() throws UnknownHostException {
        actInputFsNodePath = actInputFsNodePath.replace(PARAM_HOSTNAME, InetAddress.getLocalHost().getHostName());
        actOutputFsNodePath = actOutputFsNodePath.replace(PARAM_HOSTNAME, InetAddress.getLocalHost().getHostName());
        actCommandPrefix = actCommandPrefix.replace(PARAM_HOSTNAME, InetAddress.getLocalHost().getHostName());
        actCommandSuffix = actCommandSuffix.replace(PARAM_HOSTNAME, InetAddress.getLocalHost().getHostName());
    }
    
    /**
     * Running received step in one process, monitoring the execution and
     * sending the usage of system resources to the Primary Node.
     */
    @Override
    public void run() {
        Process stepRun = null;
        FinishType finishType;
        RunStatusChecker runStatusChecker = null;
        ScheduledExecutorService execService;
        ScheduledFuture<?> scheduledFuture;

        RUNNING_EXEC_THREADS.add(this);

        try {

            // load fist input
            actInputFsNodePath = secStatusMaintainer.getInput();

            if (actInputFsNodePath == null) {
                synchronized (secStatusMaintainer) {
                    this.stopExecutingStep();
                }
                return;
            }

            while (actInputFsNodePath != null && !secStatusMaintainer.isStopExecuting()) {

                // get path to output file
                actOutputFsNodePath = FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getOutputPath());
                actCommandPrefix = FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getCommandPrefix());
                actCommandSuffix = FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getCommandSuffix());
                
                replaceHostname();

                // start executing step
                stepRun = this.startExecutingStep();

                // start of sending usage messages
                UsageValuesSender.getInstance().addProcessToMonitore(stepRun);

                // start of checking run status - timeout or requirement to stop the executing of the step
                runStatusChecker = new RunStatusChecker(stepRun, step, actInputFsNodePath);
                execService = Executors.newScheduledThreadPool(1);
                scheduledFuture = execService.scheduleAtFixedRate(runStatusChecker, CHECKER_INIT_DELAY, CHECKER_REPEAT_DELAY, TimeUnit.MILLISECONDS);

                // wait while RunStatusChecker doesn't call notify method (while the step with specific input hasn't been finished)
                synchronized (runStatusChecker) {
                    runStatusChecker.wait();
                }

                // end of checking run status
                scheduledFuture.cancel(true);
                execService.shutdown();

                // this block must be synchronized, because no other threads can read runningExecThreads and read next input
                // while other thread is setting fnishType, sending finish message and NOT finished stopExecuting and getInput method
                synchronized (secStatusMaintainer) {
                    // check if next input is available and if some other thread is running (must be higher than 1, because this actual thread is in list)
                    finishType = (secStatusMaintainer.isEmptyInputs() && RUNNING_EXEC_THREADS.size() == 1 ? FinishType.FINISH_ALL : FinishType.FINISH_FS_NODE);

                    // delete outputs when error occurs to reruning
                    if (runStatusChecker.getEndStatus() == EndStatus.ERROR
                            || runStatusChecker.getEndStatus() == EndStatus.TIMEOUT_EXPIRED
                            || runStatusChecker.getEndStatus() == EndStatus.STOPPED) {
                        this.deleteOutputs(); // TODO check return value
                    }

                    if (secStatusMaintainer.isEmptyInputs() || secStatusMaintainer.isStopExecuting()) {
                        this.stopExecutingStep();
                    }

                    this.sendFinishInfoToPrimNode(finishType, runStatusChecker);

                    actInputFsNodePath = secStatusMaintainer.getInput();
                }
            }
        } catch (IOException e) {
            secExecLogger.logErrExecStep(step, actInputFsNodePath, e);
            synchronized (secStatusMaintainer) {
                this.stopExecutingStep();
                this.sendExceptionErrorToPrimNode(runStatusChecker, IOErrorMsg.getCannotRunProgramMsg(step.getCommandPrefix(), e));
            }
        } catch (InterruptedException e) {
            secExecLogger.logErrWaitToUsageThread(e);
            synchronized (secStatusMaintainer) {
                this.stopExecutingStep();
                this.sendExceptionErrorToPrimNode(runStatusChecker, IOErrorMsg.getInterruptProgramMsg(step.getCommandPrefix(), e));
            }
        } finally {
            if (stepRun != null && stepRun.isAlive()) {
                stepRun.destroy();
            }
        }
    }
}
