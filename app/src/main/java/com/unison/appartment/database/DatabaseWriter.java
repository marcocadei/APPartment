package com.unison.appartment.database;

import com.unison.appartment.model.User;
import java.util.Map;

public interface DatabaseWriter {
    // Metodo di scrittura generale
    void write(Map<String, Object> childUpdates, final DatabaseWriterListener listener);

    void writeUser(final User newUser, final String uid, final DatabaseWriterListener listener);
}
