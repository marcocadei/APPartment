package com.unison.appartment.database;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.unison.appartment.utils.ImageUtils;

import java.io.ByteArrayOutputStream;
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
            /*
            Il codice è strutturato in questo modo perchéla foto caricata sullo storage di firebase veniva
            ruotata di 90° quando scattata in modalità portrait. Questo è dovuto al fatto che molti telefoni
            hanno una fotocamera in landscape e poi scrivono dei metadati (efix) per ricordarsi come è stata
            scattata la foto.
            Seguendo la guida ufficiale la foto veniva caricata ruotata e ci sono dei metodi per evitarlo, ma:
            - non ne ho trovato uno funzionante
            - richiedevano parecchio codice in più
            Allora ho pensato a Glide che già uso per caricare le immagini e difatto risolve questo problema internamente.
            L'unica differenza è che di solito lo uso per caricare in un ImageView, mentre in questo caso
            carico il risultato in un Bitmap che poi mando allo storage di firebase
             */
            Glide.with(MyApplication.getAppContext()).asBitmap().load(newUser.getImage()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    // Ridemnsiono l'immagine (in generale la rimpicciolisco)
                    resource = ImageUtils.resize(resource, ImageUtils.MAX_WIDTH, ImageUtils.MAX_HEIGHT);
                    // Comprimo l'immagine
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resource.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] data = baos.toByteArray();

                    // UUID genera un nome univoco per il file che sto caricando
                    final StorageReference userImageRef = FirebaseStorage.getInstance().getReference().child(StorageConstants.USER_IMAGES+ UUID.randomUUID().toString());
                    UploadTask uploadTask = userImageRef.putBytes(data);

                    // Codice della guida per ottenere l'URL di download del media appena caricato
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
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

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
