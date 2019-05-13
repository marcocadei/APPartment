package com.unison.appartment.activities;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.unison.appartment.fragments.FirebaseErrorDialogFragment;

public abstract class FormActivity extends AppCompatActivity implements FirebaseErrorDialogFragment.FirebaseErrorDialogInterface {

    protected Class errorDialogDestinationActivity = EnterActivity.class;

    protected ProgressDialog progress;

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
        if (progress != null) {
            progress.dismiss();
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
