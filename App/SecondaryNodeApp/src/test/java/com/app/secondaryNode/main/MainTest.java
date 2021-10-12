/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNode.main;

import com.app.secondaryNodeApp.main.Main;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketMessages.GenericSocketMsg.MsgType;
import com.app.commons.socketMessages.ProcErrorMsg;
import com.app.commons.socketMessages.ProcFinishedMsg;
import com.app.commons.socketMessages.RerunMsg;
import com.app.commons.socketMessages.RunStepMsg;
import com.app.commons.socketMessages.StopStepMsg;
import com.app.commons.socketMessages.sendingItems.FinishValues.FinishType;
import com.app.commons.socketMessages.sendingItems.RerunValues;
import com.app.commons.socketMessages.sendingItems.RunValues;
import com.app.commons.socketMessages.sendingItems.RunValues.RunType;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.app.primaryNodeApp.model.primaryNode.secNodeManager.SecNodeManager.getPrimaryNodeIP;
import static com.app.secondaryNode.TestSettings.*;
import com.app.secondaryNodeApp.errMessages.IOErrorMsg;
import com.app.secondaryNodeApp.errMessages.ValidateErrorMsg;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNode.stepExecution.ServerSocketTester;
import static com.app.secondaryNode.stepExecution.ServerSocketTester.PORT;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 *
 * @author filip
 */
public class MainTest {

    private static ServerSocketTester serverSocketTester;
    private static GenericSocketMsg receivedMessage;
    private static Thread mainThread;
    private static int numberOfProcesses;

    public MainTest() {
    }

    @BeforeClass
    public static void initialize() throws UnknownHostException, SocketException, IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        serverSocketTester = new ServerSocketTester();
        serverSocketTester.start();
        receivedMessage = null;
        numberOfProcesses = 1;

        deleteOutputs();

