package com.unison.appartment.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
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
import com.unison.appartment.R;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta l'Activity con il dettaglio di un membro della famiglia
 */
public class FamilyMemberDetailActivity extends AppCompatActivity {

    public final static String EXTRA_MEMBER_OBJECT = "memberObject";

    private HomeUser member;

    private View layoutButtons;
    private MaterialButton btnUpgrade;
    private MaterialButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_detail);

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

        int loggedUserRole = Appartment.getInstance().getUserHome().getRole();
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
                        // TODO elimina altro utente
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
                        // TODO elimina altro utente
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
                    // TODO elimina me stesso (gestire diversamente a seconda ruolo!)
//                    sendRemoveUserData(loggedUserUid);
                }
            });
        }

        // Creo il grafico
        RadarChart chart = findViewById(R.id.activity_family_member_detail_chart);
        // Nomi delle statistiche
        // Le stringhe originali sono troppo lunghe quindi il grafico viene bruttissimo
        final String[] labels = new String[]{
                getString(R.string.desc_homeusers_homename_uid_totalearnedpoints_icon),
                getString(R.string.desc_homeusers_homename_uid_earnedmoney_icon),
                getString(R.string.desc_homeusers_homename_uid_textposts_icon),
                getString(R.string.desc_homeusers_homename_uid_imageposts_icon),
                getString(R.string.desc_homeusers_homename_uid_audioposts_icon),
                getString(R.string.desc_homeusers_homename_uid_claimedrewards_icon),
                getString(R.string.desc_homeusers_homename_uid_completedtasks_icon),
                getString(R.string.desc_homeusers_homename_uid_rejectedtasks_icon)
        };
        // Recupero il riferimento alle icone usate per mostrare le statistiche
        // Nel grafico a radar non mostrerò le etichette (sono troppo lunghe), ma le icone
        Drawable iconTotalEarnedPoints = getDrawable(R.drawable.ic_star_border);
        Drawable iconEarnedMoney = getDrawable(R.drawable.ic_attach_money);
        Drawable iconTextPosts = getDrawable(R.drawable.ic_message);
        Drawable iconImagePosts = getDrawable(R.drawable.ic_photo_size_select_actual);
        Drawable iconAudioPosts = getDrawable(R.drawable.ic_audiotrack);
        Drawable iconClaimedRewards = getDrawable(R.drawable.ic_card_giftcard);
        Drawable iconCompletedTasks = getDrawable(R.drawable.ic_check);
        Drawable iconRejectedTasks = getDrawable(R.drawable.ic_clear);
        // Creo i dati come il grafico se li aspetta
        // Mettere qui i drawables come suggerito in https://stackoverflow.com/questions/44863021 non funge
        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(member.getTotalEarnedPoints()));
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
        chart.getXAxis().setTextSize(100);
        chart.invalidate();
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
        return super.onOptionsItemSelected(item);
    }

    private void sendChangeRoleData(String userId, int newRole) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FamilyFragment.EXTRA_OPERATION_TYPE, FamilyFragment.OPERATION_CHANGE_ROLE);
        returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, userId);
        returnIntent.putExtra(FamilyFragment.EXTRA_NEW_ROLE, newRole);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void sendRemoveUserData(String userId) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FamilyFragment.EXTRA_OPERATION_TYPE, FamilyFragment.OPERATION_REMOVE_USER);
        returnIntent.putExtra(FamilyFragment.EXTRA_USER_ID, userId);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}