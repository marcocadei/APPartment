package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

/**
 * Classe che rappresenta l'Activity con il dettaglio di un membro della famiglia
 */
public class FamilyMemberDetailActivity extends ActivityWithDialogs implements DeleteHomeUserConfirmationDialogFragment.ConfirmationDialogInterface {

    public final static String EXTRA_MEMBER_OBJECT = "memberObject";

    private final static int EDIT_HOMEUSER_REQUEST_CODE = 101;

    public final static int RESULT_OK = 200;
    public final static int RESULT_EDITED = 201;
    public final static int RESULT_NOT_EDITED = 202;

    private final static String BUNDLE_KEY_DELETED_USER_ID = "deletedUserId";
    private final static String BUNDLE_KEY_NEW_OWNER_ID = "newOwnerId";

    private final static String NO_MEMBERS_LEFT = "noMembersLeft";

    private HomeUser member;
    private String deletedUserId;
    private String newOwnerId;

    private DatabaseReader databaseReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_detail);

        // Modifica dell'activity di destinazione a cui andare quando si chiude il dialog di errore
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
        View layoutButtons = findViewById(R.id.activity_family_member_detail_layout_buttons);
        MaterialButton btnDelete = findViewById(R.id.activity_family_member_detail_btn_delete);
        MaterialButton btnUpgrade = findViewById(R.id.activity_family_member_detail_btn_upgrade);

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
                        showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_other, R.string.general_remove_button);
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
                        showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_other, R.string.general_remove_button);
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
                    if (newOwnerId != null && newOwnerId.equals(NO_MEMBERS_LEFT)) {
                        showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_last, R.string.general_leave_home_button);
                    }
                    else {
                        showDeleteConfirmationDialog(R.string.dialog_delete_home_user_confirmation_message_self, R.string.general_leave_home_button);
                    }
                }
            });
        }

        // Creo il grafico
        RadarChart chart = findViewById(R.id.activity_family_member_detail_chart);
        // Nomi delle statistiche
        // Le stringhe originali sono troppo lunghe quindi il grafico viene bruttissimo
        final String[] labels = new String[]{
                /*getString(R.string.desc_homeusers_homename_uid_totalearnedpoints_icon),*/
                getString(R.string.desc_homeusers_homename_uid_earnedmoney_icon),
                getString(R.string.desc_homeusers_homename_uid_textposts_icon),
                getString(R.string.desc_homeusers_homename_uid_imageposts_icon),
                getString(R.string.desc_homeusers_homename_uid_audioposts_icon),
                getString(R.string.desc_homeusers_homename_uid_claimedrewards_icon),
                getString(R.string.desc_homeusers_homename_uid_completedtasks_icon),
                getString(R.string.desc_homeusers_homename_uid_rejectedtasks_icon)
        };
        // Creo i dati come il grafico se li aspetta
        // Mettere qui i drawables come suggerito in https://stackoverflow.com/questions/44863021 non funge
        List<RadarEntry> entries = new ArrayList<>();
        // I punti crescono troppo rapidamente rispetto a tutti gli altri valori, rovinando così
        // la visualizzazione. Pertanto non vengono aggiunti al radar
        /*entries.add(new RadarEntry(member.getTotalEarnedPoints()));*/
        entries.add(new RadarEntry(member.getEarnedMoney()));
        entries.add(new RadarEntry(member.getTextPosts()));
        entries.add(new RadarEntry(member.getImagePosts()));
        entries.add(new RadarEntry(member.getAudioPosts()));
        entries.add(new RadarEntry(member.getClaimedRewards()));
        entries.add(new RadarEntry(member.getCompletedTasks()));
        entries.add(new RadarEntry(member.getRejectedTasks()));
        RadarDataSet radarDataSet = new RadarDataSet(entries, "");
        // Styling del dataset
        radarDataSet.setColor(getColor(R.color.colorPrimary));
        radarDataSet.setDrawFilled(true);
        radarDataSet.setFillColor(getColor(R.color.colorPrimary));
        radarDataSet.setHighlightEnabled(false);
        radarDataSet.setDrawValues(false);

        RadarData radarData = new RadarData(radarDataSet);
        chart.setData(radarData);
        // Styling del grafico
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getYAxis().setEnabled(false);
        chart.animateXY(1000, 1000);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setTextSize(20f);
        chart.getXAxis().setTypeface(ResourcesCompat.getFont(this, R.font.material_icons));
        chart.setExtraOffsets(20, 20, 20, 20);
        chart.invalidate();
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
            Intent i = new Intent(this, EditHomeUserActivity.class);
            i.putExtra(EditHomeUserActivity.EXTRA_HOMEUSER_DATA, member);
            startActivityForResult(i, EDIT_HOMEUSER_REQUEST_CODE);
            return true;
        }
        else if (item.getItemId() == R.id.activity_family_member_detail_emilia_romagna) {
            Intent i = new Intent(this, WebActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_HOMEUSER_REQUEST_CODE) {
            Intent returnIntent = new Intent();
            if (resultCode == Activity.RESULT_OK) {
                returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, data.getStringExtra(FamilyFragment.EXTRA_USER_ID));
                returnIntent.putExtra(FamilyFragment.EXTRA_OWN_POSTS, data.getSerializableExtra(FamilyFragment.EXTRA_OWN_POSTS));
                returnIntent.putExtra(FamilyFragment.EXTRA_REQUESTED_REWARDS, data.getSerializableExtra(FamilyFragment.EXTRA_REQUESTED_REWARDS));
                returnIntent.putExtra(FamilyFragment.EXTRA_ASSIGNED_TASKS, data.getSerializableExtra(FamilyFragment.EXTRA_ASSIGNED_TASKS));
                returnIntent.putExtra(FamilyFragment.EXTRA_NEW_NICKNAME, data.getStringExtra(FamilyFragment.EXTRA_NEW_NICKNAME));
                setResult(RESULT_EDITED, returnIntent);
            } else {
                // Necessario impostare questo resultCode perché altrimenti il default è OK e non
                // riesco a capire cosa è successo
                setResult(RESULT_NOT_EDITED, returnIntent);
            }
            finish();
        }
    }

    private void showDeleteConfirmationDialog(@StringRes int message, @StringRes int positiveButtonString) {
        DeleteHomeUserConfirmationDialogFragment dialog = DeleteHomeUserConfirmationDialogFragment.newInstance(message, positiveButtonString);
        dialog.show(getSupportFragmentManager(), DeleteHomeUserConfirmationDialogFragment.TAG_CONFIRMATION_DIALOG);
    }

    private void sendChangeRoleData(String userId, int newRole) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FamilyFragment.EXTRA_OPERATION_TYPE, FamilyFragment.OPERATION_CHANGE_ROLE);
        returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(FamilyFragment.EXTRA_NEW_ROLE, newRole);
        setResult(RESULT_OK, returnIntent);
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
        dismissProgress();
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
                getString(R.string.activity_family_member_detail_homeuserrefs_gathering_title),
                getString(R.string.activity_family_member_detail_homeuserrefs_gathering_description));
        progressDialog.show(getSupportFragmentManager(), FirebaseProgressDialogFragment.TAG_FIREBASE_PROGRESS_DIALOG);

        // Lettura dei riferimenti a task e premi da resettare
        databaseReader.retrieveHomeUserRefs(Appartment.getInstance().getHome().getName(), deletedUserId, dbReaderListener);
    }
}