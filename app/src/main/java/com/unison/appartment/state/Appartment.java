package com.unison.appartment.state;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.unison.appartment.database.DatabaseConstants;
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
        /*
        È necessario mantenere l'oggetto casa continuamente aggiornato perché il "name" e il "conversionFactor"
        sono utilizzati all'interno della MainActivity, ma possono essere cambiati da un altro utente
         */
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + home.getName());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setHomeFB(dataSnapshot.getValue(Home.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        /*
        NB
        Per l'utente non ha senso tenere i dati aggiornati con firebase perché solo lui può modificare i
        propri dati andando nella UserProfileActivity, ma se ci va e li modifica allora poi quando rientra
        nella MainActivity avremo già l'oggetto aggiornato.
        Per gli altri oggetti invece è necessario il continuo aggiornamento perché possono essere modificati
        da più fonti.
         */
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

    public void setHomeUser(@NonNull HomeUser homeUser) {
        setSharedPreferencesValue(SharedPreferencesConstants.HOMEUSER_KEY, new Gson().toJson(homeUser));
        this.homeUser = homeUser;
    }

    public void resetHomeUser() {
        removeSharedPreferencesValue(SharedPreferencesConstants.HOMEUSER_KEY);
        this.homeUser = null;
    }

    public HomeUser getHomeUser() {
        if (homeUser == null) {
            homeUser = new Gson().fromJson(getSharedPreferencesJsonValue(SharedPreferencesConstants.HOMEUSER_KEY), HomeUser.class);
        }
        return homeUser;
    }

    public void clearAll() {
        resetUser();
        resetHome();
        resetUserHome();
        resetHomeUser();
    }
}
