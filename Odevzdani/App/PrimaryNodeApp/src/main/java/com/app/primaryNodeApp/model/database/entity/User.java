package com.app.primaryNodeApp.model.database.entity;


import com.app.primaryNodeApp.model.database.enums.UserRoles.UserRolesEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.constraints.Length;


/**
 * The User entity.
 * @author Filip
 */

@Entity
@Table(name = "User")
public class User implements Serializable, EntityInterface<Long> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_userID")
    private Long id;

    @Column(name = "firstname", nullable=false)
    private String firstname;

    @Column(name = "lastname", nullable=false)
    private String lastname;

    @Column(name = "username", unique=true, nullable=false)
    @Length(max=100)
    private String username;
    
    @Column(name = "passwordHash", nullable=false)
    private String passwordHash;
    
    @Column(name = "role", nullable=false)
    @Enumerated(EnumType.STRING)
    private UserRolesEnum role;
    
    public User() {
    }

    public User(String firstname, String lastName, String username, String passwordHash, UserRolesEnum role) {
        this.firstname = firstname;
        this.lastname = lastName;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRolesEnum getRole() {
        return role;
    }

    public void setRole(UserRolesEnum role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return 
            "User{" + 
                "id=" + id + 
                ", firstaname=" + firstname + 
                ", lastname=" + lastname + 
                ", userName=" + username + 
                ", role=" + role +
            '}';
    }
    
    
}