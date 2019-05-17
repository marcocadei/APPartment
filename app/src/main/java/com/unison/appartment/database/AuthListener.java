package com.unison.appartment.database;

import com.unison.appartment.model.User;

public interface AuthListener {

    /**
     * Metodo per effettuare la scrittura in Firebase Database di un nuovo User
     *
     * @param user Il nuovo User che si vuole scrivere in Firebase Database
     */
    void onSignUpSuccess(User user);

    void onSignUpFail(Exception exception);
}
