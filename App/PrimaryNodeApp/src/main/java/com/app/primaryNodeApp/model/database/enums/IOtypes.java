/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * The enumerator which represents input and output types of each step of task.
 * @author Filip
 */
public class IOtypes {

    public static enum IOtypesEnum {
        FILE("Soubor"),
        FOLDER("Složka"),
        FOLDER_OF_FILES("Složka souborů"),
        FOLDER_OF_FOLDERS("Složka složek");

        private final String message;

        IOtypesEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Method, that returns possible output types to the corresponding input type.
     * @param inputType The input type for which the corresponding output types to be returned.
     * @return The list of output types which correspondes with entered input type.
     */
    public static List<IOtypesEnum> getOutputTypes(IOtypesEnum inputType) {
            List<IOtypesEnum> outputTypes = new ArrayList<>();
            switch(inputType) {
                case FILE:
                case FOLDER:
                    outputTypes.add(IOtypesEnum.FILE);
                    outputTypes.add(IOtypesEnum.FOLDER);
                    break;
                case FOLDER_OF_FILES:
                case FOLDER_OF_FOLDERS:
                    outputTypes.add(IOtypesEnum.FOLDER);
                    outputTypes.add(IOtypesEnum.FOLDER_OF_FILES);
                    outputTypes.add(IOtypesEnum.FOLDER_OF_FOLDERS);
                    break;
            }
            return outputTypes;
        }
}
