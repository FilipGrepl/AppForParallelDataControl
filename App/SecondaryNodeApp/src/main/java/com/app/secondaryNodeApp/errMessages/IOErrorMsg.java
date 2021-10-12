
package com.app.secondaryNodeApp.errMessages;
        
import com.app.commons.eventLogger.EventLogger;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import java.text.MessageFormat;

/**
 * Class for creating input/output error messages, which are sent to Primary node.
 * @author Filip
 */
public class IOErrorMsg {
    
    /** STATIC PROPERY **/
    
    private static enum IOErrors {
        NONEXIST_PATH("Input path {0} does not exist"),
        UNEXP_INPUT_TYPE("Expected input type is {0}, but real input type is {1}."),
        EMPTY_INPUT("Input {0} with path {1} is empty"),
        NONEMPTY_OUTPUT("Output {0} with path {1} is nonempty."),
        DIFFERENT_INPUTS("Input folder {0} does not contain any {1}s or all {1}s have been processed."),
        CANNOT_CREATE_OUT_FOLDER("Cannot create output folder {0}."),
        NONEXIST_PATH_TO_FILE("Path to {0} file does not exist."),
        CANNOT_READ_FILE("Cannot read the content of {0} file. Error is: \n\n{0}\n\n{1}."),
        CANNOT_READ_STDERR("Cannot read content of stderr. Error is: \n\n{0}\n\n{1}."),
        CANNOT_USE_INPUT_REGEX("Error while using input regex. Error is: \n\n{0}\n\n{1}."),
        PROGRAM_RUNNING_ERROR("Error while running program {0}. Error is: \n\n{1}\n\n{2}"),
        INTERRUPT_PROGRAM_ERROR("Interrput error occured while running program {0}. Error is: \n\n{1}\n\n{2}"),
        STARTING_IO_ERROR("IO error occured during starting executing of program {0}. Error is: \n\n{1}\n\n{2}");


        private final String message;

        IOErrors(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }
    }

    /** STATIC METHODS **/
    
    public static String getNonExistPathMsg(String inputPath) {
        return MessageFormat.format(IOErrors.NONEXIST_PATH.getMessage(), inputPath);
    }

    public static String getUnexpInputTypeMsg(IOtypesEnum expInputType, IOtypesEnum realInputType) {
        return MessageFormat.format(IOErrors.UNEXP_INPUT_TYPE.getMessage(), expInputType, realInputType);
    }

    public static String getEmptyInputMsg(IOtypesEnum ioType, String inputPath) {
        return MessageFormat.format(IOErrors.EMPTY_INPUT.getMessage(), ioType, inputPath);
    }
    
    public static String getNonemptyOutputMsg(IOtypesEnum ioType, String outputPath) {
        return MessageFormat.format(IOErrors.NONEMPTY_OUTPUT.getMessage(), ioType, outputPath);
    }

    public static String getDiffInputTypesMsg(String inputPath, IOtypesEnum expectedInputTypes) {
        return MessageFormat.format(IOErrors.DIFFERENT_INPUTS.getMessage(), inputPath, expectedInputTypes);
    }

    public static String getCannotCreateOutFolderMsg(String outputFolder) {
        return MessageFormat.format(IOErrors.CANNOT_CREATE_OUT_FOLDER.getMessage(), outputFolder);
    }

    public static String getCannotCreateOutFolderMsg(String outputFolder, final Throwable error) {
        return MessageFormat.format(IOErrors.CANNOT_CREATE_OUT_FOLDER.getMessage()+" Error is: \n\n{1}\n\n{2}.", outputFolder, error.toString(), EventLogger.getErrorStackTrace(error));
    }

    public static String getNonExistPathToFileMsg(String filename) {
        return MessageFormat.format(IOErrors.NONEXIST_PATH_TO_FILE.getMessage(), filename);
    }

    public static String getCannotReadFileMsg(String filename, final Throwable ex) {
        return MessageFormat.format(IOErrors.CANNOT_READ_FILE.getMessage(), filename, ex.toString(), EventLogger.getErrorStackTrace(ex));
    }
    
    public static String getCannotReadStderrMsg(final Throwable ex) {
        return MessageFormat.format(IOErrors.CANNOT_READ_STDERR.getMessage(), ex.toString(), EventLogger.getErrorStackTrace(ex));
    }
    
    public static String getCannotUseInputRegexMsg(final Throwable ex) {
        return MessageFormat.format(IOErrors.CANNOT_USE_INPUT_REGEX.getMessage(), ex.toString(), EventLogger.getErrorStackTrace(ex));
    }
    
    public static String getCannotRunProgramMsg(final String commandPrefix, final Throwable ex) {
        return MessageFormat.format(IOErrors.PROGRAM_RUNNING_ERROR.getMessage(), commandPrefix, ex.toString(), EventLogger.getErrorStackTrace(ex));
    }
    
    public static String getInterruptProgramMsg(final String commandPrefix, final Throwable ex) {
        return MessageFormat.format(IOErrors.INTERRUPT_PROGRAM_ERROR.getMessage(), commandPrefix, ex.toString(), EventLogger.getErrorStackTrace(ex));
    }
    
    public static String getIOErrorStartProgramMsg(final String commandPrefix, final Throwable ex) {
        return MessageFormat.format(IOErrors.STARTING_IO_ERROR.getMessage(), commandPrefix, ex.toString(), EventLogger.getErrorStackTrace(ex));
    }
}
