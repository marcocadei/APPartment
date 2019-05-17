package com.unison.appartment.database;

import com.unison.appartment.model.User;

public interface DatabaseWriter {
    void writeUser(final User newUser, final String uid, final DatabaseWriterListener listener);
}
