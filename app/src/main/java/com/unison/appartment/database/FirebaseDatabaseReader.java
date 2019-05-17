package com.unison.appartment.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.MyApplication;
import com.unison.appartment.R;
import com.unison.appartment.model.User;

public class FirebaseDatabaseReader implements DatabaseReader {
    @Override
    public void retrieveUser(final String uid, final DatabaseReaderListener listener) {
        String separator = MyApplication.getAppContext().getResources().getString(R.string.db_separator);
        String path = MyApplication.getAppContext().getResources().getString(R.string.db_users) + separator + MyApplication.getAppContext().getResources().getString(R.string.db_users_uid, uid);
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onReadSuccess(dataSnapshot.getValue(User.class));
                }
                else {
                    listener.onReadEmpty();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                /*
                onCancelled viene invocato solo se si verifica un errore a lato server oppure se
                le regole di sicurezza impostate in Firebase non permettono l'operazione richiesta.
                In questo caso perciò viene visualizzato un messaggio di errore generico, dato che
                la situazione non può essere risolta dall'utente.
                 */
                listener.onReadCancelled(databaseError);
            }
        });
    }
}
