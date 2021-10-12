/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.secondaryNodeApp.stepExecution;

import com.app.commons.socketMessages.sendingItems.RunValues;
import com.app.commons.socketMessages.sendingItems.RunValues.RunType;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.secondaryNodeApp.secConnStatusMaintainer.SecStatusMaintainer;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import com.app.secondaryNodeApp.errMessages.IOErrorMsg;
import com.app.secondaryNodeApp.fileManager.FileManager;
import com.app.secondaryNodeApp.secSocketCommunicators.SecSocketSender;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * @author Filip
 */
public class InputLoader {
    
    /** STATIC PROPERTY **/
    
    private static InputLoader inputLoader = null;
    
    private static SecStatusMaintainer secStatusMaintainer;
    private static SecSocketSender secSocketSender = null;
    private static int numberOfInputs;
    private static RunValues runValues;
    
    /** STATIC METHOD **/
    
    /**
     * Method which return reference to singleton InputLoader object.
     * @return Reference to singleton InputLoader object.
     * @throws IOException If an I/O exception occurs.
     */
    public static InputLoader getInstance() throws IOException {
       if (inputLoader == null) {
           inputLoader = new InputLoader();
       }
       return inputLoader;
    }
    
    /**
     * Private constructor.
     * @throws IOException If an I/O exception occurs. 
     */
    private InputLoader() throws IOException {
        InputLoader.secSocketSender = SecSocketSender.getInstance();
        InputLoader.secStatusMaintainer = SecStatusMaintainer.getInstance();       
    }
        
    /**
     * Check if path to input exists and if the received type of input corresponds with real input type.
     * @return True if path to input exists and received type of input corresponds with real input type. False otherwise.
     * @throws IOException 
     */
    private void isValidInputType() {
        
        // verify existing input
        if (!FileManager.isExistPath(runValues.getStep().getInputPath())) {
            throw new RuntimeException(IOErrorMsg.getNonExistPathMsg(runValues.getStep().getInputPath()));
        }
        
        switch(runValues.getStep().getInputType()) {
            case FILE:
                if (FileManager.isFolder(runValues.getStep().getInputPath())) {
                    throw new RuntimeException(IOErrorMsg.getUnexpInputTypeMsg(runValues.getStep().getInputType(), IOtypesEnum.FOLDER));
                }
                else if (!FileManager.isNonEmptyFile(runValues.getStep().getInputPath())) {
                    throw new RuntimeException(IOErrorMsg.getEmptyInputMsg(runValues.getStep().getInputType(), runValues.getStep().getInputPath()));
                }
                break;
            default:
                if (!FileManager.isFolder(runValues.getStep().getInputPath())) {
                    throw new RuntimeException(IOErrorMsg.getUnexpInputTypeMsg(runValues.getStep().getInputType(), IOtypesEnum.FILE));
                }
                else if (!FileManager.isNonEmptyFolder(runValues.getStep().getInputPath())) {
                    throw new RuntimeException(IOErrorMsg.getEmptyInputMsg(runValues.getStep().getInputType(), runValues.getStep().getInputPath()));
                }
                break;
        }
    }
    
    /**
     * Check if exist output fs node to input fs node (if the input should be processed).
     * @param fsNodePath Name of input file/folder.
     * @return True if output file/folder exists. False otherwise.
     */
    private boolean isNonEmptyOutputFsNode(String fsNodePath) throws IOException {
        
        String pathToOutputFsNode = FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodePath, runValues.getStep().getOutputPath());

