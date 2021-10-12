/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.dao;

import com.app.primaryNodeApp.model.database.entity.User;
import java.util.List;
import javax.persistence.criteria.Predicate;

/**
 * The Dao object of User entity.
 * @author Filip
 */
public class UserDao extends Dao<User, Long> {
   
    /**
     * Constructor
     */
    public UserDao() {
        super(User.class);
    }   
    
    /**
     * Method, that returns User object by username.
     * @param username Name of user for which the User object to be returned.
     * @return User with entered name. If no entity is found, null is returned.
     */
    public User getByUsername(String username) {
        this.prepareCriteria();
        Predicate predicate = this.queryBuilder.equal(this.queryRoot.get("username"), username);
        this.queryCriteria.select(this.queryRoot).where(predicate);        
        List<User> users = this.executeQuery();
                
        if (users.size() > 1) {
            String IDs = "";
            for (User user : users) IDs=IDs+"("+user.getId().toString()+") ";
            daoLogger.logDuplicateErrStatus(username, IDs);
        }    
        return (users.isEmpty() ? null : users.get(0));
    }

}
