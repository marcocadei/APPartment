package com.unison.appartment.database;

import com.google.firebase.database.DatabaseError;

public interface DatabaseListener {

    void onReadSuccess(Object object);
    void onReadEmpty();
    void onReadCancelled(DatabaseError databaseError);

    void onWriteSuccess();

    void onWriteFail(Exception exception);
}
