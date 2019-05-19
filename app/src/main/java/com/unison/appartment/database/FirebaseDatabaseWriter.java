package com.unison.appartment.database;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.model.UserHome;
import com.unison.appartment.state.MyApplication;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
        // Quando scrivo un utente nel database devo primare caricare la foto nel firebase storage
        // poi ottenere un URL a quella foto e salvare quello all'interno del realtime database
        // Tutto questo è fatto se l'utente ha selezionato una foto
        if (newUser.getImage() != null) {
            Uri file = Uri.parse(newUser.getImage());
            // Comprimo l'immagine prima di caricarla
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), file);
            } catch (IOException e) {
                listener.onWriteFail(e);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            // UUID genera un nome univoco per il file che sto caricando
            final StorageReference userImageRef = FirebaseStorage.getInstance().getReference().child("images/users/"+ UUID.randomUUID().toString());
            UploadTask uploadTask = userImageRef.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        listener.onWriteFail(task.getException());
                    }
                    return userImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String userPhotoUrl = task.getResult().toString();
                        newUser.setImage(userPhotoUrl);
                        writeUserAfterUpdate(newUser, uid, listener);
                    } else {
                        listener.onWriteFail(task.getException());
                    }
                }
            });
        } else {
            writeUserAfterUpdate(newUser, uid, listener);
        }
    }

    private void writeUserAfterUpdate(final User newUser, final String uid, final DatabaseWriterListener listener) {
        // Scrittura dei dati relativi al nuovo utente nel database
        String path = DatabaseConstants.USERS + DatabaseConstants.SEPARATOR + uid;

        HashMap<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, newUser);

        write(childUpdates, listener);
    }

    @Override
    public void writeJoinHome(final String homeName, final String uid,
                              final HomeUser homeUser, final UserHome userHome,
                              final DatabaseWriterListener listener) {
        String familyPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR + uid;
        String userhomePath = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + uid + DatabaseConstants.SEPARATOR + homeName;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(familyPath, homeUser);
        childUpdates.put(userhomePath, userHome);

        write(childUpdates, listener);
    }

    @Override
    public void writeCreateHome(final String homeName, final String uid,
                                final Home home, final HomeUser homeUser, final UserHome userHome,
                                final DatabaseWriterListener listener) {
        final String homePath = DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + homeName;
        final String homeuserPath = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName + DatabaseConstants.SEPARATOR + uid;
        final String userhomePath = DatabaseConstants.USERHOMES + DatabaseConstants.SEPARATOR + uid + DatabaseConstants.SEPARATOR + homeName;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(homePath, home);
        childUpdates.put(homeuserPath, homeUser);
        childUpdates.put(userhomePath, userHome);

        write(childUpdates, listener);
    }


}
