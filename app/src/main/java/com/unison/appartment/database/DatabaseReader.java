package com.unison.appartment.database;

public interface DatabaseReader {
    // Metodo di lettura generale
    void read(final String path, final DatabaseReaderListener listener, final Class type);

    void retrieveUser(final String uid, final DatabaseReaderListener listener);
    void retrieveHomePassword(final String homeName, final DatabaseReaderListener listener);
    void retrieveHome(final String homeName, final DatabaseReaderListener listener);
}
