package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

/**
 * Classe che rappresenta una casa
 */
public class Home implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.password);
        dest.writeInt(this.conversionFactor);
    }

    protected Home(Parcel in) {
        this.name = in.readString();
        this.password = in.readString();
        this.conversionFactor = in.readInt();
    }

    public static final Parcelable.Creator<Home> CREATOR = new Parcelable.Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel source) {
            return new Home(source);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };
}
