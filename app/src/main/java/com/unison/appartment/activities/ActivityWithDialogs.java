package com.unison.appartment.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.unison.appartment.fragments.FirebaseErrorDialogFragment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;

/**
 * Classe astratta che rappresenta una generica activity in cui vengono visualizzati dei
 * DialogFragment.
 * È predisposta per la visualizzazione di un ProgressDialog (da usare per esempio quando viene
 * eseguita un'operazione di lunga durata) e di un AlertDialog che alla chiusura riporta ad una
 * certa activity (modificabile cambiando il valore del campo errorDialogDestinationActivity).
 * Entrambi questi dialogs sono non-dismissable (in particolare, il ProgressDialog non può essere
 * chiuso a seguito di un'azione dell'utente dal momento che non presenta bottoni o elementi con
 * cui l'utente può interagire).
 */
public abstract class ActivityWithDialogs extends AppCompatActivity implements FirebaseErrorDialogFragment.FirebaseErrorDialogInterface {

    /**
     * Activity a cui si ritorna dopo la chiusura dell'ErrorDialog.
     */
    protected Class errorDialogDestinationActivity = EnterActivity.class;

    /**
     * Progress dialog (non-dismissable) visualizzabile nell'activity.
     * @see FirebaseProgressDialogFragment
     */
    protected FirebaseProgressDialogFragment progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Se si è verificato un configuration change che ha fatto sì che l'activity venisse ricreata
        mentre era aperto il ProgressDialog, il riferimento a quest'ultimo viene recuperato e salvato
        nella variabile progressDialog. Se questa è la prima creazione dell'activity, nella variabile
        viene salvato il valore null.
         */
        progressDialog = (FirebaseProgressDialogFragment)getSupportFragmentManager()
                .findFragmentByTag(FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
    }

    /**
     * Mostra l'AlertDialog con cui viene comunicata all'utente una situazione di errore.
     */
    protected void showErrorDialog() {
        FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
        dismissProgress();
        dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
    }

    /**
     * Rimuove il ProgressDialog mostrato all'utente (se questo è visualizzato su schermo).
     */
    protected void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onErrorDialogFragmentDismiss() {
        Intent i = new Intent(this, errorDialogDestinationActivity);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    /**
     * Trasferisce il controllo all'activity indicata e termina l'activity corrente.
     * L'activity di destinazione viene chiamata con un {@link Intent} in cui NON vengono passati
     * dati extra.
     * @param destination Activity di destinazione.
     */
    protected void moveToNextActivity(Class destination) {
        moveToNextActivity(destination, true);
    }

    /**
     * Trasferisce il controllo all'activity indicata.
     * L'activity di destinazione viene chiamata con un {@link Intent} in cui NON vengono passati
     * dati extra.
     * @param destination Activity di destinazione.
     * @param finish      Valore booleano che indica se terminare o meno l'activity corrente.
     */
    protected void moveToNextActivity(Class destination, boolean finish) {
        Intent i = new Intent(this, destination);
        startActivity(i);
        if (finish) {
            finish();
        }
    }

}
