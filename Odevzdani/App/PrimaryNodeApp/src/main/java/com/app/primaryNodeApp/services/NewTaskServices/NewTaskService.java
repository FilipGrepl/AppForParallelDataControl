/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.NewTaskServices;

import com.app.primaryNodeApp.services.dataClasses.NewTaskStepValues;
import com.app.primaryNodeApp.services.dataClasses.NewTaskStepValues.LogSizeUnitEnum;
import com.app.primaryNodeApp.services.dataClasses.NewTaskStepValues.TimeoutUnitEnum;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Step;
import java.util.ArrayList;
import java.util.List;

/**
 * The service for obtaining data about new task.
 * @author filip
 */
public class NewTaskService {

    /**
     * List of all entered task steps. Each item corresponds with tab of tabview component.
     * @return List of init task steps. Last item is append button.
     */
    public static List<NewTaskStepValues> getInitList() {
        List<NewTaskStepValues> stepValues = new ArrayList<>();

        NewTaskStepValues firstStepValues = new NewTaskStepValues();
        NewTaskStepValues appendStepValues = new NewTaskStepValues();

        Step firstStep = new Step();
        Step appendStep = new Step();
        firstStep.setStepOrder(1);
        appendStep.setStepOrder(0);

        firstStepValues.setStep(firstStep);
        appendStepValues.setStep(appendStep);

        stepValues.add(firstStepValues);
        stepValues.add(appendStepValues);

        return stepValues;
    }

    /**
     * Method, that returns list of all displaying data about each step for specific task.
     * @param jobID The ID of job for which the displaying step data to be obtained.
     * @return  List of All displaying data about each sep for specific task.
     */
    public static List<NewTaskStepValues> getJobStepValuesList(Job job) {
        List<NewTaskStepValues> stepValues = new ArrayList<>();

        job.getJobSteps().forEach(step -> {
            NewTaskStepValues stepValue = new NewTaskStepValues();
            stepValue.setStep(step);
            
            if ((step.getTimeout() % Step.MILLIS_IN_DAY) == 0) {
                stepValue.setTimeout(step.getTimeout() / Step.MILLIS_IN_DAY);
                stepValue.setTimeoutUnit(TimeoutUnitEnum.DAYS);
            } else if ((step.getTimeout() % Step.MILLIS_IN_HOUR) == 0) {
                stepValue.setTimeout(step.getTimeout() / Step.MILLIS_IN_HOUR);
                stepValue.setTimeoutUnit(TimeoutUnitEnum.HOURS);
            } else {
                stepValue.setTimeout(step.getTimeout() / Step.MILLIS_IN_MINUTE);
                stepValue.setTimeoutUnit(TimeoutUnitEnum.MINUTES);
            }

            if (step.getLogSizeLessThan() % Step.BYTES_IN_MB == 0) {
                stepValue.setLogSize(step.getLogSizeLessThan() / Step.BYTES_IN_MB);
                stepValue.setLogSizeUnit(LogSizeUnitEnum.MB);
            } else if (step.getLogSizeLessThan() % Step.BYTES_IN_KB == 0) {
                stepValue.setLogSize(step.getLogSizeLessThan() / Step.BYTES_IN_KB);
                stepValue.setLogSizeUnit(LogSizeUnitEnum.KB);
            } else {
                stepValue.setLogSize(step.getLogSizeLessThan());
                stepValue.setLogSizeUnit(LogSizeUnitEnum.B);
            }
            stepValues.add(stepValue);
        });

        Step appendStep = new Step();
        appendStep.setStepOrder(0);
        NewTaskStepValues newTaskStepValues = new NewTaskStepValues();
        newTaskStepValues.setStep(appendStep);

        stepValues.add(newTaskStepValues);

        return stepValues;
    }

    /**
     * Method, that adds new step to list of all steps.
     * @param stepsValues List of steps, where new steps to be added.
     */
    public static void addNewStep(List<NewTaskStepValues> stepsValues) {
        stepsValues.get(stepsValues.size() - 1).getStep().setStepOrder(stepsValues.size());
        Step step = new Step();
        step.setStepOrder(0);
        NewTaskStepValues newStepValue = new NewTaskStepValues();
        newStepValue.setStep(step);
        stepsValues.add(newStepValue);
    }

    /**
     * Method, that returns data about task steps which is obtained from user input data about each step.
     * @param stepsValues Data entered by user.
     * @return List of all steps of task.
     */
    public static List<Step> getStepsToSave(List<NewTaskStepValues> stepsValues) {
        List<Step> stepsToSave = new ArrayList<>();        
        stepsValues.remove(stepsValues.size() - 1);
        stepsValues.forEach(stepValues -> {
            Step step = stepValues.getStep();

            switch (stepValues.getTimeoutUnit()) {
                case MINUTES:
                    step.setTimeout(stepValues.getTimeout() * Step.MILLIS_IN_MINUTE);
                    break;
                case HOURS:
                    step.setTimeout(stepValues.getTimeout() * Step.MILLIS_IN_HOUR);
                    break;
                case DAYS:
                default:
                    step.setTimeout(stepValues.getTimeout() * Step.MILLIS_IN_DAY);
                    break;
            }
            if (step.isCheckLogFileSize()) {
                switch (stepValues.getLogSizeUnit()) {
                    case B:
                        step.setLogSizeLessThan(stepValues.getLogSize());
                        break;
                    case KB:
                        step.setLogSizeLessThan(stepValues.getLogSize() * Step.BYTES_IN_KB);
                        break;
                    case MB:
                    default:
                        step.setLogSizeLessThan(stepValues.getLogSize() * Step.BYTES_IN_MB);
                        break;
                }
            }
            stepsToSave.add(step);
        });
        return stepsToSave;
    }
}
