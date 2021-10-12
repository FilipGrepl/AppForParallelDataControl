/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.ChangePassword;

import com.app.primaryNodeApp.controllers.backingBeans.Login.LoginBean;
import com.app.primaryNodeApp.services.UserService.UserService;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * The backing bean for the password change form.
 * @author filip
 */
@ManagedBean
@RequestScoped
public class ChangePasswordBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private String oldPassword;
    private String newPassword_1;
    private String newPassword_2;
    
    private final UserService userService;

    @Inject
    private LoginBean loginBean;
    
    /** OBJECT METHODS **/

    /**
     * Constructor
     */
    public ChangePasswordBean() {
        userService = new UserService();
    }

    /**
     * Getter of old password.
     * @return Old password.
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Setter of the old password.
     * @param oldPassword New value of the old password.
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Getter of new password.
     * @return New password.
     */
    public String getNewPassword_1() {
        return newPassword_1;
    }

    /**
     * Setter of new password.
     * @param newPassword_1 New value of new password.
     */
    public void setNewPassword_1(String newPassword_1) {
        this.newPassword_1 = newPassword_1;
    }

    /**
     * Getter of new password second time.
     * @return New password second time.
     */
    public String getNewPassword_2() {
        return newPassword_2;
    }

    /**
     * Setter of new password second time.
     * @param newPassword_2 New password second time.
     */
    public void setNewPassword_2(String newPassword_2) {
        this.newPassword_2 = newPassword_2;
    }

    /**
     * Handler of click event of change password button. It persists the new password.
     * @throws IOException If an IOException occurs.
     */
    public void changePasswordHandler() throws IOException {
        try {
            this.userService.changeUserPassword(loginBean.getUser().getId(), oldPassword, newPassword_1, newPassword_2);
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Chyba", e.getMessage()));
            return;
        }
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Heslo bylo úspěšně změněno"));
        FacesContext.getCurrentInstance().getExternalContext().redirect("runningTasks.xhtml?i=0&faces-redirect=true");
    }
}
