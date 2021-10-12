/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses;

import com.app.primaryNodeApp.model.database.entity.Step;
import static com.app.primaryNodeApp.model.database.entity.Step.BYTES_IN_KB;
import static com.app.primaryNodeApp.model.database.entity.Step.BYTES_IN_MB;
import static com.app.primaryNodeApp.model.database.entity.Step.MILLIS_IN_DAY;
import static com.app.primaryNodeApp.model.database.entity.Step.MILLIS_IN_HOUR;
import static com.app.primaryNodeApp.model.database.entity.Step.MILLIS_IN_MINUTE;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The data of step configuration.
 * @author filip
 */
public class StepConfData implements Serializable {
    
    /** OBJECT PROPERTIES **/
    
    private Step step;
    
    /** OBJECT METHODS **/
    
    public StepConfData() {
        this.step = null;
    }
    
    public Step getStep() {
        return this.step;
    }
    
    public void setStep(Step step) {
        this.step = step;
    }
    
    /**
     * Method, which returns timeout as string with time unit.
     * @return Timeout as string with time unit.
     */
    public String getTimeoutStr() {
        long timeoutDays = this.step.getTimeout() / MILLIS_IN_DAY;
        long timeoutHours = this.step.getTimeout() / MILLIS_IN_HOUR;
        long timeoutMinutes = this.step.getTimeout() / MILLIS_IN_MINUTE;
        if (this.step.getTimeout() % MILLIS_IN_DAY == 0) {
            if (timeoutDays == 1) {
                return timeoutDays + " den";
            } else if (timeoutDays < 5) {
                return timeoutDays + " dny";
            } else {
                return timeoutDays + " dnů";
            }
        } else if (this.step.getTimeout() % MILLIS_IN_HOUR == 0) {
            if (timeoutHours == 1) {
                return timeoutHours + " hodina";
            } else if (timeoutHours < 5) {
                return timeoutHours + " hodiny";
            } else {
                return timeoutHours + " hodin";
            }
        } else {
            if (timeoutMinutes == 1) {
                return timeoutMinutes + " minuta";
            } else if (timeoutMinutes < 5) {
                return timeoutMinutes + " minuty";
            } else {
                return timeoutMinutes + " minut";
            }
        }
    }

    /**
     * Method, which returns size of log file as string with size unit.
     * @return Timeout as string with time unit.
     */
    private String getLogFileSizeLessThenStr() {
        long sizeInMB = this.step.getLogSizeLessThan() / BYTES_IN_MB;
        long sizeInKB = this.step.getLogSizeLessThan() / BYTES_IN_KB;
        if (this.step.getLogSizeLessThan() % BYTES_IN_MB == 0) {
            return sizeInMB + " MB";
        } else if (this.step.getLogSizeLessThan() % BYTES_IN_KB == 0) {
            return sizeInKB + " kB";
        } else {
            return this.step.getLogSizeLessThan() + " B";
        }
    }

    /**
     * Method, which returns the Map of all validation options, that depends on input and output type.
     * @return The map of all validation options, that depends on input and output type.
     */
    public Map<String, Boolean> getCheckOptions() {
        Map<String, Boolean> checkOptions = new HashMap<>();
        
        checkOptions.put("Stderr je prázdný", this.step.isEmptyStderr());
        checkOptions.put("Velikost logu "
                + (this.step.isCheckLogFileSize() ? this.step.getPathToLog() : "-")
                + " menší než "
                + (this.step.isCheckLogFileSize() ? this.getLogFileSizeLessThenStr() : "-") + " .", this.step.isCheckLogFileSize());

        switch (this.step.getOutputType()) {
            case FILE:
                checkOptions.put("Existence neprázdného výstupního souboru.", this.step.isExistNoEmptyOutputFsNode());
                break;
            default:
                checkOptions.put("Existence neprázdné výstupní složky.", this.step.isExistNoEmptyOutputFsNode());
                break;
        }

        switch (this.step.getInputType()) {
            case FOLDER:
                switch (this.step.getOutputType()) {
                    case FOLDER:
                        checkOptions.put("Počet souborů ve vstupní složce je roven počtu souborů ve výstupní složce.", this.step.isEqualInToOutFiles());
                        checkOptions.put("Počet souborů ve vstupní složce je roven počtu souborů ve složce "
                                + (this.step.isEqualInToOutFilesSecondOutput() ? this.step.getPathToSecondOutputFolder() : "- ") + ".", this.step.isEqualInToOutFilesSecondOutput());
                        break;
                    default:
                        break;
                }
                break;
            case FOLDER_OF_FILES:
                switch (this.step.getOutputType()) {
                    case FOLDER_OF_FILES:
                        checkOptions.put("Pro každý vstupní soubor existuje odpovídající neprázdný výstupní soubor.", this.step.isExistNoEmptyOutputFsNode());
                        checkOptions.put("Počet souborů ve vstupní složce je roven počtu souborů ve složce "
                                + (this.step.isEqualInToOutFilesSecondOutput() ? this.step.getPathToSecondOutputFolder() : "- ") + ".", this.step.isEqualInToOutFilesSecondOutput());
                        break;
                    case FOLDER_OF_FOLDERS:
                        checkOptions.put("Pro každý vstupní soubor existuje odpovídající neprázdná výstupní složka.", this.step.isExistNoEmptyOutputFsNode());
                        checkOptions.put("Počet souborů ve vstupní složce je roven počtu složek ve složce "
                                + (this.step.isEqualInToOutFilesSecondOutput() ? this.step.getPathToSecondOutputFolder() : "- ") + ".", this.step.isEqualInToOutFilesSecondOutput());
                        break;
                    default:
                        break;
                }
                break;
            case FOLDER_OF_FOLDERS:
                switch (this.step.getOutputType()) {
                    case FOLDER_OF_FILES:
                        checkOptions.put("Ke každé vstupní složce existuje odpovídající neprázdný výstupní soubor.", this.step.isExistNoEmptyOutputFsNode());
                        checkOptions.put("Počet složek ve vstupní složce je roven počtu souborů ve složce "
                                + (this.step.isEqualInToOutFilesSecondOutput() ? this.step.getPathToSecondOutputFolder() : "- ") + ".", this.step.isEqualInToOutFilesSecondOutput());
                        break;
                    case FOLDER_OF_FOLDERS:
                        checkOptions.put("Ke každé vstupní složce existuje odpovídající neprázdná výstupní složka.", this.step.isExistNoEmptyOutputFsNode());
                        checkOptions.put("Počet složek ve vstupní složce je roven počtu souborů ve složce "
                                + (this.step.isEqualInToOutFilesSecondOutput() ? this.step.getPathToSecondOutputFolder() : "- ") + ".", this.step.isEqualInToOutFilesSecondOutput());
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
        return checkOptions;
    }

    /**
     * Method, which returns the Map of all error options.
     * @return The map of all error options.
     */
    public Map<String, Boolean> getErrorOptions() {
        Map<String, Boolean> errorOptions = new HashMap<>();

        errorOptions.put("Vypsat obsah standardního chybového výstupu.", this.step.isSaveStderr());
        errorOptions.put("Vypsat obsah logu " + (this.step.isSaveErrLog() ? this.step.getPathToErrLog() : "- ") + ".", this.step.isSaveErrLog());

        return errorOptions;
    }
}
