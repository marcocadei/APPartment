package com.unison.appartment.livedata;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(listener);
        Log.w(getClass().getCanonicalName(), "DATI SCARICATI - ADD EVENT LISTENER");
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(listener);
        Log.w(getClass().getCanonicalName(), "DATI SCARICATI - REMOVE EVENT LISTENER");
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.e(getClass().getCanonicalName(), "DATI SCARICATI DA FIREBASE");
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
