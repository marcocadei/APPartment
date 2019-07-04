package com.unison.appartment.state;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.unison.appartment.services.NotificationService;

/*
NOTA: Classe attualmente non utilizzata!

Questa è la classe che estende Application utilizzata per la gestione del NotificationService.

Vedi anche classe AppStartReceiver e metodo onDestroy di NotificationService.

Se si sceglie di utilizzare questa classe, il manifest deve essere modificato e l'altra classe
che estende Application (MyApplication) dev'essere rimossa dal momento che non possono essere
registrate due o più classi Application contemporaneamente.
 */
public class ApplicationLoader extends Application {

    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    @Override
    public void onCreate() {
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable ignore) {

        }

        super.onCreate();

        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }

        applicationHandler = new Handler(applicationContext.getMainLooper());
    }

    public static void startPushService() {
        try {
            applicationContext.startService(new Intent(applicationContext, NotificationService.class));
        } catch (Throwable ignore) {

        }
    }

}
