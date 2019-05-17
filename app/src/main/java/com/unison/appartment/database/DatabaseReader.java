package com.unison.appartment.database;

public interface DatabaseReader {
    void read(final String path, final DatabaseReaderListener listener, final Class type);
    void retrieveUser(final String uid, final DatabaseReaderListener listener);
}
