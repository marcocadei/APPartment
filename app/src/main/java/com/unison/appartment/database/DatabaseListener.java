package com.unison.appartment.database;

public interface DatabaseListener {
    void onWriteSuccess(Object object);

    void onWriteFail(Exception exception);
}
