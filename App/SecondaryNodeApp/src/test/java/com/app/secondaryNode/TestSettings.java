/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNode;

/**
 *
 * @author filip
 */
public class TestSettings {
    
    public static final String PATH_TO_SECONDARY_NODE_CONFIG_FILE = "D:/FIT/Ing/DIP/gja---parallel-data-processing-application/UnitTests/Configuration.json";
    public static final String PATH_TO_SERVER_KEYSTORE = "C:/Users/filip/GlassFish_Server/glassfish/domains/domain1/config/cacerts.jks";
    public static final String TESTSERVER_KEYSTORE_PASSWORD = "changeit";

    public static final String PATH_TO_TEST_FOLDER = "D:/FIT/Ing/DIP/gja---parallel-data-processing-application/UnitTests/TestFolder/";
    public static final String PATH_TO_TEST_EXECUTE_FOLDER = "D:/FIT/Ing/DIP/gja---parallel-data-processing-application/UnitTests/TestExecuteFolder/";
    public static final String TEST_NODE_NAME = "TestNode";

    public static final String EMPTY_FOLDER_NAME = "EmptyFolder";
    public static final String NONEMPTY_FOLDER_NAME = "NonemptyFolder";
    public static final String EMPTY_FILE_NAME = "emptyFile.txt";
    public static final String NONEMPTY_FILE_NAME = "nonemptyFile.txt";

    public static final String NONEXIST_FOLDER_NAME = "nonexistFolder";
    public static final String NONEXIST_FILE_NAME = "nonexistFile";

    public static final String TMP_FILE_NAME = "tmpFile.txt";
    public static final String TMP_FOLDER_NAME = "tmpFolder";

    // nested fs nodes in NONEMPTY_FOLDER_NAME folder
    public static final String NESTED_FOLDER_NAME_1 = "TrashFolder1";
    public static final String NESTED_FOLDER_NAME_2 = "TrashFolder2";
    public static final String NESTED_FILE_NAME_1 = "trash1.txt";
    public static final String NESTED_FILE_NAME_2 = "trash2.txt";
    public static final String NESTED_FOLDER_1_CONTENT = "trash";
    public static final String NESTED_FOLDER_2_CONTENT = "trash trash trash\ntrash trash\ntrash";

    // test task - MainTest
    public static final String PATH_TO_PYTHON_SUCCESS_TASK = "D:/FIT/Ing/DIP/gja---parallel-data-processing-application/UnitTests/UnitTestTask.py";
    public static final String PATH_TO_INPUT_SUCCESS_TASK = PATH_TO_TEST_EXECUTE_FOLDER + "InFiles";
    public static final String EXEC_OUTPUT_PATH = PATH_TO_TEST_EXECUTE_FOLDER + "OutFiles/$2.out";
    public static final String EXEC_PATH_TO_LOG = PATH_TO_TEST_EXECUTE_FOLDER + "Log/$2.log";
    public static final String EXEC_PATH_TO_ERR_LOG = PATH_TO_TEST_EXECUTE_FOLDER + "ErrLog/$2.errLog";
    public static final String EXEC_PATH_TO_SECOND_OUTPUT = PATH_TO_TEST_EXECUTE_FOLDER + "SecondOut/$2.secondOut";

    // test fs nodes - OutputValidatorTest
    public static final String TEST_INPUT_PATH_FOLDER_OF_FILES = PATH_TO_TEST_FOLDER + "InFiles";
    public static final String TEST_OUTPUT_FOLDER = PATH_TO_TEST_FOLDER + "OutFiles";
    public static final String TEST_PATH_TO_OUTPUT = TEST_OUTPUT_FOLDER + "/$2.out";
    public static final String TEST_LOG_FOLDER = PATH_TO_TEST_FOLDER + "Log";
    public static final String TEST_PATH_TO_LOG = TEST_LOG_FOLDER + "/$2.log";
    public static final String TEST_SECOND_OUTPUT_FOLDER = PATH_TO_TEST_FOLDER + "SecondOut";
    public static final String TEST_PATH_TO_SECOND_OUTPUT = TEST_SECOND_OUTPUT_FOLDER + "/$2.secondOut";
    public static final String TEST_PATH_TO_BAD_OUTPUT_NUMBER = PATH_TO_TEST_FOLDER + "/OutBad";

    public static final String TEST_INPUT_NAME_1 = "/in1.in";
    public static final String TEST_INPUT_NAME_2 = "/in2.in";
    public static final String TEST_INPUT_NAME_3 = "/in3.in";

    public static final String TEST_OUTPUT_NAME_1 = "/in1.out";
    public static final String TEST_LOG_OUTPUT_NAME_1 = "/in1.log";

    // test fs nodes - InputLoaderTest
    public static final String TEST_PATH_TO_ERR_LOG = PATH_TO_TEST_FOLDER + "/ErrLog/$1.errLog";
    public static final String TEST_INPUT_PATH_FOLDER_OF_FOLDERS = PATH_TO_TEST_FOLDER + "InFolders";
    public static final String TEST_PATH_TO_EMPTY_OUTPUT = PATH_TO_TEST_FOLDER + "/EmptyOut";
    public static final String TEST_PATH_TO_OUT_FOLDER_OF_FOLDERS = PATH_TO_TEST_FOLDER + "/OutFolders/$2_Out";
    public static final String OUTPUT_FOLDER_CREATE_FOLDERS_TEST = PATH_TO_TEST_FOLDER + "OutFolderTest";
    public static final String PATH_TO_OUTPUT_FOLDER_CREATE_FOLDERS_TEST = OUTPUT_FOLDER_CREATE_FOLDERS_TEST + "/$2_Out";

    public static final String TEST_OUTPUT_FOLDER_NAME_1 = "inFolder1_Out";
    public static final String TEST_OUTPUT_FOLDER_NAME_2 = "inFolder2_Out";
    public static final String TEST_OUTPUT_FOLDER_NAME_3 = "inFolder3_Out";

}
