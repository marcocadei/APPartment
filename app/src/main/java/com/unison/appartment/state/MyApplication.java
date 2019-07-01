package com.unison.appartment.state;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        /*
        NB
        Mantenere questa riga di codice abilita questa funzionalità: se si apre l'app, e si accede
        con un utente, dopodiché si chiude l'app dalla schermata del profilo utente, si spegne la
        rete e la si riapre allora l'app mostrerà comunque la lista delle case dell'utente.
        Senza questa riga alla chiusura dell'app si perdono i dati e se la si riapre senza rete
        si continua a vedere la progress bar.
         */
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}