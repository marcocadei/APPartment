package com.unison.appartment.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.unison.appartment.state.Appartment;

import java.util.HashSet;
import java.util.Set;

public class HomeStateReceiver  extends BroadcastReceiver {
    protected Set<HomeStateReceiver.HomeStateReceiverListener> listeners;
    protected Boolean kicked;
    protected Boolean deleted;

    public HomeStateReceiver() {
        listeners = new HashSet<>();
        kicked = false;
        deleted = false;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Appartment.EVENT_HOME_DELETE)) {
            deleted = true;
            Log.d("ZZZZ", "EVENTO ELIMINATO CASA");
        } else if (intent.getAction().equals(Appartment.EVENT_HOME_KICK)) {
            kicked = true;
            Log.d("ZZZZ", "EVENTO RIMOSSO CASA");
        }

        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for(HomeStateReceiver.HomeStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(HomeStateReceiver.HomeStateReceiverListener listener) {
        kicked = Appartment.getInstance().getKicked();
        deleted = Appartment.getInstance().getHomeDeleted();
        Log.d("ZZZZ", "NOTIFY STATE kicked: " + kicked);
        Log.d("ZZZZ", "NOTIFY STATE deleted: " + deleted);
        if((kicked == null && deleted == null) || listener == null)
            return;

        if (deleted != null && deleted) {
            listener.homeDeleted();
        }
        else if(kicked != null && kicked) {
            listener.kickedFromHome();
        }
    }

    public void addListener(HomeStateReceiver.HomeStateReceiverListener l) {
        Log.d("ZZZZ", "AGGIUNTO LISTENER");
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(HomeStateReceiver.HomeStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface HomeStateReceiverListener {
        void kickedFromHome();
        void homeDeleted();
    }
}
