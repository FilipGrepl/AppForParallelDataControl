/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.RunTaskServices;

import com.app.primaryNodeApp.services.dataClasses.interfaces.TaskDataInterface;
import com.app.primaryNodeApp.services.dataClasses.RunTaskData;
import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import com.app.primaryNodeApp.model.database.entity.Step;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import com.app.primaryNodeApp.model.database.enums.StepStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The service, that returns data about running task.
 * @author filip
 */
public class RunTaskService implements TaskDataInterface<RunTaskData> {

    /**
     * Method, that returns data about all running tasks in the system.
     * @return Data about all running jobs in the system.
     */
    public List<RunTaskData> getTasksData() {
        List<RunTaskData> runTaskData = new ArrayList<>();
        // get all running jobs
        List<RunJob> runJobs = new RunJobDao().getRunJobs();

        if (runJobs != null) {
            List<Job> allJobs = new JobDao().getAllWithCollections();

            runJobs.forEach(runJob -> {
                runTaskData.add(createRunTaskData(runJob, allJobs));
            });
        }
        return runTaskData;
    }

    /**
     * Method, that returns data about running for specific task.
     * @param runJobID ID of runJob for which the running data to be returned.
     * @return Data about running for specific task.
     */
    public RunTaskData getRunTaskData(Long runJobID) {
        RunJob runJob = new RunJobDao().getByIdWithCollections(runJobID);
        List<Job> allJobs = new ArrayList<>();
        allJobs.add(new JobDao().getByIdWithCollections(runJob.getJob().getId()));
        return createRunTaskData(runJob, allJobs);
    }

    /**
     * This method is for optimalization. When this method is called multiple times, the allJobs is obtained from database only once.
     * Method, that returns data about running for specific task.
     * @param runJob The runJob for which the running data to be returned.
     * @param allJobs The list of all jobs in system.
     * @return Data about running for specific task.
     */
    private RunTaskData createRunTaskData(RunJob runJob, List<Job> allJobs) {

        Predicate<Job> byJobID = job -> job.getId().equals(runJob.getJob().getId());
        Job job = allJobs.stream().filter(byJobID).findAny().orElse(null);
        
        Long runningTime = System.currentTimeMillis() - runJob.getStartedAt().getTime();

        RunTaskData runTaskData = new RunTaskData();
        runTaskData.setTaskName(job.getJobName());
        runTaskData.setRunJobID(runJob.getId());
        runTaskData.setStartedAt(runJob.getStartedAt());
        runTaskData.setRunningTime(runningTime < 0 ? 0 : runningTime);
        runTaskData.setNumberOfServers(job.getJobNodes().size());
        runTaskData.setAllSteps(job.getJobSteps().size());

        setNumberOfRunningServers(runJob, job, runTaskData);
        setFinishedStepsAndLastSuccStep(runJob, job, runTaskData);
        runTaskData.setTaskStatus(CommonRunService.getStepRunDataStatus(runJob.getStepRunData()));

        runTaskData.setStatusClass(CommonRunService.getStepStatusClass(runTaskData.getTaskStatus()));
        return runTaskData;
    }

    /**
     * Method, that sets number of finished steps and name of last success step in data about running task.
     * @param runJob The runJob for which the parameters to be set.
     * @param job The job for which the parameters to be set.
     * @param runTaskData The object for which the number of finished steps and name of last success step to be set.
     */
    private void setFinishedStepsAndLastSuccStep(RunJob runJob, Job job, RunTaskData runTaskData) {
        
        // sort step by its order
        job.getJobSteps().sort((Step step1, Step step2) -> step1.getStepOrder() - step2.getStepOrder());
        
        job.getJobSteps().forEach(step -> {
            Predicate<StepRunData> byStepID = stepRunData -> stepRunData.getStep().getId().equals(step.getId());
            if (runJob.getStepRunData().stream().filter(byStepID).anyMatch(stepRunData -> stepRunData.getStepStatus() != StepStatus.StepStatusEnum.FINISHED)) {
                return;
            }
            runTaskData.setFinishedSteps(runTaskData.getFinishedSteps() + 1);
            runTaskData.setLastSuccStep(step.getStepName());
        });
    }

    /**
     * Method, that sets number of running secondary nodes in data about running task.
     * @param runJob The runJob for which the parameters to be set.
     * @param job The job for which the parameters to be set.
     * @param runTaskData The object for which the number of running secondary nodes to be set.
     */
    private void setNumberOfRunningServers(RunJob runJob, Job job, RunTaskData runTaskData) {
        // find number of running servers

        job.getJobNodes().forEach(node -> {
            Predicate<StepRunData> byNodeID = stepRunData -> stepRunData.getNode().getId().equals(node.getId());
            runJob.getStepRunData().stream().filter(byNodeID).forEach(stepRunData -> {
                if (stepRunData.getStepStatus() == StepStatus.StepStatusEnum.RUNNING || stepRunData.getStepStatus() == StepStatus.StepStatusEnum.RUNNING_ERROR) {
                    runTaskData.setNumberOfRunningServers(runTaskData.getNumberOfRunningServers() + 1);
                }
            });
        });
    }

}
