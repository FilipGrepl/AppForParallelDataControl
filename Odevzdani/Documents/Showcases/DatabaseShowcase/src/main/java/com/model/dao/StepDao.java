/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.model.dao;

import com.model.database.entity.Step;

/**
 *
 * @author Filip
 */
public class StepDao extends Dao<Step, Long> {
    public StepDao() {
        super(Step.class);
    }
}
