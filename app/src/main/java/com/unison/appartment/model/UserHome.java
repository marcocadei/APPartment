package com.unison.appartment.model;

/**
 * Classe che rappresenta la relazione tra un utente e una casa a cui appartiene
 */
public class UserHome {

    private String homename;
    private int role;
    private int members;

    public UserHome() {
    }

    public UserHome(String homename, int role) {
        this.homename = homename;
        this.role = role;
        this.members = Home.DEFAULT_MEMBERS;
    }

    public UserHome(String homename, int role, int members) {
        this(homename, role);
        this.members = members;
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

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

}
