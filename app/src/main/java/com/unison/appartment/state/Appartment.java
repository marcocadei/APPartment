package com.unison.appartment.state;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.User;

/**
 * Classe che rappresenta lo stato globale dell'applicazione
 */
public class Appartment {
    // Singleton pattern, per avere sempre un'unica istanza di Appartment
    private static final Appartment holder = new Appartment();
    private Appartment() {}
    public static Appartment getInstance() {return holder;}

    private Home home;
    private User user;

    public void setHome(Home home) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedPreferencesConstants.HOME_KEY, new Gson().toJson(home));
        editor.apply();
        this.home = home;
    }

    public Home getHome() {
        if (home != null) {
            return home;
        }
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        String json = sp.getString(SharedPreferencesConstants.HOME_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(json, Home.class);
    }

    public User getUser() {
        if(user != null) {
            return user;
        }
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        String json = sp.getString(SharedPreferencesConstants.USER_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(json, User.class);
    }

    public void setUser(User user) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedPreferencesConstants.USER_KEY, new Gson().toJson(user));
        editor.apply();
        this.user = user;
    }
}
