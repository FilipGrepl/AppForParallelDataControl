/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.services.UserService;

import com.app.primaryNodeApp.controllers.backingBeans.Login.LoginBean;
import com.app.primaryNodeApp.model.database.dao.UserDao;
import com.app.primaryNodeApp.model.database.entity.User;
import java.util.List;

/**
 *
 * @author filip
 */
public class UserService {
    
    /** OBJECT PROPERTIES **/

    private final UserDao userDao = new UserDao();

    /** OBJECT METHODS **/
    
    /**
     * Method, that returns User object by its id.
     * @param id Id of User to be returned.
     * @return User entity.
     */
    public User getById(Long id) {
        return userDao.getById(id);
    }
    
    /**
     * Method, that returns all objects of User entity.
     * @return List of all User entities.
     */
    public List<User> getAll() {
        return userDao.getAll();
    }

    /**
     * Method, that return User object by its name.
     * @param username Name of User to be returned.
     * @return User entity.
     */
    public User getByUsername(String username) {
        return userDao.getByUsername(username);
    }

    /**
     * Method, that changes password of user entity.
     * @param id Id of User for whom the password to be changed.
     * @param oldPassword Old user password.
     * @param newPassword_1 New user password (first intput).
     * @param newPassword_2 New user password (second input).
     */
    public void changeUserPassword(Long id, String oldPassword, String newPassword_1, String newPassword_2) {
        User user = userDao.getById(id);
        String enteredOldPasswordHash = LoginBean.getPasswordHash(oldPassword);

        // compare old password hashes
        if (!enteredOldPasswordHash.equals(user.getPasswordHash())) {
            throw new RuntimeException("Zadané současné heslo není správné");
        }

        // compare new passwords
        if (!newPassword_1.equals(newPassword_2)) {
            throw new RuntimeException("Zadaná hesla se neshodují");
        }
        user.setPasswordHash(LoginBean.getPasswordHash(newPassword_1));
        userDao.update(user);
    }

    /**
     * Method, that saves new User.
     * @param user User to be saved.
     */
    public void saveNewUser(User user) {
        if (userDao.getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Uživatel se zadaným uživatelským jménem již existuje");
        }
        userDao.save(user);
    }
    
    /**
     * Method, that updates User.
     * @param userId Id of User to be updated.
     * @param newUserValues New values of items to be updated.
     * @param isChangePassword True if password should be updated.
     * @param password_1 New user password (first intput).
     * @param password_2 New user password (second intput).
     */
    public void updateUser(Long userId, User newUserValues, boolean isChangePassword, String password_1, String password_2) {
        User user = userDao.getById(userId);

        if (!user.getUsername().equals(newUserValues.getUsername()) && userDao.getByUsername(newUserValues.getUsername()) != null) {
            throw new RuntimeException("Uživatel se zadaným uživatelským jménem již existuje");
        } 
            
        user.setFirstname(newUserValues.getFirstname());
        user.setLastname(newUserValues.getLastname());
        user.setRole(newUserValues.getRole());
        user.setUsername(newUserValues.getUsername());

        if (isChangePassword) {
            if (!password_1.equals(password_2)) {
                throw new RuntimeException("Zadaná hesla se neshodují");
            }
            user.setPasswordHash(LoginBean.getPasswordHash(password_1));
        }
        userDao.update(user);
    }
    
    /**
     * Method, that deletes the object of User entity.
     * @param id Id of User to be deleted.
     */
    public void delete(Long id) {
        userDao.delete(id);
    }

}
