/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.primaryNodeApp.model.database.enums;

/**
 * The enumerator which represents user roles.
 * @author filip
 */
public class UserRoles {
    public static enum UserRolesEnum {
        USER("Uživatel"),
        ADMIN("Administrátor");

        private final String message;

        UserRolesEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

