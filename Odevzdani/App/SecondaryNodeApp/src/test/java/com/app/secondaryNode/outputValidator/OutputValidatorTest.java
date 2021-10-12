/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNode.outputValidator;

import com.app.secondaryNodeApp.outputValidator.OutputValidator;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import static com.app.secondaryNode.TestSettings.*;
import com.app.secondaryNodeApp.errMessages.ValidateErrorMsg;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author filip
 */
public class OutputValidatorTest {
    
    
    private static Step step;
    private static String actInputFsNodePath;
    private static String stderrContent;
    
    public OutputValidatorTest() {
    }

    
    public void commonInit() {
        step = new Step();
        step.setOutputType(IOtypesEnum.FILE);
        step.setInputRegex("(.*)\\/(.*)(\\.in)");
        step.setOutputPath(TEST_PATH_TO_OUTPUT);
        step.setPathToLog(TEST_PATH_TO_LOG);
        step.setPathToSecondOutputFolder(TEST_PATH_TO_SECOND_OUTPUT);
        
        step.setLogSizeLessThan(new Long(10));
        
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES + TEST_INPUT_NAME_1;            
        stderrContent = "";      
    }
    public void initExistNoEmptyOutputFsNode() {
        commonInit();
        step.setExistNoEmptyOutputFsNode(true);
        step.setEmptyStderr(false);
        step.setCheckLogFileSize(false);
        step.setEqualInToOutFiles(false);
        step.setEqualInToOutFilesSecondOutput(false);       
    }
    
    public void initIsEmptyStderr() {
        commonInit();
        step.setExistNoEmptyOutputFsNode(false);
        step.setEmptyStderr(true);
        step.setCheckLogFileSize(false);
        step.setEqualInToOutFiles(false);
        step.setEqualInToOutFilesSecondOutput(false); 
    }
    
    public void initCheckLogFileSize() {
        commonInit();
        step.setExistNoEmptyOutputFsNode(false);
        step.setEmptyStderr(false);
        step.setCheckLogFileSize(true);
        step.setEqualInToOutFiles(false);
        step.setEqualInToOutFilesSecondOutput(false); 
    }
    
    public void initIsEqualInToOutFiles() {
        commonInit();
        step.setInputRegex("(.*)\\/(.*)");
        step.setOutputType(IOtypesEnum.FOLDER);
        step.setInputType(IOtypesEnum.FOLDER);
        step.setExistNoEmptyOutputFsNode(false);
        step.setEmptyStderr(false);
        step.setCheckLogFileSize(false);
        step.setEqualInToOutFiles(true);
        step.setEqualInToOutFilesSecondOutput(false); 
    }
    
    public void initIsEqualInToOutFilesSecOutput() {
        commonInit();
        step.setInputRegex("(.*)\\/(.*)");
        step.setOutputType(IOtypesEnum.FOLDER);
        step.setInputType(IOtypesEnum.FOLDER);
        step.setExistNoEmptyOutputFsNode(false);
        step.setEmptyStderr(false);
        step.setCheckLogFileSize(false);
        step.setEqualInToOutFiles(false);
        step.setEqualInToOutFilesSecondOutput(true); 
    }
    
