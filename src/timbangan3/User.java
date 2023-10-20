/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package timbangan3;

/**
 *
 * @author yogi
 */
public class User {
    private String username;
    private String password;
    private boolean isAdmin;
    private String role;

    public User(String username, String password, boolean isAdmin, String role) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    boolean isAdministrator() {
        return isAdmin;
    }
    
    public String getRole(){
        return role;
    }
}
