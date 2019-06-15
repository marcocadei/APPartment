package com.unison.appartment.activities;

import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

import com.unison.appartment.fragments.NetworkErrorDialogFragment;
import com.unison.appartment.fragments.NetworkErrorDialogFragment.NetworkErrorDialogInterface;
import com.unison.appartment.utils.NetworkStateReceiver;
import com.unison.appartment.utils.NetworkStateReceiver.NetworkStateReceiverListener;

public abstract class ActivityWithNetworkConnectionDialog extends AppCompatActivity
        implements NetworkStateReceiverListener, NetworkErrorDialogInterface {

    /**
     * Broadcast receiver che si occupa di monitorare costantemente lo stato della rete
     */
    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onPause() {
        super.onPause();
        // Quando l'activity non è più visibile rimuovo il listener che riceve gli
        // aggiornamenti sullo stato della rete
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Quando l'activity torna ad essere visibile riaggiungo il listener che riceve gli
        // aggiornamenti sullo stato della rete
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void networkAvailable() {
        // Se il dialog di errore di rete è mostrato allora lo nascondo.
        // Questo controllo è necessario perché all'apertura dell'app se la rete è presente il dialog
        // non è mai stato mostrato e quindi non faccio dismiss() su un null
        NetworkErrorDialogFragment dialog = (NetworkErrorDialogFragment)getSupportFragmentManager().findFragmentByTag(NetworkErrorDialogFragment.TAG_NETWORK_ERROR_DIALOG);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void networkUnavailable() {
        if (getSupportFragmentManager().findFragmentByTag(NetworkErrorDialogFragment.TAG_NETWORK_ERROR_DIALOG) == null) {
            new NetworkErrorDialogFragment().show(getSupportFragmentManager(), NetworkErrorDialogFragment.TAG_NETWORK_ERROR_DIALOG);
        }
    }

    @Override
    public void onNetworkErrorDialogFragmentDismiss() {
        moveTaskToBack(true);
    }
}
