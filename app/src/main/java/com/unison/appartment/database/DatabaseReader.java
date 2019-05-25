package com.unison.appartment.database;

public interface DatabaseReader {
    // Metodo di lettura generale
    void read(final String path, final DatabaseReaderListener listener, final Class type);
    void readList(final String path, final DatabaseReaderListener listener, final Class type);

    void retrieveHome(final String homeName, final DatabaseReaderListener listener);
    void retrieveHomePassword(final String homeName, final DatabaseReaderListener listener);
    void retrieveHomeUsers(final String homeName, final String uid, final DatabaseReaderListener listener);
    void retrieveUser(final String uid, final DatabaseReaderListener listener);
}
