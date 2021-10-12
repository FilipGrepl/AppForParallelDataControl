/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.dao;

import com.model.database.entity.Job;

/**
 *
 * @author Filip
 */
public class JobDao extends Dao<Job, Long>{
    
    public JobDao() {
        super(Job.class);
    }
}
