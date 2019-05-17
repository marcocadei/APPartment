package com.unison.appartment;

import android.content.Context;

import com.unison.appartment.model.User;

/**
 * Classe che rappresenta lo stato globale dell'applicazione
 */
public class Appartment {
    // Singleton pattern, per avere sempre un'unica istanza di Appartment
    private static final Appartment holder = new Appartment();
    private Appartment() {}
    public static Appartment getInstance() {return holder;}

    // Salvataggio del contesto, per accedere alle resources
    private Context context;
    public void init(Context context) {
        if (this.context == null) {
            this.context = context;
        }
    }
    public Context getContext() {
        return this.context;
    }

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
        this.user = user;
    }
}
