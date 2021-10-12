/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.StepRunDataService;

import com.app.primaryNodeApp.model.database.dao.StepRunDataDao;
import com.app.primaryNodeApp.model.database.entity.RunJobNodeStepKey;
import com.app.primaryNodeApp.model.database.entity.StepRunData;
import java.util.List;

/**
 * The service for working with StepRunData object.
 * @author filip
 */
public class StepRunDataService {
    
    /** OBJECT PROPERTIES **/
    
    private final StepRunDataDao stepRunDataDao = new StepRunDataDao();
    
    /** OBJECT METHODS **/
    
    /**
     * Method, that returns StepRunData object with all collections by its id.
     * @param runJobNodeStepKey Id of StepRunData to be returned.
     * @return StepRunData entity.
     */
    public StepRunData getByIdWithCollections(RunJobNodeStepKey runJobNodeStepKey) {
        return stepRunDataDao.getByIdWithCollections(runJobNodeStepKey);
    }
    
    /**
     * Method, that returns all running data for specific RunJob and Node.
     * @param runJobId The id of RunJob for which the running data to be returned.
     * @param NodeId The id of Node for which the running data to be returned.
     * @return List of all running data for specific RunJob and Node.
     */
    public List<StepRunData> getNodeAllRunData(Long runJobId, Long NodeId) {
        return stepRunDataDao.getNodeAllRunData(runJobId, NodeId);
    }
    
}
