package com.unison.appartment.database;

import com.unison.appartment.model.User;

public interface AuthListener {

    void onSuccess();

    void onFail(Exception exception);
}
