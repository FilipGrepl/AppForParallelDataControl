/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.SortBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import com.app.primaryNodeApp.services.dataClasses.interfaces.SortInterface;
import java.io.Serializable;

/**
 * The backing bean for sorting task overview data.
 * @author filip
 */
@ManagedBean
@ViewScoped
public class SortBean implements Serializable {
    
    /** STATIC PROPERTY **/
    
    public static enum SortOptionsEnum {
        BY_STARTED_AT("Data spuštění"),
        BY_EXEC_TIME("Doby běhu"),
        BY_SERVER_LOAD("Počtu vytížených serverů");


        private final String message;

        SortOptionsEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    
    /** OBJECT PROPERTIES **/
    
     // sorting
    private SortOptionsEnum selectValue;    // orderBy    
    private List<SelectItem> selectValues;  // sortValues
    
    /** OBJECT METHODS **/
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    public void postConstruct() {
        this.selectValues = new ArrayList<>();
        this.selectValue = SortOptionsEnum.BY_STARTED_AT;
        for (SortOptionsEnum option : SortOptionsEnum.values()) {
            this.selectValues.add(new SelectItem(option.toString(), option.getMessage()));
        }
    }
    
    /**
     * Method, which sorts data based on filter settings.
     * @param tasksDataToDisplay Data to be sorted.
     */
    public void sortData(List<? extends SortInterface> tasksDataToDisplay) {
        switch(selectValue) {
            case BY_STARTED_AT:
                Collections.sort(tasksDataToDisplay, (SortInterface taskData1, SortInterface taskData2) -> {
                    if (taskData1.getStartedAt() == null) {
                        return taskData2.getStartedAt() == null ? 0 : -1;
                    }
                    if (taskData2.getStartedAt() == null) {
                        return 1;
                    }
                    return taskData2.getStartedAt().compareTo(taskData1.getStartedAt());
                });
                break;
            case BY_EXEC_TIME:
                Collections.sort(tasksDataToDisplay, (SortInterface taskData1, SortInterface taskData2) -> {
                    if (taskData1.getRunningTime() == null) {
                        return taskData2.getRunningTime() == null ? 0 : -1;
                    }
                    if (taskData2.getRunningTime() == null) {
                        return 1;
                    }
                    return taskData2.getRunningTime().compareTo(taskData1.getRunningTime());
                });
                break;
            case BY_SERVER_LOAD:
                Collections.sort(tasksDataToDisplay, (SortInterface taskData1, SortInterface taskData2) -> {
                    if (taskData1.getNumberOfServersToFilterAndSort() == null) {
                        return taskData2.getRunningTime() == null ? 0 : -1;
                    }
                    if (taskData2.getNumberOfServersToFilterAndSort() == null) {
                        return 1;
                    }                    
                    return taskData2.getNumberOfServersToFilterAndSort().compareTo(taskData1.getNumberOfServersToFilterAndSort());
                });
                break;
        }
    }

    /**
     * Getter of sort option value.
     * @return Sort option value.
     */
    public SortOptionsEnum getSelectValue() {
        return selectValue;
    }

    /**
     * Setter of sort option value.
     * @param sortOption Nea value of sort option.
     */
    public void setSelectValue(SortOptionsEnum sortOption) {
        this.selectValue = sortOption;
    }

    /**
     * Getter of all sort options.
     * @return List of select items of all sort options.
     */
    public List<SelectItem> getSelectValues() {
        return selectValues;
    }   
}
