package com.unison.appartment;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.unison.appartment.model.User;

/**
 * Classe che rappresenta lo stato globale dell'applicazione
 */
public class Appartment {
    // Singleton pattern, per avere sempre un'unica istanza di Appartment
    private static final Appartment holder = new Appartment();
    private Appartment() {}
    public static Appartment getInstance() {return holder;}

    private String home;
    private User user;

    public void setHome(String home) {
        this.home = home;
    }

    public String getHome() {
        return this.home;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(
                MyApplication.getAppContext().getResources().getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        editor.putString("User", gson.toJson(user));
        editor.apply();
        this.user = user;
    }
}
