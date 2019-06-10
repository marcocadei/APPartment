package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseError;
import com.unison.appartment.R;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.fragments.DeleteHomeUserConfirmationDialogFragment;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.fragments.FirebaseProgressDialogFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.ImageUtils;

import java.util.HashSet;
import java.util.Map;

/**
 * Classe che rappresenta l'Activity con il dettaglio di un membro della famiglia
 */
public class FamilyMemberDetailActivity extends ActivityWithDialogs implements DeleteHomeUserConfirmationDialogFragment.ConfirmationDialogInterface {

    public final static String EXTRA_MEMBER_OBJECT = "memberObject";

    private final static String BUNDLE_KEY_DELETED_USER_ID = "deletedUserId";
    private final static String BUNDLE_KEY_NEW_OWNER_ID = "newOwnerId";

    private final static String NO_MEMBERS_LEFT = "noMembersLeft";

    private HomeUser member;
    private String deletedUserId;
    private String newOwnerId;

    private DatabaseReader databaseReader;

    private View layoutButtons;
    private MaterialButton btnUpgrade;
    private MaterialButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_detail);

        this.errorDialogDestinationActivity = MainActivity.class;

        databaseReader = new FirebaseDatabaseReader();

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar;
        toolbar = findViewById(R.id.activity_family_member_detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent creationIntent = getIntent();
        member = (HomeUser) creationIntent.getSerializableExtra(EXTRA_MEMBER_OBJECT);
        
        String[] roles = getResources().getStringArray(R.array.desc_userhomes_uid_homename_role_values);

        // Recupero il riferimento agli elementi dell'interfaccia
        layoutButtons = findViewById(R.id.activity_family_member_detail_layout_buttons);
        btnDelete = findViewById(R.id.activity_family_member_detail_btn_delete);
        btnUpgrade = findViewById(R.id.activity_family_member_detail_btn_upgrade);

        final ImageView image = findViewById(R.id.activity_family_member_detail_img_profile);
        final ImageView imgDefault = findViewById(R.id.activity_family_member_detail_img_profile_default);
        TextView name = findViewById(R.id.activity_family_member_detail_text_name);
        TextView points = findViewById(R.id.activity_family_member_detail_text_points_value);
        TextView role = findViewById(R.id.activity_family_member_detail_text_role_value);

        TextView earnedPoints = findViewById(R.id.activity_family_member_detail_text_total_points_value);
        TextView earnedMoney = findViewById(R.id.activity_family_member_detail_text_earned_money_value);
        TextView publishedMessages = findViewById(R.id.activity_family_member_detail_text_text_posts_value);
        TextView publishedImages = findViewById(R.id.activity_family_member_detail_text_image_posts_value);
        TextView publishedAudio = findViewById(R.id.activity_family_member_detail_text_audio_posts_value);
        TextView claimedRewards = findViewById(R.id.activity_family_member_detail_text_claimed_rewards_value);
        TextView completedTasks = findViewById(R.id.activity_family_member_detail_text_completed_tasks_value);
        TextView rejectedTasks = findViewById(R.id.activity_family_member_detail_text_rejected_tasks_value);
        earnedPoints.setText(String.valueOf(member.getTotalEarnedPoints()));
        earnedMoney.setText(String.valueOf(member.getEarnedMoney()));
        publishedMessages.setText(String.valueOf(member.getTextPosts()));
        publishedImages.setText(String.valueOf(member.getImagePosts()));
        publishedAudio.setText(String.valueOf(member.getAudioPosts()));
        claimedRewards.setText(String.valueOf(member.getClaimedRewards()));
        completedTasks.setText(String.valueOf(member.getCompletedTasks()));
        rejectedTasks.setText(String.valueOf(member.getRejectedTasks()));
        
        name.setText(member.getNickname());
        points.setText(String.valueOf(member.getPoints()));
        role.setText(roles[member.getRole()]);
        if (member.getImage() != null) {
            imgDefault.setVisibility(View.INVISIBLE);
            image.setVisibility(View.VISIBLE);
            Glide.with(image.getContext()).load(member.getImage()).apply(RequestOptions.circleCropTransform()).into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(FamilyMemberDetailActivity.this, ImageDetailActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            FamilyMemberDetailActivity.this, image, ViewCompat.getTransitionName(image));
                    // Animazione apertura immagine tonda
                    /*getWindow().setSharedElementEnterTransition(TransitionInflater.from(FamilyMemberDetailActivity.this).inflateTransition(R.transition.itl_image_transition));
                    getWindow().setSharedElementExitTransition(TransitionInflater.from(FamilyMemberDetailActivity.this).inflateTransition(R.transition.itl_image_transition));*/
                    i.putExtra(ImageDetailActivity.EXTRA_IMAGE_URI, member.getImage());
                    i.putExtra(ImageDetailActivity.EXTRA_IMAGE_TYPE, ImageUtils.IMAGE_TYPE_ROUND);
                    startActivity(i, options.toBundle());
                }
            });
        }

        final int loggedUserRole = Appartment.getInstance().getUserHome().getRole();
        final String loggedUserUid = new FirebaseAuth().getCurrentUserUid();

        if (loggedUserRole == Home.ROLE_OWNER) {
            // Il proprietario può eliminare e upgradare/downgradare tutti quanti
            // (non può però modificarsi il proprio ruolo)
            if (!member.getUserId().equals(loggedUserUid)) {
                layoutButtons.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setText(R.string.activity_family_member_detail_btn_delete_masters);
                btnUpgrade.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Eliminazione di uno slave o di un master
                        deletedUserId = member.getUserId();
                        showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_other);
                    }
                });
                if (member.getRole() == Home.ROLE_SLAVE) {
                    btnUpgrade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Upgrade slave -> master
                            sendChangeRoleData(member.getUserId(), Home.ROLE_MASTER);
                        }
                    });
                }
                else {
                    btnUpgrade.setText(R.string.activity_family_member_detail_btn_upgrade_action_downgrade);
                    btnUpgrade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Downgrade master -> slave
                            sendChangeRoleData(member.getUserId(), Home.ROLE_SLAVE);
                        }
                    });
                }
            }
        }

        if (loggedUserRole == Home.ROLE_MASTER) {
            // I master possono eliminare e upgradare i collaboratori
            if (member.getRole() == Home.ROLE_SLAVE) {
                layoutButtons.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setText(R.string.activity_family_member_detail_btn_delete_masters);
                btnUpgrade.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Eliminazione di uno slave o di un master
                        deletedUserId = member.getUserId();
                        showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_other);
                    }
                });
                btnUpgrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Upgrade slave -> master
                        sendChangeRoleData(member.getUserId(), Home.ROLE_MASTER);
                    }
                });
            }
        }

        if (member.getUserId().equals(loggedUserUid)) {
            // L'utente che visualizza il suo profilo vede il tasto per uscire dalla casa
            layoutButtons.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Eliminazione dell'utente attualmente loggato
                    // Se si tratta del proprietario viene determinato il nuovo proprietario fra gli altri membri
                    if (loggedUserRole == Home.ROLE_OWNER) {
                        newOwnerId = getNewOwnerId();
                    }
                    deletedUserId = loggedUserUid;
                    showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_self);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(BUNDLE_KEY_DELETED_USER_ID, deletedUserId);
        outState.putString(BUNDLE_KEY_NEW_OWNER_ID, newOwnerId);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        deletedUserId = savedInstanceState.getString(BUNDLE_KEY_DELETED_USER_ID);
        newOwnerId = savedInstanceState.getString(BUNDLE_KEY_NEW_OWNER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        L'options menù contiene il solo tasto di modifica, che è visualizzato solo se è soddisfatta
        ALMENO UNA delle seguenti condizioni:
        - l'utente loggato è un master e sta visualizzando i dettagli di uno slave;
        - l'utente loggato è il proprietario;
        - l'utente loggato sta visualizzando i dettagli di sé stesso.
         */
        String loggedUserId = new FirebaseAuth().getCurrentUserUid();
        int loggedUserRole = Appartment.getInstance().getHomeUser(loggedUserId).getRole();
        if (loggedUserRole == Home.ROLE_OWNER || (loggedUserRole == Home.ROLE_MASTER && member.getRole() == Home.ROLE_SLAVE)
                || loggedUserId.equals(member.getUserId())) {
            getMenuInflater().inflate(R.menu.activity_family_member_detail_toolbar, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activity_family_member_detail_toolbar_edit) {
//            Intent i = new Intent(this, CreateRewardActivity.class);
//            i.putExtra(CreateRewardActivity.EXTRA_REWARD_DATA, reward);
//            startActivityForResult(i, EDIT_REWARD_REQUEST_CODE);
//            return true;
        }
        else if (item.getItemId() == R.id.activity_family_member_detail_emilia_romagna) {
            Intent i = new Intent(this, WebActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog(@StringRes int message) {
        DeleteHomeUserConfirmationDialogFragment dialog = DeleteHomeUserConfirmationDialogFragment.newInstance(message);
        dialog.show(getSupportFragmentManager(), DeleteHomeUserConfirmationDialogFragment.TAG_CONFIRMATION_DIALOG);
    }

    private void sendChangeRoleData(String userId, int newRole) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FamilyFragment.EXTRA_OPERATION_TYPE, FamilyFragment.OPERATION_CHANGE_ROLE);
        returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(FamilyFragment.EXTRA_NEW_ROLE, newRole);
        setResult(RESULT_OK, returnIntent);
        dismissProgress();
        finish();
    }

    private void sendRemoveUserData(String userId, HashSet<String> requestedRewards, HashSet<String> assignedTasks) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FamilyFragment.EXTRA_OPERATION_TYPE, FamilyFragment.OPERATION_REMOVE_USER);
        if (newOwnerId != null) {
            if (newOwnerId.equals(NO_MEMBERS_LEFT)) {
                returnIntent.putExtra(FamilyFragment.EXTRA_OPERATION_TYPE, FamilyFragment.OPERATION_REMOVE_HOME);
            }
            else {
                returnIntent.putExtra(FamilyFragment.EXTRA_NEW_OWNER_ID, newOwnerId);
            }
        }
        returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(FamilyFragment.EXTRA_REQUESTED_REWARDS, requestedRewards);
        returnIntent.putExtra(FamilyFragment.EXTRA_ASSIGNED_TASKS, assignedTasks);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private String getNewOwnerId() {
        Map<String, HomeUser> homeUsers = Appartment.getInstance().getHomeUsers();

        /*
        Se il proprietario è l'ultimo membro rimasto nella casa, anziché eliminare l'utente in sé
        devo eliminare l'intera casa.
         */
        if (homeUsers.size() == 1) {
            return NO_MEMBERS_LEFT;
        }

        HomeUser bestMaster = null;
        HomeUser bestSlave = null;
        for (HomeUser homeUser : homeUsers.values()) {
            if (homeUser.getRole() == Home.ROLE_SLAVE) {
                if (bestSlave == null || bestSlave.getPoints() < homeUser.getPoints()) {
                    bestSlave = homeUser;
                }
            }
            if (homeUser.getRole() == Home.ROLE_MASTER) {
                if (bestMaster == null || bestMaster.getPoints() < homeUser.getPoints()) {
                    bestMaster = homeUser;
                }
            }
        }

        return bestMaster != null ? bestMaster.getUserId() : bestSlave.getUserId();
    }

    final DatabaseReaderListener dbReaderListener = new DatabaseReaderListener() {
        @Override
        public void onReadSuccess(String key, Object object) {
            Map<String, HashSet<String>> homeUserRefs = (Map<String, HashSet<String>>) object;
            HashSet<String> requestedRewards = homeUserRefs.get(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_REWARDS);
            HashSet<String> assignedTasks = homeUserRefs.get(DatabaseConstants.HOMEUSERSREFS_HOMENAME_UID_TASKS);
            sendRemoveUserData(deletedUserId, requestedRewards, assignedTasks);
        }

        @Override
        public void onReadEmpty() {
            /*
            Se la lettura non ha restituito nessun riferimento a task assegnati o premi prenotati,
            posso semplicemente procedere con l'eliminazione dell'utente senza dovermi preoccupare
            di modificare anche dei nodi in /rewards o /tasks.
             */
            sendRemoveUserData(deletedUserId, new HashSet<String>(), new HashSet<String>());
        }

        @Override
        public void onReadCancelled(DatabaseError databaseError) {
            showErrorDialog();
            dismissProgress();
        }
    };

    @Override
    public void onConfirm() {
        progressDialog = FirebaseProgressDialogFragment.newInstance(
                getString(R.string.activity_family_member_detail_deletion_title),
                getString(R.string.activity_family_member_detail_deletion_description));
        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

        // Lettura dei riferimenti a task e premi da resettare
        databaseReader.retrieveHomeUserRefs(Appartment.getInstance().getHome().getName(), deletedUserId, dbReaderListener);
    }
}