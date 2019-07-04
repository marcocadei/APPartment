package com.unison.appartment.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.unison.appartment.state.ApplicationLoader;

/*
NOTA: Classe attualmente non utilizzata!

Questo è il broadcast receiver utilizzato per rilanciare il servizio di gestione delle notifiche
nel caso venisse chiuso.

Vedi anche classe ApplicationLoader e metodo onDestroy di NotificationService.
 */
public class AppStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.applicationHandler.post(new Runnable() {
            @Override
            public void run() {
                ApplicationLoader.startPushService();
            }
        });
    }
}
