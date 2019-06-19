package com.unison.appartment.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.text.Html;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.unison.appartment.R;
import com.unison.appartment.activities.MainActivity;
import com.unison.appartment.database.DatabaseConstants;
import com.unison.appartment.model.Post;
import com.unison.appartment.state.Appartment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NotificationService extends Service {

    private Query postsRef;
    private ChildEventListener postsListener;

    private final static String NOTIFICATIONS_TAG = "Appartment";

    // Costanti utilizzate per la creazione dei notification channels
    private final static String POST_CHANNEL_ID = "Posts";
    private final static String POST_CHANNEL_NAME = "Posts";

    // Costanti utilizzate per gestire gli id delle notifiche
    private final static int POST_CHANNEL_NOTIFICATIONS_ID_UNIT = 1;

    // Struttura dati utilizzata per memorizzare gli id delle notifiche attualmente visualizzate per ogni canale
    private Map<String, List<Integer>> currentlyDisplayedNotifications;

    // Altri dati utilizzati nel contenuto delle notifiche
    private int newMessages = 0;

    private NotificationManagerCompat notificationManager;

    // Costanti utilizzate per la gestione dei messaggi ricevuti da activity/fragments
    public final static int MSG_CLEAR_POSTS_NOTIFICATIONS = 1;

    private Messenger messenger;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = NotificationManagerCompat.from(this);

        currentlyDisplayedNotifications = new HashMap<>();

        // Creazione dei canali per le notifiche
        // (necessario per / utilizzato esclusivamente da: Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(POST_CHANNEL_ID, POST_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        }
        currentlyDisplayedNotifications.put(POST_CHANNEL_ID, new LinkedList<Integer>());

        listenPosts();
    }

    private void listenPosts() {
        /*
        È necessario mantenere l'oggetto casa continuamente aggiornato perché il "name" e il "conversionFactor"
        sono utilizzati all'interno della MainActivity, ma possono essere cambiati da un altro utente
         */
        postsRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference(DatabaseConstants.POSTS + DatabaseConstants.SEPARATOR +
                Appartment.getInstance().getHome().getName()).orderByChild(DatabaseConstants.POSTS_HOMENAME_POSTID_TIMESTAMP).endAt(-1 * System.currentTimeMillis());
        postsListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);

                // Non mostro la notifica se sono nel messages fragment
                // (Include anche il controllo: non mostro la notifica se il nuovo messaggio è il mio
                // in quanto per scrivere un nuovo messaggio devo necessariamente essere nel messages
                // fragment)
                if (Appartment.getInstance().getCurrentScreen() != Appartment.SCREEN_MESSAGES) {
                    /*
                    Se c'è già una notifica relativa alla bacheca visualizzata, ne modifico il testo
                    in modo che contenga anche il numero di nuovi messaggi.
                    Nota importante: Siccome il riferimento all'oggetto currentlyDisplayedNotifications
                    è rimosso solo quando l'utente accede alla bacheca, e rimane invece memorizzato
                    se per esempio l'utente cancella la notifica senza cliccarci sopra, il testo
                    conterrà sempre il numero di messaggi non letti dall'ultimo accesso in bacheca.
                     */
                    boolean messageNotificationAlreadyDispatched = currentlyDisplayedNotifications.get(POST_CHANNEL_ID).size() != 0;
                    newMessages = messageNotificationAlreadyDispatched
                            ? newMessages + 1
                            : 1;
                    int notificationId = messageNotificationAlreadyDispatched
                            ? currentlyDisplayedNotifications.get(POST_CHANNEL_ID).get(0)
                            : ((int) (SystemClock.uptimeMillis() * 10)) + POST_CHANNEL_NOTIFICATIONS_ID_UNIT;
                    String notificationTitle = getResources().getQuantityString(R.plurals.notification_new_posts_title, newMessages);
                    String notificationContent = messageNotificationAlreadyDispatched
                            ? getResources().getQuantityString(R.plurals.notification_new_posts_content, newMessages, newMessages)
                            : getString(R.string.notification_new_post_content, post.getAuthor());

                    // Intent per l'activity che si vuole far partire al tap sulla notifica
                    Intent resultIntent = new Intent(NotificationService.this, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    // Extra utilizzati dall'activity che viene fatta partire al tap sulla notifica
                    resultIntent.putExtra(MainActivity.EXTRA_DESTINATION_FRAGMENT, MainActivity.POSITION_MESSAGES);

                    CharSequence bigText = "";
                    switch (post.getType()) {
                        case Post.TEXT_POST:
                            bigText = Html.fromHtml(getString(R.string.notification_new_post_extended_text, post.getAuthor(), post.getContent()));
                            break;
                        case Post.IMAGE_POST:
                            bigText = Html.fromHtml(getString(R.string.notification_new_post_extended_image, post.getAuthor()));
                            break;
                        case Post.AUDIO_POST:
                            bigText = Html.fromHtml(getString(R.string.notification_new_post_extended_audio, post.getAuthor()));
                            break;
                    }

                    notificationManager.notify(NOTIFICATIONS_TAG, notificationId, buildTextNotification(
                            resultIntent,
                            POST_CHANNEL_ID,
                            R.drawable.ic_message,
                            notificationTitle,
                            notificationContent,
                            NotificationCompat.PRIORITY_DEFAULT,
                            !messageNotificationAlreadyDispatched,
                            bigText
                    ));

                    if (!messageNotificationAlreadyDispatched) {
                        currentlyDisplayedNotifications.get(POST_CHANNEL_ID).add(notificationId);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*
                Qui non viene fatto nulla perché nell'app non è possibile modificare i messaggi;
                questo callback non verrà mai chiamato!
                 */
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Faccio qualcosa solo se c'è una notifica attualmente mostrata
                if (currentlyDisplayedNotifications.get(POST_CHANNEL_ID).size() != 0) {
                    newMessages--;
                    // Se il numero di nuovi messaggi scende a zero, cancello del tutto la notifica
                    if (newMessages == 0) {
                        for (Integer notificationId : currentlyDisplayedNotifications.get(POST_CHANNEL_ID)) {
                            notificationManager.cancel(NOTIFICATIONS_TAG, notificationId);
                        }
                        currentlyDisplayedNotifications.get(POST_CHANNEL_ID).clear();
                    }
                    else {
                        // Non mostro la notifica se sono nel messages fragment (vedi onChildAdded)
                        if (Appartment.getInstance().getCurrentScreen() != Appartment.SCREEN_MESSAGES) {
                            /*
                            Modifico il testo della notifica già visualizzata decrementando 1 dal numero
                            di nuovi messaggi visualizzato.
                             */

                            // Intent per l'activity che si vuole far partire al tap sulla notifica
                            Intent resultIntent = new Intent(NotificationService.this, MainActivity.class);
                            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            // Extra utilizzati dall'activity che viene fatta partire al tap sulla notifica
                            resultIntent.putExtra(MainActivity.EXTRA_DESTINATION_FRAGMENT, MainActivity.POSITION_MESSAGES);

                            int notificationId = currentlyDisplayedNotifications.get(POST_CHANNEL_ID).get(0);
                            notificationManager.notify(NOTIFICATIONS_TAG, notificationId, buildTextNotification(
                                    resultIntent,
                                    POST_CHANNEL_ID,
                                    R.drawable.ic_message,
                                    getResources().getQuantityString(R.plurals.notification_new_posts_title, newMessages),
                                    getResources().getQuantityString(R.plurals.notification_new_posts_content, newMessages, newMessages),
                                    NotificationCompat.PRIORITY_DEFAULT,
                                    false,
                                    ""
                            ));
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*
                Qui non viene fatto nulla perché nell'app non è possibile modificare i messaggi;
                questo callback non verrà mai chiamato!
                 */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO gestire errore
            }
        };
        postsRef.addChildEventListener(postsListener);
    }

    /*
    Questo consente di far ripartire il servizio nel caso in cui sia ucciso dal sistema
    https://stackoverflow.com/questions/45005648/how-to-restart-android-service-if-killed
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (postsRef != null && postsListener != null) {
            postsRef.removeEventListener(postsListener);
        }

        // TODO rivedere se va bene come cosa - in caso toglierla
        /*
        Alla distruzione del NotificationService vengono rimosse tutte le notifiche mostrate in
        quel momento (altrimenti non ci sarebbe più modo di manipolarle dato che comunque il
        riferimento agli id verrebbe perduto). La distruzione del servizio avviene:
        - se il servizio stesso viene fermato ESPLICITAMENTE dall'utente (nei casi in cui l'intero
        processo viene rimosso dal sistema alla chiusura dell'app, il servizio dovrebbe stopparsi
        SENZA che il metodo onDestroy sia invocato - don't ask why);
        - quando l'utente esce da una casa e ritorna alla UserProfileActivity (in quanto in quel
        caso è invocato stopService).
        FIXME non è più vero se si sceglie di salvare gli id nelle shared preferences, cambiare di conseguenza!
         */
        for (List<Integer> notificationList : currentlyDisplayedNotifications.values()) {
            for (Integer notificationId : notificationList) {
                notificationManager.cancel(NOTIFICATIONS_TAG, notificationId);
            }
        }
        currentlyDisplayedNotifications.clear();
        newMessages = 0;

        // TODO codice scopiazzato da telegram - togliere se non serve
//        Intent intent = new Intent("com.unison.appartment.start");
//        sendBroadcast(intent);
    }

    private Notification buildTextNotification(Intent resultIntent, String channelId,
                                               @DrawableRes int iconDrawable, String notificationTitle,
                                               String notificationContent, int priority,
                                               boolean showExtended, CharSequence bigText) {
        // Creazione dell'oggetto TaskStackBuilder, utilizzato per creare il backstack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // PendingIntent che contiene l'intero backstack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(iconDrawable)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setPriority(priority)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true);

        if (showExtended) {
            builder = builder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigText));
        }

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    private class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLEAR_POSTS_NOTIFICATIONS:
                    try {
                        for (Integer notificationId : currentlyDisplayedNotifications.get(POST_CHANNEL_ID)) {
                            notificationManager.cancel(NOTIFICATIONS_TAG, notificationId);
                        }
                        currentlyDisplayedNotifications.get(POST_CHANNEL_ID).clear();
                        newMessages = 0;
                    }
                    catch (NullPointerException e) {
                        currentlyDisplayedNotifications.put(POST_CHANNEL_ID, new LinkedList<Integer>());
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        messenger = new Messenger(new IncomingHandler(this));
        return messenger.getBinder();
    }

}
