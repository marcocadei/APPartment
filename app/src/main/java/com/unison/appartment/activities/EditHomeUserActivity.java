package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.unison.appartment.R;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.KeyboardUtils;

import java.util.HashSet;
import java.util.Map;

public class EditHomeUserActivity extends FormActivity {

    public final static String EXTRA_HOMEUSER_DATA = "homeUserData";

    private final static String BUNDLE_KEY_ORIGINAL_NICKNAME = "originalNickname";
    private final static String BUNDLE_KEY_USER_ID = "UserId";

    private EditText inputNickname;
    private TextInputLayout layoutNickname;

    private String originalNickname;
    private String userId;

    private DatabaseReader databaseReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_home_user);

        // Modifica dell'activity di destinazione a cui andare quando si chiude il dialog di errore
        this.errorDialogDestinationActivity = MainActivity.class;

        databaseReader = new FirebaseDatabaseReader();

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_edit_home_user_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputNickname = findViewById(R.id.activity_edit_home_user_input_nickname_value);
        layoutNickname = findViewById(R.id.activity_edit_home_user_input_nickname);

        /*
        Quando questa activity viene visualizzata è SEMPRE stata chiamata da un intent in cui
        sono stati specificati dei dati.
         */
        Intent creationIntent = getIntent();
        HomeUser member = (HomeUser) creationIntent.getSerializableExtra(EXTRA_HOMEUSER_DATA);
        originalNickname = member.getNickname();
        userId = member.getUserId();
        inputNickname.setText(originalNickname);

        inputNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) resetErrorMessage(layoutNickname);
            }
        });

        FloatingActionButton floatFinish = findViewById(R.id.activity_edit_home_user_float_finish);
        floatFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideKeyboard(EditHomeUserActivity.this);
                if (checkInput()) {
                    if (!inputNickname.getText().toString().equals(originalNickname)) {
                        progressDialog = FirebaseProgressDialogFragment.newInstance(
                                getString(R.string.activity_family_member_detail_homeuserrefs_gathering_title),
                                getString(R.string.activity_family_member_detail_homeuserrefs_gathering_description));
                        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

                        // Lettura dei riferimenti a task e premi a cui va aggiornato il nickname
                        Log.e("zzzz HOMENAME", Appartment.getInstance().getHome().getName());
                        Log.e("zzzz USERID", userId);
                        databaseReader.retrieveHomeUserRefs(Appartment.getInstance().getHome().getName(), userId, dbReaderListener);
                    }
                    else {
                        returnNoChangedData();
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(BUNDLE_KEY_ORIGINAL_NICKNAME, originalNickname);
        outState.putString(BUNDLE_KEY_USER_ID, userId);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        originalNickname = savedInstanceState.getString(BUNDLE_KEY_ORIGINAL_NICKNAME);
        userId = savedInstanceState.getString(BUNDLE_KEY_USER_ID);
    }

    @Override
    protected boolean checkInput() {
        resetErrorMessage(layoutNickname);

        inputNickname.setText(inputNickname.getText().toString().trim());

        String nicknameValue = inputNickname.getText().toString();

        boolean result = true;

        // Controllo che i campi non siano stati lasciati vuoti
        if (nicknameValue.trim().length() == 0) {
            layoutNickname.setError(getString(R.string.form_error_missing_value));
            result = false;
        }

        return result;
    }

    private void returnNewHomeUserData(String userId, HashSet<String> requestedRewards, HashSet<String> assignedTasks) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(FamilyFragment.EXTRA_REQUESTED_REWARDS, requestedRewards);
        returnIntent.putExtra(FamilyFragment.EXTRA_ASSIGNED_TASKS, assignedTasks);
        returnIntent.putExtra(FamilyFragment.EXTRA_NEW_NICKNAME, inputNickname.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        dismissProgress();
        finish();
    }

    private void returnNoChangedData() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    final DatabaseReaderListener dbReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(String key, Object object) {
            Log.e("zzzz READ SUCCESS", "read success");
            Map<String, HashSet<String>> homeUserRefs = (Map<String, HashSet<String>>) object;
            HashSet<String> requestedRewards = homeUserRefs.get(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_REWARDS);
            if (requestedRewards == null) {
                Log.e("zzzz MAP IS NULL", "REQREW NULL");
            }
            else {
                Log.e("zzzz MAP OK", String.valueOf(requestedRewards.size()));
            }
            HashSet<String> assignedTasks = homeUserRefs.get(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS);
            returnNewHomeUserData(userId, requestedRewards, assignedTasks);
        }

        @Override
        public void onReadEmpty() {
            /*
            Se la lettura non ha restituito nessun riferimento a task assegnati o premi prenotati,
            posso semplicemente procedere senza dovermi preoccupare di modificare anche dei nodi
            in /rewards o /tasks.
             */
            returnNewHomeUserData(userId, new HashSet<String>(), new HashSet<String>());
        }

        @Override
        public void onReadCancelled(DatabaseError databaseError) {
            showErrorDialog();
            dismissProgress();
        }
    };
}
