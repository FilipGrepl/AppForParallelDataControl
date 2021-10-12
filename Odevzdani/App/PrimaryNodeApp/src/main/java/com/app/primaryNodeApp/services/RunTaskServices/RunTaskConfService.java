/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.RunTaskServices;

import com.app.primaryNodeApp.services.dataClasses.StepConfData;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import java.util.ArrayList;
import java.util.List;

/**
 * The service for observing configartion data about all steps of task.
 * @author filip
 */
public class RunTaskConfService {
    
    /**
     * Method, that returns configuration data about all steps of task.
     * @param jobID Id of job for which the configuration data about all steps to be returned.
     * @return Configuration data about all steps of task.
     */
    public static List<StepConfData> getStepConfData(Long jobID) {
        List<StepConfData> stepsConfData = new ArrayList<>();
        new JobDao().getByIdWithCollections(jobID).getJobSteps().forEach(step -> {
            StepConfData stepConfData = new StepConfData();
            stepConfData.setStep(step);
            stepsConfData.add(stepConfData);
        });
        return stepsConfData;
    }
}
