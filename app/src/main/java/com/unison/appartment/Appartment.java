package com.unison.appartment;

import android.content.Context;

public class Appartment {
    // Singleton pattern
    private static final Appartment holder = new Appartment();
    private Appartment() {}
    public static Appartment getInstance() {return holder;}

    // Salvataggio del context
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

    public void setHome(String home) {
        this.home = home;
    }

    public String getHome() {
        return this.home;
    }
}
