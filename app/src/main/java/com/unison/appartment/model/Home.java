package com.unison.appartment.model;

import java.util.HashMap;
import java.util.Map;

public class Home {

    private final static int DEFAULT_CONVERSION_FACTOR = 50;

    private String name;
    private String password;
    private int conversionFactor;

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

    public int getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(int conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

}
