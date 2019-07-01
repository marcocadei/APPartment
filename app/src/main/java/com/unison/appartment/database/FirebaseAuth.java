package com.unison.appartment.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.unison.appartment.R;
import com.unison.appartment.activities.UserProfileActivity;
import com.unison.appartment.model.User;

public class FirebaseAuth implements Auth {

    @Override
    public void signUp(final User newUser, final String password, final AuthListener listener) {
        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(newUser.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFail(task.getException());
                        }
                    }
                });
    }

    @Override
    public void signIn(final String email, final String password, final AuthListener listener) {
        final com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFail(task.getException());
                        }
                    }
                });
    }

    @Override
    public void logOut() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void reauthenticate(String email, String password, final AuthListener listener) {
        final com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        auth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(email, password))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFail(task.getException());
                        }
                    }
                });
    }

    @Override
    public void deleteUser(final AuthListener listener) {
        final com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        auth.getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.onSuccess();
                        } else {
                            listener.onFail(task.getException());
                        }
                    }
                });
    }

    @Nullable
    public String getCurrentUserUid() {
        try {
            return com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
