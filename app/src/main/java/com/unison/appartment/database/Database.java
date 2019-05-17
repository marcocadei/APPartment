package com.unison.appartment.database;

import com.unison.appartment.model.User;

public interface Database {
    void writeUser(final User newUser, final DatabaseListener listener, final String uid);
}
