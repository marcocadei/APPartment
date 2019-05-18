package com.unison.appartment.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHome userHome = (UserHome) o;
        return role == userHome.role &&
                Objects.equals(homename, userHome.homename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homename, role);
    }
}
