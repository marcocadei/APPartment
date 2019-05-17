package com.unison.appartment.database;

import com.google.firebase.database.DatabaseError;

public interface DatabaseReaderListener {
    void onReadSuccess(Object object);
    void onReadEmpty();
    void onReadCancelled(DatabaseError databaseError);
}
