package com.unison.appartment.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

/**
 * Classe che rappresenta una casa
 */
public class Home implements Serializable {

    public final static int DEFAULT_CONVERSION_FACTOR = 50;

    public final static int ROLE_OWNER = 0;
    public final static int ROLE_MASTER = 1;
    public final static int ROLE_SLAVE = 2;

    private final static String ATTRIBUTE_CONVERSION_FACTOR = "conversion-factor";

    private String name;
    private String password;
    @PropertyName(ATTRIBUTE_CONVERSION_FACTOR)
    private int conversionFactor;

    // Costruttore vuoto richiesto da firebase
    public Home() {}

    public Home(String name, String password) {
        this(name, password, DEFAULT_CONVERSION_FACTOR);
    }

    public Home(String name, String password, int conversionFactor) {
        this.name = name;
        this.password = password;
        this.conversionFactor = conversionFactor;
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
}
