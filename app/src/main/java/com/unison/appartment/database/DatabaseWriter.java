package com.unison.appartment.database;

import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.model.User;
import com.unison.appartment.model.UserHome;

import java.util.Map;

public interface DatabaseWriter {
    // Metodo di scrittura generale
    void write(Map<String, Object> childUpdates, final DatabaseWriterListener listener);

    void writeUser(final User newUser, final String uid, final DatabaseWriterListener listener);
    void writeJoinHome(final String homeName, final String uid,
                       final HomeUser homeUser, final UserHome userHome,
                       final DatabaseWriterListener listener);
    void writeCreateHome(final String homeName, final String uid,
                         final Home home, final HomeUser homeUser, final UserHome userHome,
                         final DatabaseWriterListener listener);
}
