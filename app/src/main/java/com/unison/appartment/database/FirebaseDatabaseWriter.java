package com.unison.appartment.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.MyApplication;
import com.unison.appartment.R;
import com.unison.appartment.model.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseWriter implements DatabaseWriter {
    @Override
    public void write(final Map<String, Object> childUpdates, final DatabaseWriterListener listener) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.updateChildren(childUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onWriteSuccess();
                        }
                        else {
                            listener.onWriteFail(task.getException());
                        }
                    }
                });
    }

    public void writeUser(final User newUser, final String uid, final DatabaseWriterListener listener) {
        // Scrittura dei dati relativi al nuovo utente nel database
        String path = DatabaseConstants.USERS + DatabaseConstants.SEPARATOR + uid;
        HashMap<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, newUser);
        write(childUpdates, listener);
    }
}
