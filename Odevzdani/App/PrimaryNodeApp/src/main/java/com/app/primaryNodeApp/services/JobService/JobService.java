/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.JobService;

import com.app.primaryNodeApp.model.database.dao.JobDao;
import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.Step;
import java.util.List;

/**
 *
 * @author filip
 */
public class JobService {
    
    /** OBJECT PROPERTIES **/
    
    private final JobDao jobDao = new JobDao();
    
    /** OBJECT METHODS **/
    
    /**
     * Method, that returns Job object by its id.
     * @param id Id of Job to be returned.
     * @return Job entity.
     */    
    public Job getById(Long id) {
        return jobDao.getById(id);
    }
    
    /**
     * Method, that returns all objects of Job entity.
     * @return List of all Job entities.
     */
    public List<Job> getAll() {
        return jobDao.getAll();
    }
    
    /**
     * Method, that returns object entity with all collections.
     * @param id Id of Job entity to be returned.
     * @return Job entity.
     */
    public Job getByIdWithCollections(Long id) {
        return jobDao.getByIdWithCollections(id);
    }
    
    /**
     * Mthod, that updates Job entity.
     * @param jobId Id of Job to be updated.
     * @param jobName New name of Job.
     * @param jobSteps List of all Job steps.
     * @param jobNodes List of all Job nodes.
     * @return Updated Job.
     */
    public Job updateJob(Long jobId, String jobName, List<Step> jobSteps, List<Node> jobNodes) {
        return jobDao.updateJob(jobId, jobName, jobSteps, jobNodes);
    }
    
    /**
     * Method, that saves new Job entity.
     * @param jobName New name of Job.
     * @param jobSteps List of all Job steps.
     * @param jobNodes List of all Job nodes.
     * @return Saved Job.
     */
    public Job saveNewJob(String jobName, List<Step> jobSteps, List<Node> jobNodes) {
        return jobDao.saveNewJob(jobName, jobSteps, jobNodes);
    }
    
    /**
     * Method, that deletes the object of Job entity.
     * @param id Id of Job to be deleted.
     */
    public void delete(Long id) {
        Job job = jobDao.getByIdWithCollections(id);
        // check if task isn't already running
        if (!job.getRunJobs().stream().anyMatch(runJob -> runJob.getFinishedAt() == null)) {
            jobDao.delete(id);
        } else {
            throw new RuntimeException("Nelze smazat běžící úlohu");
        }
    }
    
    /**
     * Method, that checks if the Job is running.
     * @param id Id of Job to be checked.
     * @return True if Job is running. False otherwise.
     */
    public boolean isRunningTask(Long id) {
        return jobDao.getByIdWithCollections(id).getRunJobs().stream().anyMatch(runJob -> runJob.getFinishedAt() == null);
    }
}
