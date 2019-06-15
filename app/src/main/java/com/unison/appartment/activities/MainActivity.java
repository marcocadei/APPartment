package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.fragments.DeleteHomeUserConfirmationDialogFragment;
import com.unison.appartment.fragments.DoneFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.services.AppartmentService;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.fragments.MessagesFragment;
import com.unison.appartment.R;
import com.unison.appartment.fragments.RewardsFragment;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.viewmodel.HomeUserViewModel;

import java.util.List;

/**
 * Classe che rappresenta l'Activity principale di una Home
 */
public class MainActivity extends ActivityWithNetworkConnectionDialog implements DeleteHomeUserConfirmationDialogFragment.ConfirmationDialogInterface {

    /*
    Costanti che indicano la posizione delle varie sezioni così come ordinate nella bottom
    navigation; sono usate per le animazioni di ingresso/uscita al cambio fragment.
     */
    private static final byte POSITION_MESSAGES = 0;
    private static final byte POSITION_FAMILY = 1;
    private static final byte POSITION_TODO = 2;
    private static final byte POSITION_DONE = 3;
    private static final byte POSITION_REWARDS = 4;

    // Ultima voce selezionata nella bottom navigation
    private int lastPosition = POSITION_MESSAGES;
    // Voce attualmente selezionata nella bottom navigation
    private int currentPosition = POSITION_MESSAGES;

    private static final String BUNDLE_KEY_SELECTED_BOTTOM_MENU_ITEM = "selectedBottomMenuItem";
    private static final String BUNDLE_KEY_OLD_POINTS_VALUE = "oldPointsValue";

    private int selectedBottomNavigationMenuItemId;

    private Toolbar toolbar;
    private TextView userPoints;
    private HomeUserViewModel viewModel;
    private BottomNavigationView bottomNavigation;
    private ViewPager pager;
    private FragmentSlidePagerAdapter pagerAdapter;

    private int oldPointsValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Avvio il servizio che mantiene aggiornato lo stato
        Intent intent = new Intent(this, AppartmentService.class);
        startService(intent);

        // Precondizione: Quando si arriva in questa activity, TUTTI gli oggetti della classe
        // Appartment sono stati settati

        // Supporto per la toolbar
        toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        userPoints = toolbar.findViewById(R.id.activity_main_text_points_value);
        final View layoutPoints = toolbar.findViewById(R.id.activity_main_layout_points);
        viewModel = ViewModelProviders.of(this).get(HomeUserViewModel.class);
        readHomeUser();

        // Le voci della bottom navigation sono un menù
        // Alla creazione dell'activity vengono impostati titolo e logo della toolbar in base alla voce
        // selezionata del menù alla prima apertura
        bottomNavigation = findViewById(R.id.activity_main_bottom_navigation);
        if (savedInstanceState != null) {
            selectedBottomNavigationMenuItemId = savedInstanceState.getInt(BUNDLE_KEY_SELECTED_BOTTOM_MENU_ITEM);
        } else {
            selectedBottomNavigationMenuItemId = bottomNavigation.getSelectedItemId();
        }
        final MenuItem selectedBottomNavigationMenuItem = bottomNavigation.getMenu().findItem(selectedBottomNavigationMenuItemId);
        updateActivityContent(selectedBottomNavigationMenuItem);
        bottomNavigation.setSelectedItemId(selectedBottomNavigationMenuItem.getItemId());

