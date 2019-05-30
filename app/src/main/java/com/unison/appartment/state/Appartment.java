package com.unison.appartment.state;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.model.UserHome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, HomeUser> homeUsers;

    private void setSharedPreferencesValue(final String key, final String jsonValue) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, jsonValue);
        editor.apply();
    }

    private void removeSharedPreferencesValue(final String key) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    private String getSharedPreferencesJsonValue(final String key) {
        SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences(SharedPreferencesConstants.FILE_KEY, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public void setHome(@NonNull Home home) {
        setSharedPreferencesValue(SharedPreferencesConstants.HOME_KEY, new Gson().toJson(home));
        this.home = home;
    }

    private void setHomeFB(Home home) {
        this.home = home;
    }

    public void resetHome() {
        removeSharedPreferencesValue(SharedPreferencesConstants.HOME_KEY);
        this.home = null;
    }

    public Home getHome() {
        if (home == null) {
            home = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.HOME_KEY), Home.class);
        }
        return home;
    }

    public void setUser(@NonNull User user) {
        setSharedPreferencesValue(SharedPreferencesConstants.USER_KEY, new Gson().toJson(user));
        this.user = user;
    }

    public void resetUser() {
        removeSharedPreferencesValue(SharedPreferencesConstants.USER_KEY);
        this.user = null;
    }

    public User getUser() {
        if (user == null) {
            user = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.USER_KEY), User.class);
        }
        return user;
    }

    public void setUserHome(@NonNull UserHome userHome) {
        setSharedPreferencesValue(SharedPreferencesConstants.USERHOME_KEY, new Gson().toJson(userHome));
        this.userHome = userHome;
    }

    public void resetUserHome() {
        removeSharedPreferencesValue(SharedPreferencesConstants.USERHOME_KEY);
        this.userHome = null;
    }

    public UserHome getUserHome() {
        if (userHome == null) {
            userHome = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.USERHOME_KEY), UserHome.class);
        }
        return userHome;
    }

    public void setHomeUsers(@NonNull Map<String, HomeUser> homeUsers) {
        setSharedPreferencesValue(SharedPreferencesConstants.HOMEUSER_KEY, new Gson().toJson(homeUsers));
        this.homeUsers = homeUsers;
    }

    public void resetHomeUsers() {
        removeSharedPreferencesValue(SharedPreferencesConstants.HOMEUSER_KEY);
        this.homeUsers = null;
    }

    public HomeUser getHomeUser(String uid) {
        if (homeUsers == null) {
            homeUsers = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.HOMEUSER_KEY), new TypeToken<HashMap<String, Object>>() { }.getType());
        }
        return homeUsers.get(uid);
    }

    public List<HomeUser> getHomeUsersList() {
        return new ArrayList<>(homeUsers.values());
    }

    public void clearAll() {
        resetUser();
        resetHome();
        resetUserHome();
        resetHomeUsers();
    }
}
