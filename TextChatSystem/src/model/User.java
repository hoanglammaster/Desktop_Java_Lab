/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author hoang
 */
public class User implements Serializable{
    static final long serialVersionUID = 42L;
    private String userName;
    private String fullName;
    private String password;
    private boolean isOnline;
    
    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
  
    public User(String userName, String fullName, String password) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
    }
    
    public User(String userName, String fullName, String password, boolean isOnline) {
        this.userName = userName;
        this.fullName = fullName;
        this.password = password;
        this.isOnline = isOnline;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
    
    
}
