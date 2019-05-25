package com.unison.appartment.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.state.Appartment;

import java.util.HashMap;
import java.util.Map;

public class AppartmentService extends Service {
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
        È necessario mantenere l'oggetto casa continuamente aggiornato perché il "name" e il "conversionFactor"
        sono utilizzati all'interno della MainActivity, ma possono essere cambiati da un altro utente
         */
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Appartment.getInstance().setHome(dataSnapshot.getValue(Home.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listenHomeUsers() {
        /*
        È necessario mantenere la lista di HomeUser continuamente aggiornata perché diversi attributi, tra
        cui i points, sono utilizzati all'interno della MainActivity, ma possono essere cambiati da un altro utente
         */
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + Appartment.getInstance().getHome().getName());
        dbRef.addValueEventListener(new ValueEventListener() {
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
        });
    }

    private void listenUserHome() {
        /*
        È necessario mantenere l'oggetto casa continuamente aggiornato perché il "name" e il "conversionFactor"
        sono utilizzati all'interno della MainActivity, ma possono essere cambiati da un altro utente
         */
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + new FirebaseAuth().getCurrentUserUid() + DatabaseConstants.SEPARATOR  + Appartment.getInstance().getHome().getName());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Appartment.getInstance().setUserHome(dataSnapshot.getValue(UserHome.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
