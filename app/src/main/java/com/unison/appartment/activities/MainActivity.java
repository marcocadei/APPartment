package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseError;
import com.unison.appartment.database.DatabaseReader;
import com.unison.appartment.database.DatabaseReaderListener;
import com.unison.appartment.database.FirebaseAuth;
import com.unison.appartment.database.FirebaseDatabaseReader;
import com.unison.appartment.fragments.DeleteHomeUserConfirmationDialogFragment;
import com.unison.appartment.fragments.DoneFragment;
import com.unison.appartment.fragments.FirebaseErrorDialogFragment;
import com.unison.appartment.model.Home;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.services.AppartmentService;
import com.unison.appartment.fragments.FamilyFragment;
import com.unison.appartment.fragments.MessagesFragment;
import com.unison.appartment.R;
import com.unison.appartment.fragments.RewardsFragment;
import com.unison.appartment.fragments.TodoFragment;
import com.unison.appartment.services.NotificationService;
import com.unison.appartment.state.Appartment;
import com.unison.appartment.utils.KeyboardUtils;
import com.unison.appartment.viewmodel.HomeUserViewModel;

import java.util.List;
import java.util.Map;

/**
 * Classe che rappresenta l'Activity principale di una Home
 */
public class MainActivity extends ActivityWithNetworkConnectionDialog implements DeleteHomeUserConfirmationDialogFragment.ConfirmationDialogInterface {

    public static final String EXTRA_DESTINATION_FRAGMENT = "destinationFragment";

    /*
    Costanti che indicano la posizione delle varie sezioni così come ordinate nella bottom
    navigation; sono usate per le animazioni di ingresso/uscita al cambio fragment.
     */
    public static final byte POSITION_MESSAGES = 0;
    public static final byte POSITION_FAMILY = 1;
    public static final byte POSITION_TODO = 2;
    public static final byte POSITION_DONE = 3;
    public static final byte POSITION_REWARDS = 4;

    // Ultima voce selezionata nella bottom navigation
    private int lastPosition = POSITION_MESSAGES;
    // Voce attualmente selezionata nella bottom navigation
    private int currentPosition = POSITION_MESSAGES;

    private static final String BUNDLE_KEY_SELECTED_BOTTOM_MENU_ITEM = "selectedBottomMenuItem";
    private static final String BUNDLE_KEY_OLD_POINTS_VALUE = "oldPointsValue";

    private int selectedBottomNavigationMenuItemId;
    private int oldPointsValue = 0;

    private Toolbar toolbar;
    private TextView userPoints;
    private HomeUserViewModel viewModel;
    private BottomNavigationView bottomNavigation;
    private ViewPager pager;
    private FragmentSlidePagerAdapter pagerAdapter;

    private Messenger serviceMessenger = null;
    private boolean serviceBound;

    private Bundle savedInstanceState;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /*
        Per gestire il fatto che l'utente potrebbe essere stato kickato qui conviene solamente
        settare l'intent e poi gestirlo in onResume() (che è il metodo invocato immediatamente dopo)
        con getIntent().
        Se metto qui pager.setCurrentItem(..) viene invocato subito l'adapter e l'app crasha
         */
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.savedInstanceState = savedInstanceState;

