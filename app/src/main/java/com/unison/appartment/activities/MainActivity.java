package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.fragments.DoneFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.services.AppartmentService;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.fragments.MessagesFragment;
import com.unison.appartment.R;
import com.unison.appartment.fragments.RewardsFragment;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.state.Appartment;

/**
 * Classe che rappresenta l'Activity principale di una Home
 */
public class MainActivity extends AppCompatActivity {

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

    private int selectedBottomNavigationMenuItemId;

    private Toolbar toolbar;
    private Menu optionsMenu;
    private BottomNavigationView bottomNavigation;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

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
        pager = findViewById(R.id.activity_main_fragment_container);
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
                /*
                Il family fragment ha un options menu differente, quindi se mi sto spostando in quel
                fragment o provengo da quel fragment l'options menu deve essere cambiato.
                 */
                 if (currentPosition == POSITION_FAMILY || lastPosition == POSITION_FAMILY) {
                    invalidateOptionsMenu();
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

                // Fermo il servizio che mantiene aggiornato lo stato
                Intent intent = new Intent(this, AppartmentService.class);
                stopService(intent);
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_KEY_SELECTED_BOTTOM_MENU_ITEM, selectedBottomNavigationMenuItemId);
        super.onSaveInstanceState(outState);
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

    private class FragmentSlidePagerAdapter extends FragmentStatePagerAdapter {
        public FragmentSlidePagerAdapter(FragmentManager fm) {
            super(fm);
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
