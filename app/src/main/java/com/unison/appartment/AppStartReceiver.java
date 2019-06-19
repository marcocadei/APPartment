package com.unison.appartment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// TODO - Classe scopiazzata da Telegram, eliminare se non serve
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