        if (Appartment.getInstance().getHome() != null) {
            // Se l'utente è uscito dall'app mentre era in una casa: vado direttamente alla
            // MainActivity di quella casa
            new FirebaseDatabaseReader().retrieveHomeUsers(Appartment.getInstance().getHome().getName(), databaseReaderListener);
        }
    }

    protected void initialize(Bundle savedInstanceState) {
        // TODO se sono arrivato qui da una notifica però ero ad es. già uscito da una casa,
        // devo fare un controllo e nel caso tornare indietro per evitare errori e crash

        // Avvio il servizio che mantiene aggiornato lo stato
        Intent appartmentServiceIntent = new Intent(this, AppartmentService.class);
        startService(appartmentServiceIntent);

        // Avvio il servizio che gestisce le notifiche
        Intent notificationServiceIntent = new Intent(this, NotificationService.class);
        startService(notificationServiceIntent);

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

        /*
        Se sono arrivato alla MainActivity schiacciando su una notifica, nell'intent è contenuta
        l'indicazione del fragment che deve essere visualizzato all'apertura dell'activity.
        (Questo è lo stesso codice utilizzato in onNewIntent, in teoria onNewIntent e onCreate
        dovrebbero essere mutuamente esclusivi).
         */
        int destinationFragment = getIntent().getByteExtra(EXTRA_DESTINATION_FRAGMENT, (byte) -1);
        if (destinationFragment != -1) {
            pager.setCurrentItem(destinationFragment);
        }
    }

    private void readHomeUser() {
        LiveData<List<HomeUser>> rewardLiveData = viewModel.getHomeUserLiveData();
        final String userId = new FirebaseAuth().getCurrentUserUid();
        rewardLiveData.observe(this, new Observer<List<HomeUser>>() {
            @Override
            public void onChanged(List<HomeUser> homeUsers) {
                for(HomeUser homeUser : homeUsers) {
                    if (homeUser.getUserId().equals(userId)) {
                        if (userPoints.getVisibility() != View.GONE) {
                            if (homeUser.getPoints() >= HomeUser.MAX_POINTS) {
                                userPoints.setText(R.string.general_max_points);
                                oldPointsValue = HomeUser.MAX_POINTS;
                            }
                            else {
                                // Animazione dei punti dal valore precedente a quello corrente
                                /*int oldPoints = oldPointsValue;
                                int newPoints = homeUser.getPoints();
                                ValueAnimator animator = ValueAnimator.ofInt(oldPoints, newPoints);
                                animator.setDuration(2000);
                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        userPoints.setText(animation.getAnimatedValue().toString());
                                    }
                                });
                                animator.start();*/
                                userPoints.setText(String.valueOf(homeUser.getPoints()));
                                oldPointsValue = homeUser.getPoints();
                            }
                            break;
                        }
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

    @Override
    protected void onResume() {
        super.onResume();

        /*
        Se sono arrivato alla MainActivity schiacciando su una notifica, nell'intent è contenuta
        l'indicazione del fragment che deve essere visualizzato all'apertura dell'activity.
         */
        if (getIntent() != null) {
            int destinationFragment = getIntent().getByteExtra(EXTRA_DESTINATION_FRAGMENT, (byte) -1);
            if (destinationFragment != -1) {
                pager.setCurrentItem(destinationFragment);
            }
        } else {
            setCurrentScreen(currentPosition);
        }
    }

    @Override
    protected void onPause() {
        Appartment.getInstance().setCurrentScreen(Appartment.SCREEN_ANYTHING_ELSE);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, NotificationService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
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
            if (currentFragment == null || !currentFragment.equals(object)) {
                // Salvo il riferimento al fragment attualmente visualizzato
                currentFragment = ((Fragment) object);

                // Salvo nello stato la posizione corrente (che verrà utilizzata dal NotificationService
                // per sapere quali notifiche mostrare) e se necessario invio al NotificationService un
                // messaggio nel caso in cui quest'ultimo debba eseguire qualche azione particolare.
                setCurrentScreen(position);
            }
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

    private void setCurrentScreen(int position) {
        switch (position) {
            case POSITION_MESSAGES:
                Appartment.getInstance().setCurrentScreen(Appartment.SCREEN_MESSAGES);
                sendMessageToNotificationService(NotificationService.MSG_CLEAR_POSTS_NOTIFICATIONS);
                break;
            case POSITION_FAMILY:
                Appartment.getInstance().setCurrentScreen(Appartment.SCREEN_FAMILY);
                sendMessageToNotificationService(NotificationService.MSG_CLEAR_USER_INFO_NOTIFICATIONS);
                break;
            case POSITION_TODO:
                Appartment.getInstance().setCurrentScreen(Appartment.SCREEN_TODO);
                sendMessageToNotificationService(NotificationService.MSG_CLEAR_TASKS_NOTIFICATIONS);
                break;
            case POSITION_REWARDS:
                Appartment.getInstance().setCurrentScreen(Appartment.SCREEN_REWARDS);
                sendMessageToNotificationService(NotificationService.MSG_CLEAR_REWARDS_NOTIFICATIONS);
                break;
            default:
                Appartment.getInstance().setCurrentScreen(Appartment.SCREEN_ANYTHING_ELSE);
                break;
        }
    }

    /**
     * Classe utilizzata per interagire con il NotificationService
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            serviceMessenger = new Messenger(service);
            serviceBound = true;

            /*
            Appena il servizio viene collegato all'activity, controllo in quale fragment sono,
            aggiorno lo stato e se necessario mando un messaggio al servizio.
            NOTA IMPORTANTE: Questa chiamata viene fatta anche nel metodo onResume, ma quando questo
            viene eseguito molto probabilmente il servizio non è ancora bindato e quindi il messaggio
            non viene inviato!
             */
            setCurrentScreen(currentPosition);
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            serviceMessenger = null;
            serviceBound = false;
        }
    };

    private void sendMessageToNotificationService(int type) {
        sendMessageToNotificationService(type, null);
    }

    private void sendMessageToNotificationService(int type, Bundle data) {
        if (!serviceBound) return;

        Message msg = Message.obtain(null, type);
        if (data != null) {
            msg.setData(data);
        }

        try {
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    final DatabaseReaderListener databaseReaderListener = new DatabaseReaderListener() {
        // Se l'utente è stato eliminato dalla casa allora vado alla UserProfileActivity
        @Override
        public void onReadSuccess(String key, Object object) {
            Map<String, HomeUser> homeUsers = (Map<String, HomeUser>) object;
            if (homeUsers.get(new FirebaseAuth().getCurrentUserUid()) == null) {
                Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra(UserProfileActivity.EXTRA_SNACKBAR_MESSAGE, getString(R.string.snackbar_user_kicked_message));
                startActivity(i);
                finish();
            } else {
                initialize(savedInstanceState);
            }
        }
        // Se la casa è stata eliminata vado alla UserProfileActivity
        @Override
        public void onReadEmpty() {
            Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.putExtra(UserProfileActivity.EXTRA_SNACKBAR_MESSAGE, getString(R.string.snackbar_home_deleted_message));
            startActivity(i);
            finish();
        }

        @Override
        public void onReadCancelled(DatabaseError databaseError) {
            /*
            onCancelled viene invocato solo se si verifica un errore a lato server oppure se
            le regole di sicurezza impostate in Firebase non permettono l'operazione richiesta.
            In questo caso perciò viene visualizzato un messaggio di errore generico, dato che
            la situazione non può essere risolta dall'utente.
            */
            FirebaseErrorDialogFragment dialog = new FirebaseErrorDialogFragment();
            dialog.show(getSupportFragmentManager(), FirebaseErrorDialogFragment.TAG_FIREBASE_ERROR_DIALOG);
        }
    };
}
