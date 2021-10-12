/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNode.stepExecution;

import com.app.secondaryNodeApp.stepExecution.InputLoader;
import com.app.commons.socketMessages.GenericSocketMsg;
import com.app.commons.socketMessages.GenericSocketMsg.MsgType;
import com.app.commons.socketMessages.ProcErrorMsg;
import com.app.commons.socketMessages.sendingItems.RunValues;
import com.app.commons.socketMessages.sendingItems.RunValues.RunType;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import com.app.primaryNodeApp.model.primaryNode.secNodeManager.SecNodeManager;
import static com.app.secondaryNode.TestSettings.*;
import static com.app.secondaryNode.TestSettings.TEST_NODE_NAME;
import static com.app.secondaryNode.stepExecution.ServerSocketTester.PORT;
import com.app.secondaryNodeApp.configuration.Configuration;
import com.app.secondaryNodeApp.errMessages.IOErrorMsg;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer;
import com.app.secondaryNodeApp.secSocketCommunicators.SecSocketSender;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author filip
 */
public class InputLoaderTest {

    private static Step step;
    private static SecStatusMaintainer secStatusMaintainer;
    private static SSLSocket socket;
    private static ServerSocketTester serverSocketTester;
    private static ProcErrorMsg message;

    public InputLoaderTest() {

    }

