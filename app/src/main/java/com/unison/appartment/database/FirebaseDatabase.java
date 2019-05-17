package com.unison.appartment.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.unison.appartment.Appartment;
import com.unison.appartment.MyApplication;
import com.unison.appartment.R;
import com.unison.appartment.activities.UserProfileActivity;
import com.unison.appartment.model.User;

public class FirebaseDatabase implements Database {
    public void writeUser(final User newUser, final DatabaseListener listener, final String uid) {
        // Scrittura dei dati relativi al nuovo utente nel database
        String separator = MyApplication.getAppContext().getResources().getString(R.string.db_separator);
        String path = MyApplication.getAppContext().getResources().getString(R.string.db_users) + separator + MyApplication.getAppContext().getResources().getString(R.string.db_users_uid, uid);
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(path);
        dbRef.setValue(newUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onWriteSuccess(newUser);
                        } else {
                            listener.onWriteFail(task.getException());
                        }
                    }
                });
    }
}
