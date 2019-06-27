package com.unison.appartment.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

/**
 * Classe che rappresenta una casa
 */
public class Home implements Parcelable {

    public final static int ROLE_OWNER = 0;
    public final static int ROLE_MASTER = 1;
    public final static int ROLE_SLAVE = 2;

    private String name;
    private String password;

    // Costruttore vuoto richiesto da firebase
    public Home() {}

    public Home(String name, String password) {
        this.name = name;
        this.password = password;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.password);
    }

    protected Home(Parcel in) {
        this.name = in.readString();
        this.password = in.readString();
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
