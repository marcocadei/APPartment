package com.unison.appartment;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.model.Home;
import com.unison.appartment.state.Appartment;

public class AppartmentService extends Service {
    public AppartmentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listenHome();
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