        // Imposto il ViewPager
        pager = findViewById(R.id.activity_main_viewpager);
        pagerAdapter = new FragmentSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateActivityContent(bottomNavigation.getMenu().getItem(position));
                bottomNavigation.setSelectedItemId(bottomNavigation.getMenu().getItem(position).getItemId());
                lastPosition = currentPosition;
                currentPosition = position;
                // Nella navigazione tra i fragment voglio che la tastiera sparisca
                KeyboardUtils.hideKeyboard(MainActivity.this);
                /*
                Il family fragment ha un options menu differente, quindi se mi sto spostando in quel
                fragment o provengo da quel fragment l'options menu deve essere cambiato.
                Inoltre i punti non devono essere visualizzati.
                 */
                 if (currentPosition == POSITION_FAMILY || lastPosition == POSITION_FAMILY) {
                    invalidateOptionsMenu();
                 }
                 if (currentPosition == POSITION_FAMILY) {
                     layoutPoints.setVisibility(View.GONE);
                 }
                 else {
                     layoutPoints.setVisibility(View.VISIBLE);
                 }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Quando viene selezionata dal menù nella bottom navigation la stessa sezione in cui si è già
                // il fragment non deve essere ricaricato (non viene fatto nulla).
                    updateActivityContent(menuItem);
                    switch (menuItem.getItemId()) {
                        case R.id.activity_main_bottom_navigation_messages:
                            pager.setCurrentItem(POSITION_MESSAGES, true);
                            break;
                        case R.id.activity_main_bottom_navigation_family:
                            pager.setCurrentItem(POSITION_FAMILY, true);
                            break;
                        case R.id.activity_main_bottom_navigation_todo:
                            pager.setCurrentItem(POSITION_TODO, true);
                            break;
                        case R.id.activity_main_bottom_navigation_done:
                            pager.setCurrentItem(POSITION_DONE, true);
                            break;
                        case R.id.activity_main_bottom_navigation_rewards:
                            pager.setCurrentItem(POSITION_REWARDS, true);
                            break;
                    }
                return true;
            }
        });
    }

    private void readHomeUser() {
        LiveData<List<HomeUser>> rewardLiveData = viewModel.getHomeUserLiveData();
        final String userId = new FirebaseAuth().getCurrentUserUid();
        rewardLiveData.observe(this, new Observer<List<HomeUser>>() {
            @Override
            public void onChanged(List<HomeUser> homeUsers) {
                for(HomeUser homeUser : homeUsers) {
                    if (homeUser.getUserId().equals(userId)) {
                        // Animazione dei punti dal valore precedente a quello corrente
                        int oldPoints = oldPointsValue;
                        int newPoints = homeUser.getPoints();
                        ValueAnimator animator = ValueAnimator.ofInt(oldPoints, newPoints);
                        animator.setDuration(1000);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                userPoints.setText(animation.getAnimatedValue().toString());
                            }
                        });
                        animator.start();
                        oldPointsValue = newPoints;
                        break;
                    }
                }
            }
        });
    }

    /**
     * Metodo per aggiornare il contenuto dell'activity, ovvero la toolbar e il fragment centrale,
     * in base alla voce selezionata nella bottom navigation
     *
     * @param menuItem La voce del menù selezionata nella bottom navigation
     */
    private void updateActivityContent(MenuItem menuItem) {
        toolbar.setTitle(menuItem.getTitle());
        toolbar.setLogo(menuItem.getIcon());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        if (currentPosition == POSITION_FAMILY && Appartment.getInstance().getHomeUser(new FirebaseAuth().getCurrentUserUid()).getRole() == Home.ROLE_OWNER) {
            /*
            Nel family fragment il proprietario della casa visualizza nell'options menù un'icona
            aggiuntiva che gli permette di modificare i dati della casa.
             */
            getMenuInflater().inflate(R.menu.activity_main_toolbar_extended, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.activity_main_toolbar, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // FIXME Aggiungere gli altri pulsanti

            case R.id.activity_main_toolbar_profile: {
                Intent i = new Intent(this, UserProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;
            }

            case R.id.activity_main_toolbar_edit_home_data: {
                Intent i = new Intent(this, CreateHomeActivity.class);
                i.putExtra(CreateHomeActivity.EXTRA_HOME_DATA, Appartment.getInstance().getHome());
                startActivity(i);
                /*
                Questo non è uno startActivityForResult perché qui non devo far nulla con
                eventuali dati che mi vengono restituiti dall'activity chiamata. L'unica cosa che
                deve essere fatta è l'aggiornamento della casa salvata nello stato ma questa
                operazione è già fatta in CreateHomeActivity.
                 */
                return true;
            }

            case R.id.activity_main_toolbar_delete_home: {
                DeleteHomeUserConfirmationDialogFragment dialog = DeleteHomeUserConfirmationDialogFragment.newInstance(R.string.dialog_delete_home_confirmation_message, R.string.general_delete_home_button);
                dialog.show(getSupportFragmentManager(), DeleteHomeUserConfirmationDialogFragment.TAG_CONFIRMATION_DIALOG);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_KEY_SELECTED_BOTTOM_MENU_ITEM, selectedBottomNavigationMenuItemId);
        outState.putInt(BUNDLE_KEY_OLD_POINTS_VALUE, oldPointsValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        oldPointsValue = savedInstanceState.getInt(BUNDLE_KEY_OLD_POINTS_VALUE);
    }

    /**
     * Lascia il backstack inalterato, ma mette tutte le attività in background, esattamente
     * come se l'utente avesse premuto il bottone home
     * 2° RISPOSTA SU:
     * https://stackoverflow.com/questions/8631095/how-to-prevent-going-back-to-the-previous-activity
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onConfirm() {
        /*
        Quando questo metodo è chiamato, il fragment attualmente visualizzato nella MainActivity è
        NECESSARIAMENTE il FamilyFragment (altrimenti l'utente non avrebbe potuto visualizzare il
        bottone nella toolbar che conduce a questo listener).
         */
        ((FamilyFragment) pagerAdapter.getCurrentFragment()).deleteHome();
    }

    private class FragmentSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment currentFragment;

        public FragmentSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getCurrentFragment() {
            return currentFragment;
        }

        /*
        L'override di questo metodo è uno dei vari modi con cui è possibile ottenere il riferimento
        al fragment attualmente visualizzato (serve per l'eliminazione della casa, in cui l'utente
        preme un bottone nella toolbar della MainActivity e dev'essere invocato un metodo del
        FamilyFragment).
        Per alternative vedere https://stackoverflow.com/questions/18609261/getting-the-current-fragment-instance-in-the-viewpager
         */
        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            currentFragment = ((Fragment) object);
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment nextFragment;
            switch(position) {
                case POSITION_MESSAGES:
                    nextFragment = MessagesFragment.newInstance();
                    break;
                case POSITION_FAMILY:
                    nextFragment = FamilyFragment.newInstance();
                    break;
                case POSITION_TODO:
                    nextFragment = TodoFragment.newInstance();
                    break;
                case POSITION_DONE:
                    nextFragment = DoneFragment.newInstance();
                    break;
                case POSITION_REWARDS:
                    nextFragment = RewardsFragment.newInstance();
                    break;
                default:
                    // FIXME ERRORE qui non deve entrare mai
                    nextFragment = null;
            }
            return nextFragment;
        }

        @Override
        public int getCount() {
            return bottomNavigation.getMenu().size();
        }
    }
}
