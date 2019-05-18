package com.unison.appartment.model;

/**
 * Classe che rappresenta la relazione tra un utente e una casa a cui appartiene
 */
public class UserHome {

    private String homename;
    private int role;

    public UserHome() {
    }

    public UserHome(String homename, int role) {
        this.homename = homename;
        this.role = role;
    }


    public String getHomename() {
        return homename;
    }

    public void setHomename(String homename) {
        this.homename = homename;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}
