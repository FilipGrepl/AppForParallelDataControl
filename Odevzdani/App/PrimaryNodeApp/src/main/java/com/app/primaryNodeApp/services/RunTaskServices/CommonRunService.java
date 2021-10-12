/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.RunTaskServices;

import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.services.dataClasses.enums.StatusClass.StatusClassEnum;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.NodeStatus.NodeStatusEnum;
import com.app.primaryNodeApp.model.database.enums.StepStatus;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.util.List;

/**
 * The service for observing general information from data of running task.
 * @author filip
 */
public class CommonRunService {

    /*
        priority:
            ERROR
            RUNNING_ERROR
            PAUSED
            WAITING
            RUNNING
            FINISHED        
     */
    /**
     * Method, that returns accumulated status based on all running data about all steps.
     * @param stepsRunData The running data of all steps of task.
     * @return Accumulated status base on all running data about all steps.
     */
    public static StepStatusEnum getStepRunDataStatus(List<StepRunData> stepsRunData) {
        StepStatusEnum taskStatusEnum = null;

        for (StepRunData stepRunData : stepsRunData) {
            StepStatus.StepStatusEnum actTaskStatus = stepRunData.getStepStatus();
            if (taskStatusEnum == null) {
                taskStatusEnum = actTaskStatus;
            } else {
                switch (taskStatusEnum) {
                    case ERROR:
                        break;
                    case RUNNING_ERROR:
                        if (actTaskStatus == StepStatus.StepStatusEnum.ERROR) {
                            taskStatusEnum = actTaskStatus;
                        }
                        break;
                    case PAUSED:
                        if (actTaskStatus == StepStatus.StepStatusEnum.ERROR || actTaskStatus == StepStatus.StepStatusEnum.RUNNING_ERROR) {
                            taskStatusEnum = actTaskStatus;
                        }
                        break;
                    case RUNNING:
                        if (actTaskStatus == StepStatus.StepStatusEnum.ERROR
                                || actTaskStatus == StepStatus.StepStatusEnum.RUNNING_ERROR
                                || actTaskStatus == StepStatus.StepStatusEnum.PAUSED) {
                            taskStatusEnum = actTaskStatus;
                        }
                        break;
                    case WAITING:
                        if (actTaskStatus != StepStatus.StepStatusEnum.FINISHED) {
                            taskStatusEnum = actTaskStatus;
                        }
                        break;
                    case FINISHED:
                        taskStatusEnum = actTaskStatus;
                        break;
                }
            }
        }
        return taskStatusEnum;

    }

    /**
     * Method, that return class of task status for status color.
     * @param stepStatus Status of task or step of task.
     * @return Color of task status.
     */
    public static String getStepStatusClass(StepStatusEnum stepStatus) {
        switch (stepStatus) {
            case RUNNING:
                return StatusClassEnum.GREEN_COLOR.getMessage();
            case WAITING:
                return StatusClassEnum.ORANGE_COLOR.getMessage();
            case PAUSED:
                return StatusClassEnum.BROWN_COLOR.getMessage();
            case FINISHED:
                return StatusClassEnum.BLUE_COLOR.getMessage();
            default:
                return StatusClassEnum.RED_COLOR.getMessage();
        }
    }
    
    /**
     * Method, that return class of node status for node color.
     * @param nodeStatusEnum Status of node connection.
     * @return Color of node status.
     */
    public static String getNodeStatusClass(NodeStatusEnum nodeStatusEnum) {
        switch(nodeStatusEnum) {
            case OCCUPIED:
                return StatusClassEnum.ORANGE_COLOR.getMessage();
            case FREE:
                return StatusClassEnum.GREEN_COLOR.getMessage();
            case DISCONNECTED:
            default:
                return StatusClassEnum.RED_COLOR.getMessage();
        }
    }

    /**
     * Method, that returns string representation of entered time in milliseconds.
     * @param time Time in milliseconds.
     * @return Time in string representation.
     */
    public static String getTimeStr(Long time) {
        long runtime = time;
        long days = runtime / RunTaskData.DAY;
        runtime -= days * RunTaskData.DAY;
        long hours = runtime / RunTaskData.HOURS;
        runtime -= hours * RunTaskData.HOURS;
        long minutes = runtime / RunTaskData.MINUTES;
        return days + " dní " + hours + " hodin " + minutes + " minut";
    }

    /**
     * Method, that returns ration string for entered values.
     * @param value1 Part of the whole.
     * @param value2 Number of parts in whole.
     * @return Ration string of entered values.
     */
    public static String getRatioStr(String value1, String value2) {
        return value1 + " / " + value2;
    }

    /**
     * Method, that returns string representation of entered number in percent.
     * @param value Number in string format
     * @return String representation of entered value in percent.
     */
    public static String getPercentageStr(String value) {
        return value + " %";
    }
}
