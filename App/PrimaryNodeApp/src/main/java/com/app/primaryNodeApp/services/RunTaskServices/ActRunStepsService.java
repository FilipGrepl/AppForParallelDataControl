/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.RunTaskServices;

import com.app.primaryNodeApp.services.dataClasses.ActRunStepData;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.StepStatus.StepStatusEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The service for actual running steps of task.
 * @author filip
 */
public class ActRunStepsService {

    /**
     * Method, that return data about actual running steps.
     * @param runJobID ID of runJob for which the data about actual runing steps to be returned.
     * @return List of all running steps data for specific run job.
     */
    public static List<ActRunStepData> getActRunStepsData(Long runJobID) {
        List<ActRunStepData> actRunStepsData = new ArrayList<>();
        RunJob runJob = new RunJobDao().getByIdWithCollections(runJobID);
        Job job = new JobDao().getByIdWithCollections(runJob.getJob().getId());

        for (Step step : job.getJobSteps()) {
            List<StepRunData> stepsRunData = ActRunStepsService.getSortedStepRunData(runJob, step);

            Integer numberOfFinishedServers = 0;
            Integer numberOfRunningServers = 0;
            //boolean isSomeStepRun = false;
            for (StepRunData stepRunData : stepsRunData) {
                /*if (stepRunData.getStepStatus() != StepStatusEnum.WAITING || stepRunData.getProcessedFsNodes() != 0) {
                    isSomeStepRun = true;
                }*/
                if (stepRunData.getStepStatus() != null) {
                    switch (stepRunData.getStepStatus()) {
                        case FINISHED:
                            numberOfFinishedServers++;
                            break;
                        case RUNNING:
                        case RUNNING_ERROR:
                            numberOfRunningServers++;
                            break;
                        default:
                            break;
                    }
                }
            }
            // check if the step is not finished on all servers
            //if (isSomeStepRun) {
                ActRunStepData actRunStepData = new ActRunStepData();
                actRunStepData.setStepName(step.getStepName());
                actRunStepData.setStepOrder(step.getStepOrder());
                actRunStepData.setNumberOfSteps(job.getJobSteps().size());

                actRunStepData.setNumberOfFinishedServers(numberOfFinishedServers);
                actRunStepData.setNumberOfRunningServers(numberOfRunningServers);
                actRunStepData.setNumberOfServers(job.getJobNodes().size());

                actRunStepData.setStepStatus(CommonRunService.getStepRunDataStatus(stepsRunData));
                actRunStepData.setStatusClass(CommonRunService.getStepStatusClass(actRunStepData.getStepStatus()));

                actRunStepsData.add(actRunStepData);
            //}
        }

        return actRunStepsData;
    }

    /**
     * Method, that returns sorted running data about specific step or task.
     * @param runJob RunJob for which the running data to be returned.
     * @param step Step for which the running data to be returned.
     * @return Sorted running data about specific step or task.
     */
    private static List<StepRunData> getSortedStepRunData(RunJob runJob, Step step) {
        Predicate<StepRunData> byStepID = stepRunData -> stepRunData.getStep().getId().equals(step.getId());
        List<StepRunData> filteredStepRunData = runJob.getStepRunData().stream().filter(byStepID).collect(Collectors.toList());
        filteredStepRunData.sort((StepRunData stepRunData1, StepRunData stepRunData2) -> stepRunData1.getStep().getStepOrder() - stepRunData2.getStep().getStepOrder());        
        return filteredStepRunData;
    }
}