        // start whole Secondary Node app
        mainThread = new Thread(() -> {
            try {
                String[] args = {"-ip", getPrimaryNodeIP(), "-port", Integer.toString(PORT), "-name", TEST_NODE_NAME, "-config", PATH_TO_SECONDARY_NODE_CONFIG_FILE};
                Main.main(args);
            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        mainThread.start();

        while (!serverSocketTester.IsConnected()) {
            synchronized (serverSocketTester) {
                serverSocketTester.wait();
            }
        }
    }

    @AfterClass
    public static void endConnections() throws InterruptedException, IOException {
        // close sockets
        serverSocketTester.closeSocket();

        // close server socket -> the application on Secondary node is then terminated        
        synchronized (mainThread) {
            // wait to terminating application on Secondary node
            mainThread.wait();
        }

        while (serverSocketTester.IsConnected()) {
            synchronized (serverSocketTester) {
                serverSocketTester.wait();
            }
        }
        Thread.sleep(1000);
    }

    /**
     * Waiting for receiving message from SeverSocketTester
     *
     * @throws InterruptedException
     */
    private void receiveMessage() throws InterruptedException {
        receivedMessage = null;
        while (receivedMessage == null) {
            Thread.sleep(500);

            GenericSocketMsg receivedMsg = serverSocketTester.getReceiveMessage();
            if (receivedMsg != null) {
                receivedMessage = receivedMsg;
            }
        }
    }

    // CREATING STEPS
    // create task - it must be executed succesfully
    private Step createSuccessTask() {
        Step step = new Step();
        step.setStepName("Success step test");
        step.setOutputType(IOtypesEnum.FOLDER_OF_FILES);
        step.setInputType(IOtypesEnum.FOLDER_OF_FILES);
        step.setInputRegex("(.*)\\/(.*)\\.(.*)");
        step.setInputPath(PATH_TO_INPUT_SUCCESS_TASK);
        step.setOutputPath(EXEC_OUTPUT_PATH);
        step.setPathToLog(EXEC_PATH_TO_LOG);
        step.setPathToErrLog(EXEC_PATH_TO_ERR_LOG);
        step.setPathToSecondOutputFolder(EXEC_PATH_TO_SECOND_OUTPUT);
        step.setLogSizeLessThan(new Long(10));

        step.setInputArgument("--in");
        step.setOutputArgument("--out");
        step.setCommandPrefix("py " + PATH_TO_PYTHON_SUCCESS_TASK);
        step.setCommandSuffix("--secondOut " + step.getPathToSecondOutputFolder() + " --logFilePath " + step.getPathToLog() + " --sleep " + 1);

        step.setTimeout(new Long(1000 * 1000)); //milliseconds
        step.setProcesses(numberOfProcesses);

        step.setSaveStderr(true);
        step.setSaveErrLog(true);
        step.setCheckLogFileSize(false);
        step.setEqualInToOutFiles(false);
        step.setExistNoEmptyOutputFsNode(true);

        return step;
    }

    // create timeout task
    private Step createTimeoutTask() {
        Step step = createSuccessTask();
        step.setTimeout(new Long(1));
        return step;
    }

    // create IO error task
    private Step createIOErrorTask() {
        Step step = createSuccessTask();
        step.setInputType(IOtypesEnum.FOLDER_OF_FOLDERS);

        return step;
    }

    // create validate error task
    private Step createValidateError() {
        Step step = createSuccessTask();
        step.setCheckLogFileSize(true);
        step.setLogSizeLessThan(new Long(30));

        return step;
    }

    // create stop and run task
    private Step createStopRunStep() {
        Step step = createSuccessTask();
        step.setCommandSuffix("--secondOut " + step.getPathToSecondOutputFolder() + " --logFilePath " + step.getPathToLog() + " --sleep " + 3);
        return step;
    }

    // create rerun stopped task
    private Step createRerunStoppedStep(boolean isWithError) {
        Step step = createSuccessTask();
        step.setCommandSuffix("--secondOut " + step.getPathToSecondOutputFolder() + " --logFilePath " + step.getPathToLog() + " --sleep " + 1 + (isWithError ? " --createError" : ""));
        return step;
    }

    // HELP METHODS
    private static void deleteOutputs() {
        // delete created output folders
        deleteFilesInFolder(EXEC_OUTPUT_PATH.substring(0, EXEC_OUTPUT_PATH.lastIndexOf('/')));
        deleteFilesInFolder(EXEC_PATH_TO_SECOND_OUTPUT.substring(0, EXEC_PATH_TO_SECOND_OUTPUT.lastIndexOf('/')));
        deleteFilesInFolder(EXEC_PATH_TO_LOG.substring(0, EXEC_PATH_TO_LOG.lastIndexOf('/')));
        deleteFilesInFolder(EXEC_PATH_TO_ERR_LOG.substring(0, EXEC_PATH_TO_ERR_LOG.lastIndexOf('/')));
    }

    private static void deleteFilesInFolder(String path) {
        File folder = new File(path);
        for (String f : folder.list()) {
            File file = new File(folder.getPath(), f);
            file.delete();
        }
    }

    private boolean isExistOutputs(Step step, String pathToOutput) {
        File folder = new File(step.getInputPath());
        for (String f : folder.list()) {
            String actOutputPath = FileManager.getPathToOutputFsNode(step.getInputRegex(), step.getInputPath() + '/' + f, pathToOutput);
            if (!FileManager.isExistPath(actOutputPath) || FileManager.isFolder(actOutputPath)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDontExistPath(Step step, String pathToOutput) {
        File folder = new File(step.getInputPath());
        for (String f : folder.list()) {
            String actOutputPath = FileManager.getPathToOutputFsNode(step.getInputRegex(), step.getInputPath() + '/' + f, pathToOutput);
            if (FileManager.isExistPath(actOutputPath)) {
                return false;
            }
        }
        return true;
    }

    private void sendRequestToProcess(Step step) throws InterruptedException, IOException {
        RunValues runValues = new RunValues();
        runValues.setStep(step);
        runValues.setErrorInputs(new ArrayList<>());
        runValues.setRunType(RunType.RUN_WITHOUT_ERRORS);
        // send request to process
        serverSocketTester.sendMessage(new RunStepMsg(runValues));

        // wait to start process ACK       
        receiveMessage();
    }

    private void waitToFinishedMessages() throws InterruptedException {
        // wating for all PROC_FINISHED messages for each input file         

        while (true) {
            receiveMessage();
            if (receivedMessage.getMessageType() != MsgType.PROC_FINISHED) {
                System.out.println(((ProcErrorMsg) receivedMessage).getMessage().getError().getDescription());
            }
            assertEquals(MsgType.PROC_FINISHED, receivedMessage.getMessageType());
            if (((ProcFinishedMsg) receivedMessage).getMessage().getFinishType() == FinishType.FINISH_ALL) {
                break;
            }
        }
    }

    public void isExistOutputs(Step step) {
        // check if all output files exist
        assertEquals(true, this.isExistOutputs(step, step.getOutputPath()));
        assertEquals(true, this.isExistOutputs(step, step.getPathToSecondOutputFolder()));
        assertEquals(true, this.isExistOutputs(step, step.getPathToLog()));
    }

    // TEST CASES
    /**
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void testSuccessTask() throws IOException, InterruptedException {
        Step successStep = this.createSuccessTask();

        // send request to process
        this.sendRequestToProcess(successStep);

        assertEquals(MsgType.RUN_JOB_STEP_ACK, receivedMessage.getMessageType());

        // wait for all finished messages for each file
        this.waitToFinishedMessages();

        // check if all output files exist
        this.isExistOutputs(successStep);

        // delete created output folders
        deleteOutputs();
    }

    public void testTimeoutTask() throws IOException, InterruptedException {
        Step timeoutStep = createTimeoutTask();

        // send request to process
        this.sendRequestToProcess(timeoutStep);

        // wait to ERROR message
        while (true) {
            receiveMessage();
            assertEquals(MsgType.PROC_ERROR, receivedMessage.getMessageType());
            assertEquals(ValidateErrorMsg.getTimeoutExpiredMsg(timeoutStep.getTimeout()), ((ProcErrorMsg) receivedMessage).getMessage().getError().getDescription());
            if (((ProcErrorMsg) receivedMessage).getMessage().getFinishType() == FinishType.FINISH_ALL) {
                break;
            }
        }
    }

    public void testIOError() throws InterruptedException, IOException {
        Step ioErrorStep = createIOErrorTask();
        RunValues runValues = new RunValues();
        runValues.setStep(ioErrorStep);
        runValues.setErrorInputs(new ArrayList<>());
        runValues.setRunType(RunType.RUN_WITHOUT_ERRORS);

        // send request to process
        serverSocketTester.sendMessage(new RunStepMsg(runValues));

        // wait to ERROR message
        receiveMessage();
        assertEquals(MsgType.PROC_ERROR, receivedMessage.getMessageType());
        assertEquals(IOErrorMsg.getDiffInputTypesMsg(ioErrorStep.getInputPath(), IOtypesEnum.FOLDER), ((ProcErrorMsg) receivedMessage).getMessage().getError().getDescription());
    }

    public void testValidateError() throws InterruptedException, IOException {
        Step validateErrorStep = createValidateError();

        // send request to process
        this.sendRequestToProcess(validateErrorStep);

        assertEquals(MsgType.RUN_JOB_STEP_ACK, receivedMessage.getMessageType());

        // wait to ERROR message
        while (true) {
            receiveMessage();
            assertEquals(MsgType.PROC_ERROR, receivedMessage.getMessageType());
            String pathToLog = FileManager.getPathToOutputFsNode(validateErrorStep.getInputRegex(), ((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile(), validateErrorStep.getPathToLog());
            assertEquals(ValidateErrorMsg.getTooBigSizeOfLogFileErr(pathToLog, validateErrorStep.getLogSizeLessThan(), 36), ((ProcErrorMsg) receivedMessage).getMessage().getError().getDescription());
            if (((ProcErrorMsg) receivedMessage).getMessage().getFinishType() == FinishType.FINISH_ALL) {
                break;
            }
        }

        // check if all output files exist
        assertEquals(true, this.isDontExistPath(validateErrorStep, validateErrorStep.getOutputPath()));
        assertEquals(true, this.isDontExistPath(validateErrorStep, validateErrorStep.getPathToSecondOutputFolder()));
        assertEquals(true, this.isExistOutputs(validateErrorStep, validateErrorStep.getPathToLog()));
        // delete created output folders
        deleteOutputs();
    }

    private void testStopRunTask() throws IOException, InterruptedException {
        Step stopRunStep = createStopRunStep();

        // send request to process
        this.sendRequestToProcess(stopRunStep);

        // waiting to process first file
        receiveMessage();
        assertEquals(MsgType.PROC_FINISHED, receivedMessage.getMessageType());

        // send request to stop all tasks
        serverSocketTester.sendMessage(new StopStepMsg());

        // waiting to ack
        receiveMessage();
        assertEquals(MsgType.STOP_JOB_STEP_ACK, receivedMessage.getMessageType());

        // send request to process
        this.sendRequestToProcess(stopRunStep);

        // wait for all finished messages for each file 
        this.waitToFinishedMessages();

        // check if all output files exist
        this.isExistOutputs(stopRunStep);
        // delete created output folders
        deleteOutputs();
    }

    private void testRerunStoppedTask() throws InterruptedException, IOException {
        Step rerunStoppedStep = createRerunStoppedStep(true);

        // send request to process
        this.sendRequestToProcess(rerunStoppedStep);

        // wait to messages about the finish or error
        List<String> errInputs = new ArrayList<>();
        while (true) {
            receiveMessage();

            if (receivedMessage.getMessageType() == MsgType.PROC_ERROR) {
                errInputs.add(((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile());
                
                String outputPath = FileManager.getPathToOutputFsNode(rerunStoppedStep.getInputRegex(), ((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile(), rerunStoppedStep.getOutputPath());
                assertEquals(ValidateErrorMsg.getNonExistOutFileErr(((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile(), outputPath), ((ProcErrorMsg) receivedMessage).getMessage().getError().getDescription());
                if (((ProcErrorMsg) receivedMessage).getMessage().getFinishType() == FinishType.FINISH_ALL) {
                    break;
                }
            } else if (receivedMessage.getMessageType() == MsgType.PROC_FINISHED) {
                if (((ProcFinishedMsg) receivedMessage).getMessage().getFinishType() == FinishType.FINISH_ALL) {
                    break;
                }
            }
        }

        assertEquals(2, errInputs.size());

        // send request to process - error inputs will be reruned, because they don't have corresponding output fs node
        rerunStoppedStep = createRerunStoppedStep(false);
        this.sendRequestToProcess(rerunStoppedStep);

        // wait for all finished messages for each file 
        this.waitToFinishedMessages();

        // check if all output files exist
        this.isExistOutputs(rerunStoppedStep);
        // delete created output folders
        deleteOutputs();

    }

    private void testRerunRunningTask() throws InterruptedException, IOException {
        Step rerunStoppedStep = createRerunStoppedStep(true);
        // send request to process
        this.sendRequestToProcess(rerunStoppedStep);

        // wait to messages about the finish or error
        List<String> errInputs = new ArrayList<>();
        while (true) {
            receiveMessage();

            if (receivedMessage.getMessageType() == MsgType.PROC_ERROR) {
                errInputs.add(((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile());

                String inputPath = ((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile();
                String outputPath = FileManager.getPathToOutputFsNode(rerunStoppedStep.getInputRegex(), inputPath, rerunStoppedStep.getOutputPath());
                assertEquals(ValidateErrorMsg.getNonExistOutFileErr(((ProcErrorMsg) receivedMessage).getMessage().getError().getPathToInputFile(), outputPath), ((ProcErrorMsg) receivedMessage).getMessage().getError().getDescription());
                if (errInputs.size() == 2) {
                    // send rerun message
                    RerunValues rerunValues = new RerunValues();
                    rerunValues.setErrInputs(errInputs);
                    Step s = createSuccessTask();
                    rerunValues.setStep(s);

                    serverSocketTester.sendMessage(new RerunMsg(rerunValues));

                    // wait to rerun ack
                    receiveMessage();
                    assertEquals(MsgType.RERUN_JOB_STEP_ACK, receivedMessage.getMessageType());

                    break;
                }
            }
        }

        // wait for all finished messages for each file 
        this.waitToFinishedMessages();

        // check if all output files exist
        this.isExistOutputs(rerunStoppedStep);
        // delete created output folders
        deleteOutputs();
    }

    /**
     * Test of parseArguments method, of class Main.
     *
     * @throws java.net.SocketException
     * @throws java.net.UnknownHostException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    @Test
    public void testParseArguments() throws SocketException, UnknownHostException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String[] correctArgs_1 = {"-ip", getPrimaryNodeIP(), "-port", Integer.toString(PORT), "-name", TEST_NODE_NAME, "-config", PATH_TO_SECONDARY_NODE_CONFIG_FILE};
        String[] correctArgs_2 = {"-primaryIP", getPrimaryNodeIP(), "-primaryPort", Integer.toString(PORT), "-secNodeName", TEST_NODE_NAME, "-pathToConfig", PATH_TO_SECONDARY_NODE_CONFIG_FILE};
        String[] missingIP = {"-port", Integer.toString(PORT), "-name", TEST_NODE_NAME, "-config", PATH_TO_SECONDARY_NODE_CONFIG_FILE};
        String[] missingPort = {"-ip", getPrimaryNodeIP(), "-name", TEST_NODE_NAME, "-config", PATH_TO_SECONDARY_NODE_CONFIG_FILE};
        String[] missingName = {"-ip", getPrimaryNodeIP(), "-port", Integer.toString(PORT), "-config", PATH_TO_SECONDARY_NODE_CONFIG_FILE};
        String[] missingConfigFile = {"-ip", getPrimaryNodeIP(), "-port", Integer.toString(PORT)};
        String[] unknownParam = {"-ip", getPrimaryNodeIP(), "-port", Integer.toString(PORT), "-name", TEST_NODE_NAME, "-unknown", "value"};

        Map<String, String> correctRes = new HashMap<>();
        correctRes.put("ip", getPrimaryNodeIP());
        correctRes.put("port", Integer.toString(PORT));
        correctRes.put("name", TEST_NODE_NAME);
        correctRes.put("config", PATH_TO_SECONDARY_NODE_CONFIG_FILE);

        Method method = Main.class.getDeclaredMethod("parseArguments", String[].class);
        method.setAccessible(true);
        Map<String, String> result_1 = (Map<String, String>) method.invoke(null, new Object[]{correctArgs_1});
        assertTrue(correctRes.equals(result_1));

        Map<String, String> result_2 = (Map<String, String>) method.invoke(null, new Object[]{correctArgs_2});
        assertTrue(correctRes.equals(result_2));

        assertEquals(null, method.invoke(null, new Object[]{missingIP}));
        assertEquals(null, method.invoke(null, new Object[]{missingPort}));
        assertEquals(null, method.invoke(null, new Object[]{missingName}));
        assertEquals(null, method.invoke(null, new Object[]{missingConfigFile}));
        
        assertEquals(null, method.invoke(null, new Object[]{unknownParam}));
    }

    /**
     * Test of main method, of class Main.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testMain() throws IOException, InterruptedException {

        for (int threads = 1; threads < 3; threads++) {
            testSuccessTask();
            testTimeoutTask();
            testIOError();
            testValidateError();
            testStopRunTask();
            testRerunStoppedTask();
            testRerunRunningTask();
        }
    }

}
