package com.unison.appartment.state;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        // TODO vedere se va tolto o qualche miglioramento lo dà lo stesso
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}