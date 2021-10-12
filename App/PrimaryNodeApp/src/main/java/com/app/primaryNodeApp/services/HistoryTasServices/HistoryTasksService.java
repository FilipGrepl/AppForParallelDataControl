/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.HistoryTasServices;

import com.app.primaryNodeApp.services.dataClasses.interfaces.TaskDataInterface;
import com.app.primaryNodeApp.services.dataClasses.HistoryTaskData;
import com.app.primaryNodeApp.services.RunTaskServices.CommonRunService;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The service for obtaining historical data about tasks.
 * @author filip
 */
public class HistoryTasksService implements TaskDataInterface<HistoryTaskData> {
    
    /**
     * Method, that returns historical data for all finished tasks in system.
     * @return List of all historical data about all task in system.
     */
    public List<HistoryTaskData> getTasksData() {
        List<HistoryTaskData> historyTaskData = new ArrayList<>();

        // get all runJobs
        List<RunJob> finishedRunJobs = new RunJobDao().getFinishedJobs();

        if (finishedRunJobs != null) {
            List<Job> allJobs = new JobDao().getAllWithCollections();

            finishedRunJobs.forEach(runJob -> {
                historyTaskData.add(createHistoryTaskData(runJob, allJobs));
            });
        }
        return historyTaskData;
    }
    
    /**
     * Method, that return historical data for concrete task.
     * @param runJobID Id of run job for which the historical data to be returned.
     * @return Historical data for concrete task.
     */
    public HistoryTaskData getRunTaskData(Long runJobID) {
        RunJob runJob = new RunJobDao().getByIdWithCollections(runJobID);
        List<Job> allJobs = new ArrayList<>();
        allJobs.add(new JobDao().getByIdWithCollections(runJob.getJob().getId()));
        return createHistoryTaskData(runJob, allJobs);
    }

    /**
     * This method is for optimalization. When this method is called multiple times, the allJobs is obtained from database only once.
     * It returns historical data for specific runJob.
     * @param runJob RunJob for which the historical data to be obtained.
     * @param allJobs List of all jobs in appliation.
     * @return Return historical task data.
     */
    private HistoryTaskData createHistoryTaskData(RunJob runJob, List<Job> allJobs) {

        Predicate<Job> byJobID = job -> job.getId().equals(runJob.getJob().getId());
        
        // filtering is faster than querying for each job separately
        Job job = allJobs.stream().filter(byJobID).findAny().orElse(null);

        HistoryTaskData historyTaskData = new HistoryTaskData();

        historyTaskData.setTaskName(job.getJobName());
        historyTaskData.setRunJobID(runJob.getId());
        historyTaskData.setStartedAt(runJob.getStartedAt());
        historyTaskData.setRunningTime(runJob.getFinishedAt().getTime() - runJob.getStartedAt().getTime());
        historyTaskData.setNumberOfServers(job.getJobNodes().size());
        historyTaskData.setTaskStatus(CommonRunService.getStepRunDataStatus(runJob.getStepRunData()));
        historyTaskData.setFinishedAt(runJob.getFinishedAt());
        
        runJob.getStepRunData().stream().map(StepRunData::getExecTime).reduce(Long::sum).ifPresent(execTime -> historyTaskData.setExecTime(execTime));
        runJob.getStepRunData().stream().map(StepRunData::getWaitingTimeToDisplay).reduce(Long::sum).ifPresent(waitingTime -> historyTaskData.setWaitingTime(waitingTime));
        
        return historyTaskData;
    }
}
