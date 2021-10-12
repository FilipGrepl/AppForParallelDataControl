/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.Node;
import com.app.primaryNodeApp.model.database.entity.Step;
import java.util.List;
import org.hibernate.HibernateException;

/**
 * The Dao object of Job entity.
 * @author Filip
 */
public class JobDao extends Dao<Job, Long> {

    /**
     * Constructor
     */
    public JobDao() {
        super(Job.class);
    }

    /**
     * Method, that saves new job.
     * @param jobName Name of job.
     * @param jobSteps List of job Steps.
     * @param jobNodes List of job Nodes.
     * @return Job that was saved.
     */
    public Job saveNewJob(String jobName, List<Step> jobSteps, List<Node> jobNodes) {
        Job job;
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            job = new Job();
            job.setJobName(jobName);
            job.setJobNodes(jobNodes);
            session.save(job);

            for (Step step : jobSteps) {
                step.setJob(job);
                session.save(step);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
        return job;
    }

    /**
     * Job to be updated.
     * @param jobID Id of job to be updated.
     * @param jobName Name of job.
     * @param jobSteps List of job Steps.
     * @param jobNodes List of job Nodes.
     * @return Job, that was updated. Null when job with id jobID was not found.
     */
    public Job updateJob(Long jobID, String jobName, List<Step> jobSteps, List<Node> jobNodes) {
        Job job;
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            job = session.get(this.classType, jobID);
            
            if (job != null) {            
                jobSteps.forEach((step) -> {
                    if (step.getId() == null) {
                        step.setJob(job);
                        session.save(step);
                    } else {
                        session.update(step);
                    }
                });
                
                job.setJobName(jobName);
                job.setJobNodes(jobNodes);
                
                job.getJobSteps().clear();
                job.getJobSteps().addAll(jobSteps);
                session.update(job);
                
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (this.transaction != null) {
                this.transaction.rollback();
            }
            daoLogger.logInternalDatabaseErr(e);
            throw e;
        } finally {
            this.closeSession();
        }
        return job;
    }
}
