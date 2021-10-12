/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.outputValidator;

import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import com.app.secondaryNodeApp.errMessages.ValidateErrorMsg;
import com.app.secondaryNodeApp.fileManager.FileManager;
import java.util.List;

/**
 * Class which check if execution of step was ended properly.
 * @author Filip
 */
public class OutputValidator {
    
    /** OBJECT PROPERY **/
    
    private static Step step;
    private static String stderrContent;
    private static String actInputFsNodePath;
    private static String actOutputFsNodePath;
    private static String actLogFsNodePath;
    private static String actPathToSecondOutFolder;
    
    /** OBJECT METHODS **/
    
    /**
     * @param step Step, which has been ran.
     * @param stderrContent Content of stderr.
     * @param actInputFsNodePath Path to input file/folder with which the step has been ran.
     */
    private static void initialize (Step step, String stderrContent, String actInputFsNodePath) {
        OutputValidator.step = step;
        OutputValidator.stderrContent = stderrContent;
        OutputValidator.actInputFsNodePath = actInputFsNodePath;
        OutputValidator.actOutputFsNodePath = step.getOutputPath() == null ? null : FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getOutputPath());
        OutputValidator.actLogFsNodePath = step.getPathToLog() == null ? null : FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getPathToLog());
        OutputValidator.actPathToSecondOutFolder = step.getPathToSecondOutputFolder() == null ? null : FileManager.getPathToOutputFsNode(step.getInputRegex(), actInputFsNodePath, step.getPathToSecondOutputFolder());
    }
    
    /**
     * Check if output file or folder is not empty.
     * @return The error message if input file or folder is not empty. Null otherwise.
     */
    private static String checkNoEmptyOutputFsNode() {           
        if ((step.getOutputType() == IOtypesEnum.FILE || step.getOutputType() == IOtypesEnum.FOLDER_OF_FILES)) {
            if (!FileManager.isExistPath(actOutputFsNodePath))
                return ValidateErrorMsg.getNonExistOutFileErr(actInputFsNodePath, actOutputFsNodePath);     
            if (FileManager.isFolder(actOutputFsNodePath))
                return ValidateErrorMsg.getUnexpTypeOutFileErr(actInputFsNodePath, actOutputFsNodePath);            
            if (!FileManager.isNonEmptyFile(actOutputFsNodePath)) 
                return ValidateErrorMsg.getEmptyOutFileErr(actInputFsNodePath, actOutputFsNodePath);  
        }
        else {
            if (!FileManager.isExistPath(actOutputFsNodePath))
                return ValidateErrorMsg.getNonExistOutFolderErr(actInputFsNodePath, actOutputFsNodePath);     
            if (!FileManager.isFolder(actOutputFsNodePath))
                return ValidateErrorMsg.getUnexpTypeFolderErr(actInputFsNodePath, actOutputFsNodePath);
            if (!FileManager.isNonEmptyFolder(actOutputFsNodePath))
                return ValidateErrorMsg.getEmptyOutFolderErr(actInputFsNodePath, actOutputFsNodePath);  
        }
        return null;
    }
    
    /**
     * Check if stderr is empty.
     * @return The error message if stderr is not empty. Null otherwise.
     */
    private static String checkEmptyStderr() {
        if (stderrContent.equals(""))
            return null;
        else
            return ValidateErrorMsg.getNoEmptyStderrErr();
    }
    
    /**
     * Check if size of specified log is less than entered value.
     * @return The error message if file size is higher or equal than entered value. Null otherwise.
     */
    private static String checkCorrectLogSize() {
        
        Long fileLength = FileManager.getFsNodeLength(actLogFsNodePath);
        if (fileLength == null)
            return ValidateErrorMsg.getNonExistLogFileErr(actLogFsNodePath);
        else if (fileLength > step.getLogSizeLessThan())
            return ValidateErrorMsg.getTooBigSizeOfLogFileErr(actLogFsNodePath, step.getLogSizeLessThan(), fileLength);
        else
            return null;
    }
    
    /**
     * Check if number of files in input folder is equal to number of output folder.
     * @param outputPath Path to output folder.
     * @return The error message if the number of input files is not equal to number of output files. Null otherwise.
     */
    private static String checkEqualInToOutFiles(String actOutputFsNodePath) {
        
        List<String> InFiles, OutFiles;
        if (step.getInputType() == IOtypesEnum.FOLDER && step.getOutputType() == IOtypesEnum.FOLDER) {            
             
            if (!FileManager.isExistPath(actOutputFsNodePath))
                return ValidateErrorMsg.getNonExistOutFolderErr(actInputFsNodePath, actOutputFsNodePath);     
            if (!FileManager.isFolder(actOutputFsNodePath))
                return ValidateErrorMsg.getUnexpTypeFolderErr(actInputFsNodePath, actOutputFsNodePath);
            
            // the check if actInputFsNodePath exists and if it is folder was performed when the program is started
            InFiles = FileManager.getAllFsNodes(actInputFsNodePath, FileManager.FsNodeType.FILE);
            OutFiles = FileManager.getAllFsNodes(actOutputFsNodePath, FileManager.FsNodeType.FILE);
            
            if (InFiles.size() == OutFiles.size())
                return null;
            else
               return ValidateErrorMsg.getNonEqualInToOutFilesErr(InFiles.size(), OutFiles.size()); 
        }
        return ValidateErrorMsg.getUnexpInToOutEqValidationErr();
    }
    
        
    /**
     * Check if the step executing is ended succesfully.
     * @param step Step, which has been ran.
     * @param stderrContent Content of stderr.
     * @param actInputFsNodePath Path to input file/folder with which the step has been ran.
     * @return The error message which descibe the error if it is occurs. Null othewrise.
     */
    public static String checkOutputValidators(Step step, String stderrContent, String actInputFsNodePath) {
        initialize(step, stderrContent, actInputFsNodePath);
        String errDescription = null;        
        
        if (step.isExistNoEmptyOutputFsNode() && (errDescription = checkNoEmptyOutputFsNode()) != null)                                         return errDescription;
        if (step.isEmptyStderr() && (errDescription = checkEmptyStderr()) != null)                                                              return errDescription;
        if (step.isCheckLogFileSize() && (errDescription = checkCorrectLogSize()) != null)                                                      return errDescription;
        if (step.isEqualInToOutFiles() && (errDescription = checkEqualInToOutFiles(actOutputFsNodePath)) != null)                               return errDescription;
        if (step.isEqualInToOutFilesSecondOutput() && (errDescription = checkEqualInToOutFiles(actPathToSecondOutFolder)) != null)              return errDescription;
        
        return errDescription;
    }
}
