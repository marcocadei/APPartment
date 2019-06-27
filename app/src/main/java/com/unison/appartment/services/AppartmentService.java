package com.unison.appartment.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.R;
import com.unison.appartment.activities.UserProfileActivity;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.state.Appartment;

import java.util.HashMap;
import java.util.Map;

public class AppartmentService extends Service {

    private DatabaseReference homeRef;
    private DatabaseReference homeUsersRef;
    private DatabaseReference userHomeRef;
    private ValueEventListener homeListener;
    private ValueEventListener homeUsersListener;
    private ValueEventListener userHomeListener;

    public AppartmentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        NB
        Per l'utente non ha senso tenere i dati aggiornati con firebase perché solo lui può modificare i
        propri dati andando nella UserProfileActivity, ma se ci va e li modifica allora poi quando rientra
        nella MainActivity avremo già l'oggetto aggiornato.
        Per gli altri oggetti invece è necessario il continuo aggiornamento perché possono essere modificati
        da più fonti.
         */
        listenHome();
        listenHomeUsers();
        listenUserHome();
    }

    private void listenHome() {
        /*
        Il nome della casa non può essere mai modificato, ma l'oggetto viene comunque tenuto aggiornato
        per intercettare il caso in cui una casa venga eliminata.
        (Quando era stato realizzato il servizio, Home conteneva dei dati che potevano essere modificati
        da altri utenti quindi questo aggiornamento era necessario, poi la logica è cambiata e si è
        scelto di conservare ugualmente l'aggiornamento dell'oggetto.)
         */
        homeRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        homeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Appartment.getInstance().setHome(dataSnapshot.getValue(Home.class));
                }
                else {
                    Intent intent = new Intent();
                    intent.setAction(Appartment.EVENT_HOME_DELETE);
                    intent.putExtra(UserProfileActivity.EXTRA_SNACKBAR_MESSAGE, getString(R.string.snackbar_home_deleted_message));
                    LocalBroadcastManager.getInstance(AppartmentService.this).sendBroadcast(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        homeRef.addValueEventListener(homeListener);
    }

    private void listenHomeUsers() {
        /*
        È necessario mantenere la lista di HomeUser continuamente aggiornata perché diversi attributi, tra
        cui i points, sono utilizzati all'interno della MainActivity, ma possono essere cambiati da un altro utente
         */
        homeUsersRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        homeUsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, HomeUser> homeUsers = new HashMap<>();
                    for (DataSnapshot homeUserSnapshot : dataSnapshot.getChildren()) {
                        homeUsers.put(homeUserSnapshot.getKey(), homeUserSnapshot.getValue(HomeUser.class));
                    }
                    Appartment.getInstance().setHomeUsers(homeUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        homeUsersRef.addValueEventListener(homeUsersListener);
    }

    private void listenUserHome() {
        userHomeRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + new FirebaseAuth().getCurrentUserUid() + DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        userHomeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Appartment.getInstance().setUserHome(dataSnapshot.getValue(UserHome.class));
                }
                else {
                    Intent intent = new Intent();
                    intent.setAction(Appartment.EVENT_HOME_KICK);
                    LocalBroadcastManager.getInstance(AppartmentService.this).sendBroadcast(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userHomeRef.addValueEventListener(userHomeListener);
    }

    /*
    Questo consente di far ripartire il servizio nel caso in cui sia ucciso dal sistema
    https://stackoverflow.com/questions/45005648/how-to-restart-android-service-if-killed
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homeRef != null && homeListener != null) {
            homeRef.removeEventListener(homeListener);
        }
        if (userHomeRef != null && userHomeListener != null) {
            userHomeRef.removeEventListener(userHomeListener);
        }
        if (homeUsersRef != null && homeUsersListener != null) {
            homeUsersRef.removeEventListener(homeUsersListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
