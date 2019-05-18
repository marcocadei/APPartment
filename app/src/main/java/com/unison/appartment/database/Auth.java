package com.unison.appartment.database;

import com.unison.appartment.model.User;

public interface Auth {
    void signUp(final User newUser, final AuthListener listener);
    void signIn(final String email, final String password, final AuthListener listener);

    String getCurrentUserUid();
}
