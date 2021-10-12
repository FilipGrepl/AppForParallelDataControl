/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.dataClasses.interfaces;

import java.util.List;

/**
 * Interface for getting (run/hostory/saved) task data.
 * @author filip
 */
public interface TaskDataInterface<T>  {
    public List<T> getTasksData();
}