    /**
     * Test of no empty output file system node.
     */
    @Test
    public void testExistNoEmptyOutputFsNode() {
        
        initExistNoEmptyOutputFsNode();
        
        // everything OK
        assertEquals(null, OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // nonexist output file
        initExistNoEmptyOutputFsNode();        
        step.setOutputPath(PATH_TO_TEST_FOLDER+NONEXIST_FILE_NAME);
        assertEquals(ValidateErrorMsg.getNonExistOutFileErr(actInputFsNodePath, PATH_TO_TEST_FOLDER+NONEXIST_FILE_NAME), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // wrong output type
        initExistNoEmptyOutputFsNode();      
        step.setOutputType(IOtypesEnum.FOLDER);
        assertEquals(ValidateErrorMsg.getUnexpTypeFolderErr(actInputFsNodePath, TEST_OUTPUT_FOLDER + TEST_OUTPUT_NAME_1), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // empty output
        initExistNoEmptyOutputFsNode();        
        step.setOutputPath(PATH_TO_TEST_FOLDER+EMPTY_FILE_NAME);
        assertEquals(ValidateErrorMsg.getEmptyOutFileErr(actInputFsNodePath, PATH_TO_TEST_FOLDER+EMPTY_FILE_NAME), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));       
    }
    
    /**
     * Test of no empty stderr.
     */
    @Test
    public void testNoEmptyStderr() {
        initIsEmptyStderr();
        
        // everything OK - empty stderr
        assertEquals(null, OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // nonempty stderr
        stderrContent = "Some error";
        assertEquals(ValidateErrorMsg.getNoEmptyStderrErr(), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
    }
    
    /**
     * Test of log file size.
     */
    @Test
    public void testCheckLogFileSize() {
        initCheckLogFileSize();
        
        // nonexist log file
        step.setPathToLog(PATH_TO_TEST_FOLDER+NONEXIST_FILE_NAME);
        assertEquals(ValidateErrorMsg.getNonExistLogFileErr(step.getPathToLog()), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // size of log file is higher then max. value
        initCheckLogFileSize();
        assertEquals(ValidateErrorMsg.getTooBigSizeOfLogFileErr(TEST_LOG_FOLDER + TEST_LOG_OUTPUT_NAME_1, step.getLogSizeLessThan(), 14), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
    
    
        // size of log file is less then max. value
        initCheckLogFileSize();
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES + TEST_INPUT_NAME_2;          
        assertEquals(null, OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
    }
    
    /**
     * Test if number of input files is equal to number of output files
     */
    @Test
    public void testIsEqualInToOutFiles() {
        initIsEqualInToOutFiles();
        
        // the number of input files is equal to number of output files
        step.setOutputPath(TEST_OUTPUT_FOLDER);
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        assertEquals(null, OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // the number of input files is not equal to number of output files
        initIsEqualInToOutFiles();
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        step.setOutputPath(TEST_PATH_TO_BAD_OUTPUT_NUMBER);
        assertEquals(ValidateErrorMsg.getNonEqualInToOutFilesErr(3, 2), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));

        // output doesn't exist
        initIsEqualInToOutFiles();
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        step.setOutputPath(PATH_TO_TEST_FOLDER+NONEXIST_FOLDER_NAME);
        assertEquals(ValidateErrorMsg.getNonExistOutFolderErr(actInputFsNodePath, step.getOutputPath()), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // output is file, not folder
        initIsEqualInToOutFiles();
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        step.setOutputPath(PATH_TO_TEST_FOLDER+NONEMPTY_FILE_NAME);
        assertEquals(ValidateErrorMsg.getUnexpTypeFolderErr(actInputFsNodePath, step.getOutputPath()), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
    }
    
    
    /**
     * Test if number of input files is equal to number of second output files
     */
    @Test
    public void testIsEqualInToOutFilesSecOutput() {
        
        // the number of input files is equal to number of output files
        initIsEqualInToOutFilesSecOutput();
        step.setPathToSecondOutputFolder(TEST_SECOND_OUTPUT_FOLDER);
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        assertEquals(null, OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // the number of input files is not equal to number of output files
        initIsEqualInToOutFilesSecOutput();
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        step.setPathToSecondOutputFolder(TEST_PATH_TO_BAD_OUTPUT_NUMBER);
        assertEquals(ValidateErrorMsg.getNonEqualInToOutFilesErr(3, 2), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));

        // output doesn't exist
        initIsEqualInToOutFilesSecOutput();
        step.setPathToSecondOutputFolder(PATH_TO_TEST_FOLDER+NONEXIST_FOLDER_NAME);
        assertEquals(ValidateErrorMsg.getNonExistOutFolderErr(actInputFsNodePath, step.getPathToSecondOutputFolder()), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
        
        // output is file, not folder
        initIsEqualInToOutFilesSecOutput();
        actInputFsNodePath = TEST_INPUT_PATH_FOLDER_OF_FILES;
        step.setPathToSecondOutputFolder(PATH_TO_TEST_FOLDER+NONEMPTY_FILE_NAME);
        assertEquals(ValidateErrorMsg.getUnexpTypeFolderErr(actInputFsNodePath, step.getPathToSecondOutputFolder()), OutputValidator.checkOutputValidators(step, stderrContent, actInputFsNodePath));
    }
}