    @BeforeClass
    public static void initialize() throws UnknownHostException, SocketException, IOException, InterruptedException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        serverSocketTester = new ServerSocketTester();
        serverSocketTester.start();
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) factory.createSocket(SecNodeManager.getPrimaryNodeIP(), PORT);
        socket.startHandshake();
        Configuration.setPathToConfigFile(PATH_TO_SECONDARY_NODE_CONFIG_FILE);
        SecSocketSender.initialize(socket, TEST_NODE_NAME);
        SecSocketSender.destroySingleton();
        SecSocketSender.getInstance();
        secStatusMaintainer = SecStatusMaintainer.getInstance();

        while (!serverSocketTester.IsConnected()) {
            synchronized (serverSocketTester) {
                serverSocketTester.wait();
            }
        }

        commonInit();
    }

    @AfterClass
    public static void endConnections() throws IOException, InterruptedException {
        // close sockets
        serverSocketTester.closeSocket();
        socket.close();

        while (serverSocketTester.IsConnected()) {
            synchronized (serverSocketTester) {
                serverSocketTester.wait();
            }
        }
    }

    private static void commonInit() {
        step = new Step();
        step.setCommandPrefix("python3 test.py");
        step.setCommandSuffix("");
        step.setOutputType(IOtypesEnum.FOLDER_OF_FILES);
        step.setInputRegex("(.*)\\/(.*)(\\.in)");
        step.setOutputPath(TEST_PATH_TO_OUTPUT);
        step.setPathToLog(TEST_PATH_TO_LOG);
        step.setPathToErrLog(TEST_PATH_TO_ERR_LOG);
        step.setPathToSecondOutputFolder(TEST_PATH_TO_SECOND_OUTPUT);
        step.setLogSizeLessThan(new Long(10));

        step.setSaveErrLog(true);
        step.setCheckLogFileSize(true);
        step.setEqualInToOutFiles(true);

        message = null;
    }

    private void receiveMessage() throws InterruptedException {
        GenericSocketMsg receivedMsg;
        while (message == null) {
            Thread.sleep(500);

            receivedMsg = serverSocketTester.getReceiveMessage();
            if (receivedMsg != null && receivedMsg.getMessageType() == MsgType.PROC_ERROR) {
                message = (ProcErrorMsg) receivedMsg;
            }
        }
    }

    /**
     * Test of checkAndLoadInputs method, of class InputLoader.
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    @Test
    public void testCheckAndLoadInputs() throws IOException, InterruptedException {
        int numberOfInputs;

        // test nonexist input
        commonInit();
        step.setInputPath(PATH_TO_TEST_FOLDER + NONEXIST_FILE_NAME);
        step.setInputType(IOtypesEnum.FILE);
        RunValues runValues = new RunValues();
        runValues.setStep(step);
        runValues.setErrorInputs(new ArrayList<>());
        runValues.setRunType(RunType.RUN_WITHOUT_ERRORS);
        numberOfInputs = InputLoader.getInstance().checkAndLoadInputs(runValues);

        assertEquals(0, numberOfInputs);
        this.receiveMessage();

        assertEquals(IOErrorMsg.getNonExistPathMsg(step.getInputPath()), message.getMessage().getError().getDescription());

        // input is folder, but input type is file
        commonInit();
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FILES);
        step.setInputType(IOtypesEnum.FILE);
        runValues.setStep(step);
        numberOfInputs = InputLoader.getInstance().checkAndLoadInputs(runValues);

        assertEquals(0, numberOfInputs);

        this.receiveMessage();
        assertEquals(IOErrorMsg.getUnexpInputTypeMsg(step.getInputType(), IOtypesEnum.FOLDER), message.getMessage().getError().getDescription());

        // empty input file
        commonInit();
        step.setInputPath(PATH_TO_TEST_FOLDER + EMPTY_FILE_NAME);
        step.setInputType(IOtypesEnum.FILE);
        runValues.setStep(step);
        numberOfInputs = InputLoader.getInstance().checkAndLoadInputs(runValues);

        assertEquals(0, numberOfInputs);

        this.receiveMessage();
        assertEquals(IOErrorMsg.getEmptyInputMsg(step.getInputType(), step.getInputPath()), message.getMessage().getError().getDescription());

        // empty input folder
        commonInit();
        step.setInputType(IOtypesEnum.FOLDER);
        step.setInputPath(PATH_TO_TEST_FOLDER + EMPTY_FOLDER_NAME);
        runValues.setStep(step);
        numberOfInputs = InputLoader.getInstance().checkAndLoadInputs(runValues);

        assertEquals(0, numberOfInputs);

        this.receiveMessage();
        assertEquals(IOErrorMsg.getEmptyInputMsg(step.getInputType(), step.getInputPath()), message.getMessage().getError().getDescription());

        // load one file
        commonInit();
        step.setInputRegex("(.*)\\/(.*)(\\.txt)");
        step.setInputType(IOtypesEnum.FILE);
        step.setInputPath(PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME);
        runValues.setStep(step);
        
        assertEquals(1, InputLoader.getInstance().checkAndLoadInputs(runValues));
        assertEquals("[" + PATH_TO_TEST_FOLDER + NONEMPTY_FILE_NAME + "]", secStatusMaintainer.getCopyOfInputs().toString());

        // load folder
        commonInit();
        step.setInputRegex("(.*)\\/(.*)");
        step.setInputType(IOtypesEnum.FOLDER);
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FILES);
        runValues.setStep(step);
        
        assertEquals(1, InputLoader.getInstance().checkAndLoadInputs(runValues));
        assertEquals("[" + TEST_INPUT_PATH_FOLDER_OF_FILES + "]", secStatusMaintainer.getCopyOfInputs().toString());

        // load folder of files
        commonInit();
        step.setInputRegex("(.*)\\/(.*)");
        step.setInputType(IOtypesEnum.FOLDER_OF_FILES);
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FILES);
        step.setOutputPath(TEST_PATH_TO_EMPTY_OUTPUT);
        runValues.setStep(step);

        assertEquals(3, InputLoader.getInstance().checkAndLoadInputs(runValues));
        assertEquals("[" + TEST_INPUT_PATH_FOLDER_OF_FILES + TEST_INPUT_NAME_1 + ", " + TEST_INPUT_PATH_FOLDER_OF_FILES + TEST_INPUT_NAME_2 + ", " + TEST_INPUT_PATH_FOLDER_OF_FILES + TEST_INPUT_NAME_3 + "]", secStatusMaintainer.getCopyOfInputs().toString());
        // [D:/FIT/Ing/DIP/TestFolder/In/in1.in, D:/FIT/Ing/DIP/TestFolder/In/in2.in, D:/FIT/Ing/DIP/TestFolder/In/in3.in]

        // load folder of folders
        commonInit();
        step.setInputRegex("(.*)\\/(.*)");
        step.setInputType(IOtypesEnum.FOLDER_OF_FOLDERS);
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FOLDERS);
        runValues.setStep(step);

        assertEquals(3, InputLoader.getInstance().checkAndLoadInputs(runValues));

        // test existing output
        commonInit();
        step.setInputType(IOtypesEnum.FOLDER_OF_FILES);
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FILES);
        step.setOutputPath(TEST_PATH_TO_OUTPUT);//????
        
        runValues.setStep(step);
        
        assertEquals(0, InputLoader.getInstance().checkAndLoadInputs(runValues));
        assertEquals("[]", secStatusMaintainer.getCopyOfInputs().toString());
        // [D:/FIT/Ing/DIP/TestFolder/In/in1.in, D:/FIT/Ing/DIP/TestFolder/In/in2.in, D:/FIT/Ing/DIP/TestFolder/In/in3.in]

        // load folder of folders
        commonInit();
        step.setInputType(IOtypesEnum.FOLDER_OF_FOLDERS);
        step.setOutputType(IOtypesEnum.FOLDER_OF_FOLDERS);
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FOLDERS);
        step.setOutputPath(TEST_PATH_TO_OUT_FOLDER_OF_FOLDERS);
        step.setInputRegex("(.*)\\/(.*)");
        runValues.setStep(step);
        assertEquals(2, InputLoader.getInstance().checkAndLoadInputs(runValues));
    }

    /**
     * Test of CreateAllOutputFolders method, of class InputLoader.
     */
    @Test
    public void testCreateAllOutputFolders() throws IOException {
        commonInit();
        step.setInputType(IOtypesEnum.FOLDER_OF_FOLDERS);
        step.setOutputType(IOtypesEnum.FOLDER_OF_FOLDERS);
        step.setInputPath(TEST_INPUT_PATH_FOLDER_OF_FOLDERS);
        step.setOutputPath(PATH_TO_OUTPUT_FOLDER_CREATE_FOLDERS_TEST);
        step.setInputRegex("(.*)\\/(.*)");
        
        RunValues runValues = new RunValues();
        runValues.setStep(step);
        runValues.setRunType(RunType.RUN_WITHOUT_ERRORS);
        runValues.setErrorInputs(new ArrayList<>());
        
        InputLoader.getInstance().checkAndLoadInputs(runValues);
        InputLoader.getInstance().CreateAllOutputFolders(step);
        assertEquals("[" + TEST_OUTPUT_FOLDER_NAME_1 + ", " + TEST_OUTPUT_FOLDER_NAME_2 + ", " + TEST_OUTPUT_FOLDER_NAME_3 + "]", FileManager.getAllFsNodes(PATH_TO_TEST_FOLDER + "OutFolderTest", FileManager.FsNodeType.FOLDER).toString());

        // delete created output folders
        File folder = new File(OUTPUT_FOLDER_CREATE_FOLDERS_TEST);
        for (String f : folder.list()) {
            File subFolder = new File(folder.getPath(), f);
            subFolder.delete();
        }
        folder.delete();
    }
}
