package com.unison.appartment.state;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    public final static String EVENT_HOME_KICK = "com.unison.appartment.home.kick";
    public final static String EVENT_HOME_DELETE = "com.unison.appartment.home.delete";

    private Home home;
    private User user;
    private UserHome userHome;
    private Map<String, HomeUser> homeUsers;

    // Questo mi serve perché quando apro un completed task voglio visualizzare la cronologia, ma
    // per far ciò nel database devo leggere il percorso completions/home-name/task-name quindi mi
    // serve sapere il nome del task corrente
    private String currentCompletedTaskName;
    public String getCurrentCompletedTaskName() {
        if (currentCompletedTaskName == null) {
            currentCompletedTaskName = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.CURRENT_COMPLETEDTASK_NAME), String.class);
        }
        return currentCompletedTaskName;
    }
    public void setCurrentCompletedTaskName(String currentCompletedTaskName) {
        setSharedPreferencesValue(SharedPreferencesConstants.CURRENT_COMPLETEDTASK_NAME, new Gson().toJson(currentCompletedTaskName));
        this.currentCompletedTaskName = currentCompletedTaskName;
    }

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
        /*
        Per gli HomeUser è necessario salvare anche l'id poiché dato che non vengono usati i repository
        non è possibile fare questa operazione direttamente alla lettura dal database, ma gli id
        servono memorizzati altrimenti non si possono recuperare dallo stato / da SharedPreferences.
         */
        for (Map.Entry<String, HomeUser> entry : homeUsers.entrySet()) {
            entry.getValue().setUserId(entry.getKey());
        }

        setSharedPreferencesValue(SharedPreferencesConstants.HOMEUSER_KEY, new Gson().toJson(homeUsers));
        this.homeUsers = homeUsers;
    }

    public void resetHomeUsers() {
        removeSharedPreferencesValue(SharedPreferencesConstants.HOMEUSER_KEY);
        this.homeUsers = null;
    }

    public HomeUser getHomeUser(String uid) {
        if (homeUsers == null) {
            homeUsers = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.HOMEUSER_KEY), new TypeToken<HashMap<String, HomeUser>>() { }.getType());
        }
        return homeUsers.get(uid);
    }

    public Map<String, HomeUser> getHomeUsers() {
        if (homeUsers == null) {
            homeUsers = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.HOMEUSER_KEY), new TypeToken<HashMap<String, HomeUser>>() { }.getType());
        }
        return homeUsers;
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
