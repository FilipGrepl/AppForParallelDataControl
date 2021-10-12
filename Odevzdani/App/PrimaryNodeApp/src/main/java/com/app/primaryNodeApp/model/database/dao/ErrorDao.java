/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.Error;

/**
 * The Dao object of Error entity.
 * @author Filip
 */
public class ErrorDao extends Dao<Error, Long>{
    
    /**
     * Constructor
     */
    public ErrorDao() {
        super(Error.class);
    }
}
