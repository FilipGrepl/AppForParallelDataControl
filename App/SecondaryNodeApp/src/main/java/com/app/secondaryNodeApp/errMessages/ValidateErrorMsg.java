
package com.app.secondaryNodeApp.errMessages;

import static com.app.primaryNodeApp.model.database.entity.Step.BYTES_IN_KB;
import static com.app.primaryNodeApp.model.database.entity.Step.BYTES_IN_MB;
import com.app.primaryNodeApp.model.database.enums.IOtypes.IOtypesEnum;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

/**
 * Class for creating validate error messages, which are sent to Primary node. 
 * @author Filip
 */
public class ValidateErrorMsg {
    
    /** STATIC PROPERTY **/
    
    private static enum ValidateErrors {
        UNEXP_TYPE_OUTPUT_FILE("The expected output type is file, but the output path {0} to input {1} is folder."),
        UNEXP_TYPE_OUTPUT_FOLDER("The expected output type is folder, but the output path {0} to input {1} is file."),
        EMPTY_OUTPUT_FILE("The output file {0} to input {1} is empty."),
        EMPTY_OUTPUT_FOLDER("The output folder {0} to input {1} is empty."),
        NO_EMPTY_STDERR("Stderr is not empty."),
        TOO_BIG_SIZE_OF_LOG_FILE("Size of log file {0} is too big. Max expected size is {1}, but real size is {2}."),
        NON_EXIST_LOG_FILE("Size of log file cannot be checked, because log file {0} does not exist."),
        NON_EQUAL_IN_TO_OUT_FILES("The number of input files is not equal to number of output files. Number of input files is {0}, but number of output files is {1}."),
        UNEXP_IN_TO_OUT_EQ_VALIDATION("This validation is can be used only if output and input is the "+IOtypesEnum.FOLDER+"."),
        UNEXP_EXIST_IN_TO_OUT_VALIDATION("This validation is can be used only if output and input type is the "+IOtypesEnum.FOLDER_OF_FILES+" or "+IOtypesEnum.FOLDER_OF_FOLDERS+"."),
        NON_EXIST_IN_TO_OUT_FILE("The output file {1} to input file {0} does not exist."),
        NON_EXIST_IN_TO_OUT_FOLDER("The output folder {1} to input folder {0} does not exist."),
        TIMEOUT_EXPIRED("Maximum running time {0} seconds exceeded."),
        FNISHED_CODE_MSG("Program finished with exit code: {0}");

        private final String message;

        ValidateErrors(String message) {
            this.message = message;
        }

        String getMessage() {
            return message;
        }
    }
    
    /** STATIC METHODS **/

    public static String getUnexpTypeOutFileErr(String inputPath, String outputPath) {
        return MessageFormat.format(ValidateErrors.UNEXP_TYPE_OUTPUT_FILE.getMessage(), outputPath, inputPath);
    }

    public static String getUnexpTypeFolderErr(String inputPath, String outputPath) {
        return MessageFormat.format(ValidateErrors.UNEXP_TYPE_OUTPUT_FOLDER.getMessage(), outputPath, inputPath);
    }
    
    public static String getEmptyOutFileErr(String inputPath, String outputPath) {
        return MessageFormat.format(ValidateErrors.EMPTY_OUTPUT_FILE.getMessage(), outputPath, inputPath);
    }
    
    public static String getEmptyOutFolderErr(String inputPath, String outputPath) {
        return MessageFormat.format(ValidateErrors.EMPTY_OUTPUT_FOLDER.getMessage(), outputPath, inputPath);
    }

    public static String getNoEmptyStderrErr() {
        return ValidateErrors.NO_EMPTY_STDERR.getMessage();
    }

    public static String getTooBigSizeOfLogFileErr(String pathToLogFile, long maxSize, long realSize) {
        String maxSizeStr;
        String realSizeStr;
        
        if (maxSize % BYTES_IN_MB == 0) {
            maxSizeStr = maxSize/BYTES_IN_MB + " MB";
        } else if (maxSize % BYTES_IN_KB == 0) {
            maxSizeStr = maxSize/BYTES_IN_KB + " kB";
        } else {
            maxSizeStr = maxSize + " B";
        }
        
        if (realSize % BYTES_IN_MB == 0) {
            realSizeStr = realSize/BYTES_IN_MB + " MB";
        } else if (realSize % BYTES_IN_KB == 0) {
            realSizeStr = realSize/BYTES_IN_KB + " kB";
        } else {
            realSizeStr = realSize + " B";
        }
                
        return MessageFormat.format(ValidateErrors.TOO_BIG_SIZE_OF_LOG_FILE.getMessage(), pathToLogFile, maxSizeStr, realSizeStr);
    }

    public static String getNonExistLogFileErr(String pathToLogFile) {
        return MessageFormat.format(ValidateErrors.NON_EXIST_LOG_FILE.getMessage(), pathToLogFile);
    }

    public static String getNonEqualInToOutFilesErr(long numberOfInputFiles, long numberOfOutputFiles) {
        return MessageFormat.format(ValidateErrors.NON_EQUAL_IN_TO_OUT_FILES.getMessage(), numberOfInputFiles, numberOfOutputFiles);
    }

    public static String getUnexpInToOutEqValidationErr() {
        return ValidateErrors.UNEXP_IN_TO_OUT_EQ_VALIDATION.getMessage();
    }

    public static String getUnexpExistInToOutValidationErr() {
        return ValidateErrors.UNEXP_EXIST_IN_TO_OUT_VALIDATION.getMessage();
    }

    public static String getNonExistOutFileErr(String pathToInputFile, String pathToOutputFile) {
        return MessageFormat.format(ValidateErrors.NON_EXIST_IN_TO_OUT_FILE.getMessage(), pathToInputFile, pathToOutputFile);
    }

    public static String getNonExistOutFolderErr(String pathToInputFolder, String pathToOutputFolder) {
        return MessageFormat.format(ValidateErrors.NON_EXIST_IN_TO_OUT_FOLDER.getMessage(), pathToInputFolder, pathToOutputFolder);
    }

    public static String getTimeoutExpiredMsg(long timeout) {
        return MessageFormat.format(ValidateErrors.TIMEOUT_EXPIRED.getMessage(), TimeUnit.MILLISECONDS.toSeconds(timeout));
    }
}
