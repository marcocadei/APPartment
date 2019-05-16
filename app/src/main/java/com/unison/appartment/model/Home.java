package com.unison.appartment.model;

/**
 * Classe che rappresenta una casa
 */
public class Home {

    public final static int ROLE_OWNER = 0;
    public final static int ROLE_MASTER = 1;
    public final static int ROLE_SLAVE = 2;

    private final static int DEFAULT_CONVERSION_FACTOR = 50;

    private String name;
    private String password;
    private int conversionFactor;
    private int members;

    public Home(String name, String password) {
        this(name, password, DEFAULT_CONVERSION_FACTOR, 0);
    }

    public Home(String name, String password, int conversionFactor, int members) {
        this.name = name;
        this.password = password;
        this.conversionFactor = conversionFactor;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(int conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }
}
