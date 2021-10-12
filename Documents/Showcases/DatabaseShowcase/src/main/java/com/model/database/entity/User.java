package com.model.database.entity;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

// nutno nahradit knihovnu jboss-loggin.jar v C:\Program Files\glassfish-4.1.1\glassfish\modules za knihovnu
// dostupnou na https://mvnrepository.com/artifact/org.jboss.logging/jboss-logging/3.3.0.Final (musi se stejne jmenovat)
// pozor na modifikaci url databaze v hibernate.cfg.xml

/**
 *
 * @author Filip
 */

@Entity
@Table(name = "User")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_userID")
    private Long userID;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "userName")
    private String userName;
    
    @Column(columnDefinition= "LONGBLOB", name="password") 
    @ColumnTransformer(
      read="AES_DECRYPT(password, 'yourkey')", 
      write="AES_ENCRYPT(?, 'yourkey')")
    private byte[] password;
    
    public User() {

    }

    public User(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password.getBytes();
    }

    public long getUserId() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return new String(password);
    }

    public void setPassword(String password) {
        this.password = password.getBytes();
    }
}