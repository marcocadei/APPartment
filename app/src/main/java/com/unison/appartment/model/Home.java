package com.unison.appartment.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

/**
 * Classe che rappresenta una casa
 */
public class Home implements Serializable {
    // Quando una casa è creata ha un membro (il creatore stesso)
    public final static int DEFAULT_MEMBERS = 1;

    public final static int ROLE_OWNER = 0;
    public final static int ROLE_MASTER = 1;
    public final static int ROLE_SLAVE = 2;

    private final static String ATTRIBUTE_CONVERSION_FACTOR = "conversion-factor";

    private final static int DEFAULT_CONVERSION_FACTOR = 50;

    private String name;
    private String password;
    @PropertyName(ATTRIBUTE_CONVERSION_FACTOR)
    private int conversionFactor;
    private int members;

    // Costruttore vuoto richiesto da firebase
    public Home() {}

    public Home(String name, String password) {
        this(name, password, DEFAULT_CONVERSION_FACTOR, DEFAULT_MEMBERS);
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

    @PropertyName(ATTRIBUTE_CONVERSION_FACTOR)
    public int getConversionFactor() {
        return conversionFactor;
    }

    @PropertyName(ATTRIBUTE_CONVERSION_FACTOR)
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
