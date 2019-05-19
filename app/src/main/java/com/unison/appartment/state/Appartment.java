package com.unison.appartment.state;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.model.UserHome;


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
    private UserHome userHome;
    private HomeUser homeUser;

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

    public void setUser(User user) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedPreferencesConstants.USER_KEY, new Gson().toJson(user));
        editor.apply();
        this.user = user;
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

    public void setUserHome(UserHome userHome) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedPreferencesConstants.USERHOME_KEY, new Gson().toJson(userHome));
        editor.apply();
        this.userHome = userHome;
    }

    public UserHome getUserHome() {
        if(userHome != null) {
            return userHome;
        }
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        String json = sp.getString(SharedPreferencesConstants.USERHOME_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(json, UserHome.class);
    }

    public void setHomeUser(HomeUser homeUser) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SharedPreferencesConstants.HOMEUSER_KEY, new Gson().toJson(homeUser));
        editor.apply();
        this.homeUser = homeUser;
    }

    public HomeUser getHomeUser() {
        if(homeUser != null) {
            return homeUser;
        }
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        String json = sp.getString(SharedPreferencesConstants.HOMEUSER_KEY, null);
        Gson gson = new Gson();
        return gson.fromJson(json, HomeUser.class);
    }
}
