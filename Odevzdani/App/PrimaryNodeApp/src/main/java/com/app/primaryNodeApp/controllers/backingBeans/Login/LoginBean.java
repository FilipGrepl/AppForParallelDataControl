/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.Login;

import com.app.primaryNodeApp.model.database.entity.User;
import com.app.primaryNodeApp.services.UserService.UserService;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * The backing bean which saves the information about logged in user.
 * @author filip
 */
@Named
@SessionScoped
public class LoginBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private String password;
    private String username;

    private User user;
    
    private final UserService userService;
    
    /** STATIC METHODS **/
    
    /**
     * Method, which returns the sha256Hex for entered string.
     * @param password The string for which the sha256Hxe hash to be obtained.
     * @return Sha256Hex hash value of entered string.
     */
    public static String getPasswordHash(String password) {
        return DigestUtils.sha256Hex(password);
    }
    
    /** OBJECT METHODS **/
    
    /**
     * Constructor.
     */
    public LoginBean() {
        userService = new UserService();
    }
    
    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.password = null;
        this.username = null;
        this.user = null;
    }

    /**
     * Getter of user password.
     * @return User password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of user password.
     * @param password New value of user password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter of username.
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter of username.
     * @param username New value of username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter of logged in user.
     * @return Logged in user,
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter of logged in user.
     * @param user New logger in user.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method which checks the correct credentials and saves information about user which is logged in.
     * @return The url of page on which the user will be returned.
     */
    public String login() {
        this.user = userService.getByUsername(username);

        if (this.user != null && user.getPasswordHash().equals(LoginBean.getPasswordHash(this.password))) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", username);            
            return "runningTasks?i=0&faces-redirect=true";
        } else {    
            //Send an error message on Login Failure
            this.username = "";
            this.password = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Chyba!", "Zadané přihlašovací údaje nejsou platné"));
            return "";
        }
    }

    /**
     * Methods, which logs out the user.
     * @return The url of page on which the user will be returned.
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().invalidateSession();
        return "loginPage?faces-redirect=true";
    }
    
}
