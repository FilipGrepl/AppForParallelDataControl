/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.ErrorService;

import com.app.primaryNodeApp.model.database.dao.ErrorDao;
import com.app.primaryNodeApp.model.database.entity.Error;

/**
 * The service for working with Error object.
 * @author filip
 */
public class ErrorService {
    
    /** OBJECT PROPERTIES **/
    
    private final ErrorDao errorDao = new ErrorDao();
    
    /** OBJECT METHODS **/
    
    /**
     * Method, that returns Error object by its id.
     * @param id Id of Error to be returned.
     * @return Error entity.
     */
    public Error getById(Long id) {
        return errorDao.getById(id);
    }
    
}
