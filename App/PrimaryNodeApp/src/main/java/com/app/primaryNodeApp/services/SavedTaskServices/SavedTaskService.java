/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.SavedTaskServices;

import com.app.primaryNodeApp.services.dataClasses.interfaces.TaskDataInterface;
import com.app.primaryNodeApp.services.dataClasses.SavedTaskData;
import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The service for observing data about saved task.
 * @author filip
 */
public class SavedTaskService implements TaskDataInterface<SavedTaskData> {

    /**
     * Method, that returns data about all saved tasks in system.
     * @return Data about all saved tasks.
     */
    @Override
    public List<SavedTaskData> getTasksData() {
        List<SavedTaskData> savedTaskData = new ArrayList<>();
        JobDao jobDao = new JobDao();

        for (Job job : jobDao.getAllWithCollections()) {
            savedTaskData.add(createSavedTaskData(job));
        }
        return savedTaskData;
    }

    /**
     * Method, that returns data about specific saved task.
     * @param jobID ID of job for which the saved data to be returned.
     * @return Data about specific saved task.
     */
    public SavedTaskData getSavedTaskData(Long jobID) {
        Job job = new JobDao().getByIdWithCollections(jobID);
        return createSavedTaskData(job);
    }

    /**
     * This method is for optimalization. When this method is called multiple times, the job is obtained from database only once.
     * Method, that returns data about specific saved task.
     * @param job Job for which the saved data to be returned.
     * @return Data about specific saved task.
     */
    private SavedTaskData createSavedTaskData(Job job) {
        SavedTaskData savedTaskData = new SavedTaskData();
        savedTaskData.setJobID(job.getId());
        savedTaskData.setTaskName(job.getJobName());
        savedTaskData.setNumberOfServers(job.getJobNodes().size());
        savedTaskData.setNumberOfSteps(job.getJobSteps().size());

        if (!job.getRunJobs().isEmpty()) {
            job.getRunJobs().sort((RunJob runJob_1, RunJob runJob_2) -> runJob_2.getStartedAt().compareTo(runJob_1.getStartedAt()));
            RunJob newestRunJob = job.getRunJobs().get(0);
            Optional<RunJob> firstNonFinishedJob = job.getRunJobs().stream().filter(runJob -> runJob.getFinishedAt() == null).findFirst();
            RunJob runJobToDisplay;
            if (firstNonFinishedJob.isPresent()) {
                runJobToDisplay = new RunJobDao().getByIdWithCollections(firstNonFinishedJob.get().getId());                
            } else {
                runJobToDisplay = new RunJobDao().getByIdWithCollections(newestRunJob.getId());
            }
            
            savedTaskData.setLastStartedAt(runJobToDisplay.getStartedAt());
            savedTaskData.setTaskStatus(CommonRunService.getStepRunDataStatus(runJobToDisplay.getStepRunData()));
            savedTaskData.setStatusClass(CommonRunService.getStepStatusClass(savedTaskData.getTaskStatus()));

            if (runJobToDisplay.getFinishedAt() == null) {
                savedTaskData.setRunningTime(System.currentTimeMillis() - runJobToDisplay.getStartedAt().getTime());
            } else {
                savedTaskData.setRunningTime(runJobToDisplay.getFinishedAt().getTime() - runJobToDisplay.getStartedAt().getTime());
            }
        }
        return savedTaskData;
    }
}
