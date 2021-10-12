/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.dao;

import com.model.database.entity.JobNodeStepKey;
import com.model.database.entity.JobRunData;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Filip
 */
public class JobRunDataDao extends Dao<JobRunData, JobNodeStepKey> {
    
    public JobRunDataDao() {
        super(JobRunData.class);
    }
}
