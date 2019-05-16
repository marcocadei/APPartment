package com.unison.appartment.model;

/**
 * Classe che rappresenta la relazione tra un utente e una casa a cui appartiene
 */
public class UserHome {

    private String homeName;
    private int role;
    private int members;

    public UserHome() {
    }

    public UserHome(String homeName, int role) {
        this.homeName = homeName;
        this.role = role;
    }

    public UserHome(String homeName, int role, int members) {
        this(homeName, role);
        this.members = members;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
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
