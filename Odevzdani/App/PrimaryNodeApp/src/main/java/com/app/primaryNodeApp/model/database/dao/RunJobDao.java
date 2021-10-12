/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.Job;
import com.app.primaryNodeApp.model.database.entity.RunJob;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;

/**
 * The Dao object of RunJob entity.
 * @author Filip
 */
public class RunJobDao extends Dao<RunJob, Long> {

    /**
     * Constructor
     */
    public RunJobDao() {
        super(RunJob.class);
    }

    /**
     * Method, that returns running jobs (not finished).
     * @return List of all running jobs.
     */
    public List<RunJob> getRunJobs() {
        this.prepareCriteria();
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.isNull(this.queryRoot.get("finishedAt")));
        List<RunJob> runJobs = this.executeQuery();
        return this.getWithCollections(runJobs);
    }

    /**
     * Method, that returns finished jobs (not running).
     * @return List of all finished jobs.
     */
    public List<RunJob> getFinishedJobs() {
        this.prepareCriteria();
        this.queryCriteria.select(this.queryRoot).where(this.queryBuilder.isNotNull(this.queryRoot.get("finishedAt")));
        List<RunJob> runJobs = this.executeQuery();
        return this.getWithCollections(runJobs);
    }

    /**
     * Method, that creates and saves a new running job.
     * @param jobId Id of job for which new run job to be created and saved.
     * @return Created and saved run job.
     */
    public RunJob saveNewRunJob(Long jobId) {
        RunJob runJob = null;
        try {
            Job job = new JobDao().getByIdWithCollections(jobId);
            this.openSession();
            this.transaction = this.session.beginTransaction();
            runJob = new RunJob();
            runJob.setStartedAt(new Date(System.currentTimeMillis()));
            runJob.setJob(job);
            session.save(runJob);
            new StepRunDataDao().createNewStepRecords(job, runJob.getId()).forEach(session::save);
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
        return runJob;
    }
}
