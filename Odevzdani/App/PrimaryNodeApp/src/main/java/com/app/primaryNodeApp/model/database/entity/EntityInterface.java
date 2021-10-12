/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.entity;

import java.io.Serializable;

/**
 * Interface of all entities
 * @author filip
 * @param <Id> Type of Id of entity.
 */
public interface EntityInterface<Id extends Serializable> {
    public Id getId();
}
