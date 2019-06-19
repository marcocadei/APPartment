package com.unison.appartment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.unison.appartment.services.NotificationService;

// TODO - Classe scopiazzata da Telegram, eliminare se non serve
// (Nel caso c'è da aggiornare il manifest)
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
