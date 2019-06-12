package com.unison.appartment.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FirebaseDatabaseReader implements DatabaseReader {
    @Override
    public void read(final String path, final DatabaseReaderListener listener, final Class type) {
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onReadSuccess(dataSnapshot.getKey(), dataSnapshot.getValue(type));
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

    @Override
    public void readList(final String path, final DatabaseReaderListener listener, final Class type) {
        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    for (DataSnapshot homeUserSnapshot : dataSnapshot.getChildren()) {
                        map.put(homeUserSnapshot.getKey(), homeUserSnapshot.getValue(type));
                    }
                    listener.onReadSuccess(dataSnapshot.getKey(), map);
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

    @Override
    public void retrieveUser(final String uid, final DatabaseReaderListener listener) {
        String path = DatabaseConstants.USERS + DatabaseConstants.SEPARATOR + uid;
        read(path, listener, User.class);
    }

    @Override
    public void retrieveHomePassword(String homeName, DatabaseReaderListener listener) {
        String path = DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + DatabaseConstants.HOMES_HOMENAME_PASSWORD;
        read(path, listener, String.class);
    }

    @Override
    public void retrieveHomeUsers(String homeName, DatabaseReaderListener listener) {
        String path = DatabaseConstants.HOMEUSERS + DatabaseConstants.SEPARATOR + homeName;
        readList(path, listener, HomeUser.class);
    }

    @Override
    public void retrieveHomeUserRefs(final String homeName, final String uid, final DatabaseReaderListener listener) {
        String path = DatabaseConstants.HOMEUSERSREFS + DatabaseConstants.SEPARATOR + homeName +
                DatabaseConstants.SEPARATOR + uid;

        DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(path);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, HashSet<String>> map = new HashMap<>();
                    HashSet<String> ownPosts = new HashSet<>();
                    HashSet<String> requestedRewards = new HashSet<>();
                    HashSet<String> assignedTasks = new HashSet<>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        switch (data.getKey()) {
                            case DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_REWARDS:
                                for (DataSnapshot rewardId : data.getChildren()) {
                                    requestedRewards.add(rewardId.getKey());
                                }
                                break;
                            case DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS:
                                for (DataSnapshot taskId : data.getChildren()) {
                                    assignedTasks.add(taskId.getKey());
                                }
                                break;
                            case DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_POSTS:
                                for (DataSnapshot postId : data.getChildren()) {
                                    ownPosts.add(postId.getKey());
                                }
                                break;
                        }
                    }
                    map.put(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_POSTS, ownPosts);
                    map.put(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_REWARDS, requestedRewards);
                    map.put(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS, assignedTasks);
                    listener.onReadSuccess(dataSnapshot.getKey(), map);
                }
                else {
                    listener.onReadEmpty();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onReadCancelled(databaseError);
            }
        });
    }

    @Override
    public void retrieveHome(String homeName, DatabaseReaderListener listener) {
        String path = DatabaseConstants.HOMES + DatabaseConstants.SEPARATOR + homeName;
        read(path, listener, Home.class);
    }

}
