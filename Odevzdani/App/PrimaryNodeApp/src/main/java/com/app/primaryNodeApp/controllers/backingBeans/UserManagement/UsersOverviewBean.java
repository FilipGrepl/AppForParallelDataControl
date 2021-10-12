/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.UserManagement;

import com.app.primaryNodeApp.model.database.entity.User;
import com.app.primaryNodeApp.model.database.enums.UserRoles.UserRolesEnum;
import com.app.primaryNodeApp.services.UserService.UserService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 * The backing bean for users overview page.
 * @author filip
 */
@ViewScoped
@ManagedBean
public class UsersOverviewBean implements Serializable {
    
    /** OBJECT PROPERTIES **/

    private List<User> users;
    private List<User> usersFiltered;

    private User selectedUser;

    private boolean isLastAdmin;
    
    private final UserService userService;
    
    /** OBJECT METHODS **/
            
    /**
     * Constructor
     */
    public UsersOverviewBean() {
        userService = new UserService();
    }

    /**
     * Method which is called before the bean is set into scope.
     */
    @PostConstruct
    private void postConstruct() {
        this.loadData();
    }

    /**
     * Method, which loads data about all users.
     */
    private void loadData() {
        users = userService.getAll();
        if (users.stream().filter(user -> user.getRole() == UserRolesEnum.ADMIN).count() == 1) {
            this.isLastAdmin = true;
        } else {
            this.isLastAdmin = false;
        }
    }

    /**
     * Getter of all application users.
     * @return List of all application users.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Setter of all application users.
     * @param users New value of list of all application users.
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }

    /**
     * Getter of all filtered application users.
     * @return List of filtered application users.
     */
    public List<User> getUsersFiltered() {
        return usersFiltered;
    }

    /**
     * Setter of all filtered application users.
     * @param usersFiltered New value of list of all filtered application users.
     */
    public void setUsersFiltered(List<User> usersFiltered) {
        this.usersFiltered = usersFiltered;
    }

    /**
     * Getter of selected user (user that corresponds with row on which the user clicked).
     * @return Selected user.
     */
    public User getSelectedUser() {
        return selectedUser;
    }

    /**
     * Setter of selected user (user that corresponds with row on which the user clicked).
     * @param selectedUser New value of selected user. 
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * Method that returns true if there is only one admin in whole application. False otherwise.
     * @return True if there is only one admin in whole application. False otherwise.
     */
    public boolean isIsLastAdmin() {
        return isLastAdmin;
    }

    /**
     * Getter of all user roles for filtering.
     * @return List of select items of all user roles in application for filtering.
     */
    public List<SelectItem> getUserRolesValues() {
        List<SelectItem> userRolesValues = new ArrayList<>();
        for (UserRolesEnum userRole : UserRolesEnum.values()) {
            userRolesValues.add(new SelectItem(userRole.toString(), userRole.getMessage()));
        }
        return userRolesValues;
    }

    /**
     * Method, that redirect user to user edit page.
     * @throws IOException If an IOException occurs.
     */
    public void redirectToUserEdit() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("userEditForm.xhtml?i=5&userID=" + this.selectedUser.getId());
    }

    /**
     * Handler of click event method on delete button.
     * @param userID Id of user to be deleted.
     */
    public void deleteUserHandler(Long userID) {
        userService.delete(userID);
        this.loadData();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Uživatel byl odstraněn"));
    }
}
