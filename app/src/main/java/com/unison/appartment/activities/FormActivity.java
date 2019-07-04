package com.unison.appartment.activities;

import com.google.android.material.textfield.TextInputLayout;

public abstract class FormActivity extends ActivityWithDialogs {

    /**
     * Toglie il messaggio d'errore da un campo della form.
     * @param inputLayout Campo della form da cui togliere il messaggio d'errore.
     */
    protected void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    /**
     * Verifica che gli input immessi dall'utente nei diversi campi rispettino tutti i vincoli.
     * @return Valore booleano che indica se i controlli sono superati.
     */
    protected abstract boolean checkInput();

}