        switch(runValues.getStep().getOutputType()) {
            case FILE:
            case FOLDER_OF_FILES:
                if (FileManager.isNonEmptyFile(pathToOutputFsNode)) {
                        return true;
                }
                break;
            case FOLDER:
            case FOLDER_OF_FOLDERS:
                if (FileManager.isNonEmptyFolder(pathToOutputFsNode)) {
                    return true;
                }
                break;
        }
        return false;
    }
    
    /**
     * Check if the input regex is applicable correctly to the path to output file/folder - if the input regex has correct format, if there is no groups out of range etc.
     * @param fsNodePath Path to the input file/folder.
     * @return True if input regex is applicable correctly. False otherwise.
     * @throws IOException If an I/O exception occurs.
     */
    private void isCorrectInputRegex(String fsNodeInputPath) {
        try {
            FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodeInputPath, runValues.getStep().getCommandPrefix());
            FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodeInputPath, runValues.getStep().getOutputPath());
            FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodeInputPath, runValues.getStep().getCommandSuffix());
            if (runValues.getStep().isSaveErrLog())
                FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodeInputPath, runValues.getStep().getPathToErrLog());
            if (runValues.getStep().isCheckLogFileSize())
                FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodeInputPath, runValues.getStep().getPathToLog());
            if (runValues.getStep().isEqualInToOutFilesSecondOutput())
                FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), fsNodeInputPath, runValues.getStep().getPathToSecondOutputFolder());
        } catch (IndexOutOfBoundsException | PatternSyntaxException | IllegalStateException e) {
            throw new RuntimeException(IOErrorMsg.getCannotUseInputRegexMsg(e));
        } 
    }
    
    /**
     * Method, which loads all input files or folders if input type is folder of files or folder of folders.
     * @throws IOException If an I/O exception occurs.
     */
    private void loadInputs() throws IOException {
        File folder = new File(runValues.getStep().getInputPath());
        String[] inputs = folder.list();
        
        for (String fsNode : inputs) {            
            String fsNodeInputPath = runValues.getStep().getInputPath()+(runValues.getStep().getInputPath().endsWith("/") ? "" : '/')+fsNode;
            
            // check if input regex is in correct format and the used groups are not out of bound in all entered paths
            isCorrectInputRegex(fsNodeInputPath);
            
            if (runValues.getStep().getInputType() == IOtypesEnum.FOLDER_OF_FILES && !FileManager.isFolder(fsNodeInputPath)) {
                if (!FileManager.isNonEmptyFile(fsNodeInputPath) ||         // check if input is nonempty
                        isNonEmptyOutputFsNode(fsNodeInputPath) ||          // check if output is empty
                        runValues.getErrInputs().contains(fsNodeInputPath)) // check if input is error input
                    continue;        
                numberOfInputs++;
                secStatusMaintainer.addInput(fsNodeInputPath);
            }
            else if (runValues.getStep().getInputType() == IOtypesEnum.FOLDER_OF_FOLDERS && FileManager.isFolder(fsNodeInputPath)) {
                if (!FileManager.isNonEmptyFolder(fsNodeInputPath) ||       // check if input is nonempty
                        isNonEmptyOutputFsNode(fsNodeInputPath) ||          // check if output is empty
                        runValues.getErrInputs().contains(fsNodeInputPath)) // check if input is error input
                    continue;
                numberOfInputs++;
                secStatusMaintainer.addInput(fsNodeInputPath);
            }
        }
    }
    
    /**
     * Method, which loads ale input files or folders into list and check if there is at least one input.
     * @param runValues Data about step of task to be ran.
     * @return The number of loaded inputs.
     * @throws IOException If an I/O exception occurs.
     */
    public int checkAndLoadInputs(RunValues runValues) throws IOException {
        numberOfInputs = 0;
        secStatusMaintainer.clearInputs();
        InputLoader.runValues = runValues;
        
        try {
            isValidInputType();
            if (runValues.getRunType() ==  RunType.RUN_WITH_ERRORS || runValues.getRunType() == RunType.RUN_WITHOUT_ERRORS) {
                switch(runValues.getStep().getInputType()) {
                    case FILE:
                    case FOLDER:
                        isCorrectInputRegex(runValues.getStep().getInputPath());
                        if (    !runValues.getErrInputs().contains(runValues.getStep().getInputPath()) && 
                                !isNonEmptyOutputFsNode(runValues.getStep().getInputPath())) {
                            secStatusMaintainer.addInput(runValues.getStep().getInputPath());
                            numberOfInputs++;
                        }
                        break;                
                    case FOLDER_OF_FILES:
                   case FOLDER_OF_FOLDERS:
                        loadInputs();
                        break;
                }
            }
        
            // add error inputs to the top of stack of inputs
            if (runValues.getRunType() == RunValues.RunType.RUN_WITH_ERRORS || runValues.getRunType() == RunValues.RunType.RUN_ONLY_ERRORS) {
                numberOfInputs = runValues.getErrInputs().stream().map((errorInput) -> {
                    SecStatusMaintainer.getInstance().addInput(errorInput);
                    return errorInput;
                }).map((_item) -> 1).reduce(numberOfInputs, Integer::sum);
            }


            if (numberOfInputs == 0) {
                 switch(runValues.getStep().getInputType()) {
                        case FILE:
                        case FOLDER:
                            String pathToOutputFsNode = FileManager.getPathToOutputFsNode(runValues.getStep().getInputRegex(), runValues.getStep().getInputPath(), runValues.getStep().getOutputPath());
                            throw new RuntimeException(IOErrorMsg.getNonemptyOutputMsg(runValues.getStep().getOutputType(), pathToOutputFsNode));
                        case FOLDER_OF_FILES:
                            throw new RuntimeException(IOErrorMsg.getDiffInputTypesMsg(runValues.getStep().getInputPath(), IOtypesEnum.FILE));
                        case FOLDER_OF_FOLDERS:
                            throw new RuntimeException(IOErrorMsg.getDiffInputTypesMsg(runValues.getStep().getInputPath(), IOtypesEnum.FOLDER));
                    }
            }
        } catch (RuntimeException e) {
            secSocketSender.sendIOError(e.getMessage());
            return 0;
        }
        
        return numberOfInputs;        
    }
    
    /**
     * Check if output folder(s) are created.If some folder doesn't exist, the folder is created.
     * @param step The step of task to be executed.
     * @return False if output folder(s) don't exist and cannot be created. True otherwise. 
     * @throws IOException If an I/O exception occurs.
     */
    public boolean CreateAllOutputFolders(Step step) throws IOException {
        
        ArrayList<String> outputFolders = new ArrayList<>();
        Stack<String> inputs = secStatusMaintainer.getCopyOfInputs();
        switch (step.getOutputType()) {
            case FILE:
            case FOLDER_OF_FILES:                
                while(!inputs.empty()) {
                    String output = FileManager.getPathToOutputFsNode(step.getInputRegex(), inputs.pop(), step.getOutputPath());
                    if (output.contains("/")) {
                        outputFolders.add(output.substring(0, output.lastIndexOf('/')));
                    }
                }
                break;            
            case FOLDER:
            case FOLDER_OF_FOLDERS:
            default:                
                while(!inputs.empty()) {
                    outputFolders.add(FileManager.getPathToOutputFsNode(step.getInputRegex(), inputs.pop(), step.getOutputPath()));
                }
                break;
        }
        
        for (String outputFolder : outputFolders) {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                try {
                    if (!folder.mkdirs()) {
                        secSocketSender.sendIOError(IOErrorMsg.getCannotCreateOutFolderMsg(outputFolder));
                        return false;
                    }
                } catch (IOException e) {
                    secSocketSender.sendIOError(IOErrorMsg.getCannotCreateOutFolderMsg(outputFolder, e));
                    return false;
                }
            }
        }  
        return true;
    }    
}
