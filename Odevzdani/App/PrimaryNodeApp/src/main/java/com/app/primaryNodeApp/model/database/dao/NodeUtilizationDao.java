/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.NodeUtilization;
import java.util.List;
import org.hibernate.HibernateException;

/**
 * The Dao object of NodeUtilization entity.
 * @author Filip
 */
public class NodeUtilizationDao extends Dao<NodeUtilization, Long> {
    
    /**
     * Constructor
     */
    public NodeUtilizationDao() {
        super(NodeUtilization.class);
    }
    
    /**
     * Method, that saves new utilization record and delete old utilization records in one transaction.
     * @param utilizationToDelete List of utilization data to be deleted.
     * @param recordToSave Utilization data to be saved.
     */
    public void saveAndDeleteUtilizationRecord(List<NodeUtilization> utilizationToDelete, NodeUtilization recordToSave) {
        try {
            this.openSession();
            this.transaction = this.session.beginTransaction();
            session.save(recordToSave);
            utilizationToDelete.forEach(utilizationData -> {
                session.delete(utilizationData);
            });
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
    }
}
