package com.unison.appartment.model;

/**
 * Classe che rappresenta la relazione tra un utente e una casa a cui appartiene
 */
public class UserHome {

    public final static String ROLE_OWNER = "Proprietario";
    public final static String ROLE_MASTER = "Leader";
    public final static String ROLE_SLAVE = "Collaboratore";

    private String homeName;
    private String role;

    private int members;

    public UserHome() {
    }

    public UserHome(String homeName, String role, int members) {
        this.homeName = homeName;
        this.role = role;
        this.members = members;
    }

    public String getHomeName() {
        return homeName;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

}
