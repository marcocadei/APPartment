package com.unison.appartment.database;

import com.google.firebase.database.DatabaseError;

public interface DatabaseWriterListener {
    void onWriteSuccess();
    void onWriteFail(Exception exception);
}
