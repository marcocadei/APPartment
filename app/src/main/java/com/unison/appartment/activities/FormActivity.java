package com.unison.appartment.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.unison.appartment.fragments.FirebaseErrorDialogFragment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;

public abstract class FormActivity extends AppCompatActivity implements FirebaseErrorDialogFragment.FirebaseErrorDialogInterface {

    protected Class errorDialogDestinationActivity = EnterActivity.class;

    protected FirebaseProgressDialogFragment progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = (FirebaseProgressDialogFragment)getSupportFragmentManager()
                .findFragmentByTag(FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);
    }

    protected void resetErrorMessage(TextInputLayout inputLayout) {
        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
    }

    protected abstract boolean checkInput();

    protected void showErrorDialog() {
        FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
        dismissProgress();
        dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
    }

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

    protected void moveToNextActivity(Class destination) {
        Intent i = new Intent(this, destination);
        startActivity(i);
        finish();
    }

}
