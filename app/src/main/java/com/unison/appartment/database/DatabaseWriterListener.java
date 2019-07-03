package com.unison.appartment.database;

public interface DatabaseWriterListener {
    void onWriteSuccess();
    void onWriteFail(Exception exception);
}
