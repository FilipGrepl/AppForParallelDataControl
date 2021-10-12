/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.UserManagement;

import com.app.primaryNodeApp.controllers.backingBeans.Login.LoginBean;
import com.app.primaryNodeApp.model.database.entity.User;
import com.app.primaryNodeApp.services.UserService.UserService;
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * The backing bean for user edit page and save new user page.
 * @author filip
 */
@ViewScoped
@ManagedBean
public class UserEditBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private Long userID;

    private User user;
    private String password_1;
    private String password_2;
    
    private boolean isExistUsername;

    private boolean isChangePassword;
    
    private final UserService userService;
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor
     */
    public UserEditBean() {
        userService = new UserService();
    }

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        isChangePassword = false;
        isExistUsername = false;
    }

    /**
     * Method, which is called when URL parameters(userID) are processed.
     */
    public void loadData() {
        if (userID == null) {
            this.user = new User();
        } else {
            this.user = userService.getById(userID);
            if (this.user == null) {
                throw new RuntimeException("Uživatel s ID " + userID + " neexistuje.");
            }
        }
    }

    /**
     * Getter of userID.
     * @return UserID.
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * Setter of userID.
     * @param userID New value of userID.
     */
    public void setUserID(Long userID) {
        this.userID = userID;
    }

    /**
     * Getter of user to be edited.
     * @return User to be edited.
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter of user to be edited.
     * @param user New value of user to be edited.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Getter of value of password input 1.
     * @return Value of password input 1.
     */
    public String getPassword_1() {
        return password_1;
    }

    /**
     * Setter of value of password input 1.
     * @param password_1 New value of value of password input 1.
     */
    public void setPassword_1(String password_1) {
        this.password_1 = password_1;
    }

    /**
     * Getter of value of password input 2.
     * @return Value of password input 2.
     */
    public String getPassword_2() {
        return password_2;
    }

    /**
     * Setter of value of password input 2.
     * @param password_2 New value of value of password input 2.
     */
    public void setPassword_2(String password_2) {
        this.password_2 = password_2;
    }

    /**
     * Getter that returns true, if the entered username exists. False otherwise.
     * @return True, if the entered username exists. False otherwise.
     */
    public boolean isIsExistUsername() {
        return isExistUsername;
    }
    
    /**
     * Setter of value of isExistUsername.
     * @param isExistUsername New value of isExistUsername property.
     */
    public void setIsExistUsername(boolean isExistUsername) {
        this.isExistUsername = isExistUsername;
    }        
    
    /**
     * Getter of is change password checkbox.
     * @return True if the change password checkbox is checked. False otherwise.
     */
    public boolean isIsChangePassword() {
        return isChangePassword;
    }

     /**
     * Setter of is change password checkbox.
     * @param isChangePassword New value of is change password checkbox.
     */
    public void setIsChangePassword(boolean isChangePassword) {
        this.isChangePassword = isChangePassword;
    }
    
    public void changeUsernameHandler() {
        boolean isChangedUsername = true;
        if (this.userID != null) { // update user mode
            User oldUser = userService.getById(userID);
            isChangedUsername = !oldUser.getUsername().equals(this.user.getUsername());           
        } // update user mode
        
        if (isChangedUsername && userService.getByUsername(this.user.getUsername()) != null) {
            this.isExistUsername = true;
        } else {
            this.isExistUsername = false;
        }
    }

    /**
     * Handler of click event on save or update button.
     * @throws IOException If an IOException occurs.
     */
    public void saveUserHandler() throws IOException {
        try {
            if (this.userID == null) { // new user           
                    this.user.setPasswordHash(LoginBean.getPasswordHash(this.password_1));
                    userService.saveNewUser(user);
            } else {
                userService.updateUser(userID, user, isChangePassword, password_1, password_2);         
            }
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Chyba", e.getMessage()));
            return;
        }
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Změny byly úspěšně uloženy"));
        FacesContext.getCurrentInstance().getExternalContext().redirect("usersOverview.xhtml?i=5&faces-redirect=true");
    }
}
