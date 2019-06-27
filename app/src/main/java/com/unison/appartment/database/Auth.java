package com.unison.appartment.database;

import androidx.annotation.Nullable;

import com.unison.appartment.model.User;

public interface Auth {
    void signUp(final User newUser, final String password, final AuthListener listener);
    void signIn(final String email, final String password, final AuthListener listener);
    void reauthenticate(final  String email, final String password, final AuthListener listener);
    void deleteUser(final AuthListener listener);

    @Nullable
    String getCurrentUserUid();
}
