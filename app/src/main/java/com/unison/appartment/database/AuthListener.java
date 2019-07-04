package com.unison.appartment.database;

public interface AuthListener {
    void onSuccess();
    void onFail(Exception exception);
}
