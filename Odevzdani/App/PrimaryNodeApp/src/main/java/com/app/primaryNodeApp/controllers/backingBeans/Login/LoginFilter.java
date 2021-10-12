/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.controllers.backingBeans.Login;

import com.app.primaryNodeApp.model.database.enums.UserRoles.UserRolesEnum;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.omnifaces.filter.HttpFilter;
import org.omnifaces.util.Servlets;

/**
 * Web filter for filtering http requests.
 * @author filip
 */

@WebFilter("/*")
public class LoginFilter extends HttpFilter implements Serializable {
    
    /** OJBECT PROPERTIES **/
    
    @Inject
    LoginBean loginBean;
        
    /** OBJECT METHODS **/
    
    /**
     * Method, which checks if the user has the permission to display the requested page.
     * @param request Http request.
     * @param response Http response.
     * @param session Http session.
     * @param chain Chain of filters.
     * @return True if the user has permission to display the page. False otherwise.
     */
    private boolean hasPermissionToDisplay(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) {
        List<String> adminRequests = new ArrayList<>();
        adminRequests.add(request.getContextPath() + "/faces/usersOverview.xhtml");
        adminRequests.add(request.getContextPath() + "/faces/userEditForm.xhtml");
        
        if (adminRequests.contains(request.getRequestURI()) && loginBean.getUser().getRole() != UserRolesEnum.ADMIN) {
            return false;
        } else {
            return true;
        }        
    }

    /**
     * Methods, which filters the http requests.
     * @param request Http request.
     * @param response Http response.
     * @param session Http session.
     * @param chain Chain of filters.
     * @throws ServletException If a ServletException occurs.
     * @throws IOException If an IOException occurs.
     */
    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, HttpSession session, FilterChain chain) throws ServletException, IOException {
        String loginURL = request.getContextPath() + "/faces/loginPage.xhtml";
        String homePageURL = request.getContextPath() + "/faces/runningTasks.xhtml";
               
        
        boolean loggedIn = (session != null) && (session.getAttribute("user") != null);
        boolean loginRequest = request.getRequestURI().equals(loginURL);
        boolean resourceRequest = request.getRequestURI().contains("/javax.faces.resource");
               
        // check if user is logged in
        if (loggedIn && loginRequest) {
            Servlets.facesRedirect(request, response, homePageURL);
        }
        
        if (loggedIn || loginRequest || resourceRequest) {
            if (!resourceRequest) { // Prevent browser from caching restricted resources. Prevent user from seeing previo   usly visited secured page after logout. See also https://stackoverflow.com/q/4194207/157882
                Servlets.setNoCacheHeaders(response);
            }
            
            //verify roles
            if (this.hasPermissionToDisplay(request, response, session, chain)) {

                chain.doFilter(request, response); // So, just continue request.
            } else {
                Servlets.facesRedirect(request, response, homePageURL);
            }
        }
        else {
            System.out.println(request.getRequestURI() + "         " + request.getServletPath());
            Servlets.facesRedirect(request, response, loginURL);
        }
    }

}