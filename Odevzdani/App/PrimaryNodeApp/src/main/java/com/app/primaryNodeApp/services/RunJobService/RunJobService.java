/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.RunJobService;

import com.app.primaryNodeApp.model.database.dao.RunJobDao;
import com.app.primaryNodeApp.model.database.entity.RunJob;

/**
 *
 * @author filip
 */
public class RunJobService {
    
    /** OBJECT PROPERTIES **/
    
    private final RunJobDao runJobDao = new RunJobDao();
    
    /** OBJECT METHODS **/
    
    /**
     * Method, that returns RunJob object by its id.
     * @param id Id of RunJob to be returned.
     * @return RunJob entity.
     */
    public RunJob getById(Long id) {
        return runJobDao.getById(id);
    }
    
    /**
     * Method, that deletes the object of RunJob entity.
     * @param id Id of RunJob to be deleted.
     */
    public void delete(Long id) {
        runJobDao.delete(id);
    }
    
    /**
     * Method that creates and saves new RunJob object.
     * @param jobId Id of job for which new RunJob object to be created.
     * @return Created RunJob object.
     */
    public RunJob saveNewRunJob(Long jobId) {
        return runJobDao.saveNewRunJob(jobId);
    }
    
}
