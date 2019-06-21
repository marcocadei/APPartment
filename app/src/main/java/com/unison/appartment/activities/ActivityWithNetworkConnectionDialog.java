package com.unison.appartment.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unison.appartment.R;
import com.unison.appartment.fragments.NetworkErrorDialogFragment;
import com.unison.appartment.fragments.NetworkErrorDialogFragment.NetworkErrorDialogInterface;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.NetworkStateReceiver;
import com.unison.appartment.utils.NetworkStateReceiver.NetworkStateReceiverListener;

public abstract class ActivityWithNetworkConnectionDialog extends AppCompatActivity
        implements NetworkStateReceiverListener, NetworkErrorDialogInterface {

    protected boolean bypassHomeEventsReceiver = false;

    /**
     * Broadcast receiver che si occupa di monitorare costantemente lo stato della rete
     */
    private NetworkStateReceiver networkStateReceiver;

    private BroadcastReceiver homeEventsReceiver;

    @Override
    protected void onPause() {
        super.onPause();
        // Quando l'activity non è più visibile rimuovo il listener che riceve gli
        // aggiornamenti sullo stato della rete
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);

        if (!bypassHomeEventsReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(homeEventsReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Quando l'activity torna ad essere visibile riaggiungo il listener che riceve gli
        // aggiornamenti sullo stato della rete
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        if (!bypassHomeEventsReceiver) {
            homeEventsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Appartment.EVENT_HOME_DELETE)) {
                        Log.w("zzzzzzzzzzzzzzzz", "receive broadcast HOME");
                        Intent i = new Intent(ActivityWithNetworkConnectionDialog.this, UserProfileActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra(UserProfileActivity.EXTRA_SNACKBAR_MESSAGE, getString(R.string.snackbar_home_deleted_message));
                        startActivity(i);
                    /*
                    Non mettiamo finish() perché altrimenti viene fuori un warning
                    "duplicate finish request" (don't know why tho)
                     */
                    } else if (intent.getAction().equals(Appartment.EVENT_HOME_KICK)) {
                        Log.w("zzzzzzzzzzzzzzzz", "receive broadcast");
                        Intent i = new Intent(ActivityWithNetworkConnectionDialog.this, UserProfileActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra(UserProfileActivity.EXTRA_SNACKBAR_MESSAGE, getString(R.string.snackbar_user_kicked_message));
                        startActivity(i);
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Appartment.EVENT_HOME_KICK);
            filter.addAction(Appartment.EVENT_HOME_DELETE);
            LocalBroadcastManager.getInstance(this).registerReceiver(homeEventsReceiver, filter);
        }
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
