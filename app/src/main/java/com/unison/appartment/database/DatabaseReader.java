package com.unison.appartment.database;

public interface DatabaseReader {
    void retrieveUser(final String uid, final DatabaseReaderListener listener);
}
