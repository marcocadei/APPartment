package com.unison.appartment.services;

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

import java.util.Date;
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
    private int newMessages = 1;

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
                    Nota importante: Siccome il riferimento dall'oggetto currentlyDisplayedNotifications
                    è rimosso solo quando l'utente accede alla bacheca, e rimane invece memorizzato
                    se per esempio l'utente cancella la notifica senza cliccarci sopra, il testo
                    conterrà sempre il numero di messaggi non letti dall'ultimo accesso in bacheca.
                     */
                    boolean messageNotificationAlreadyDispatched = currentlyDisplayedNotifications.get(POST_CHANNEL_ID).size() != 0;
                    int notificationId = messageNotificationAlreadyDispatched
                            ? currentlyDisplayedNotifications.get(POST_CHANNEL_ID).get(0)
                            : ((int) (SystemClock.uptimeMillis() * 10)) + POST_CHANNEL_NOTIFICATIONS_ID_UNIT;
                    String notificationTitle = messageNotificationAlreadyDispatched
                            ? getString(R.string.notification_new_posts_title)
                            : getString(R.string.notification_new_post_title);
                    String notificationContent = messageNotificationAlreadyDispatched
                            ? getString(R.string.notification_new_posts_content, ++newMessages)
                            : getString(R.string.notification_new_post_content, post.getAuthor());

                    // Intent per l'activity che si vuole far partire al tap sulla notifica
                    Intent resultIntent = new Intent(NotificationService.this, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    // Extra utilizzati dall'activity che viene fatta partire al tap sulla notifica
                    resultIntent.putExtra(MainActivity.EXTRA_DESTINATION_FRAGMENT, MainActivity.POSITION_MESSAGES);

                    // Creazione dell'oggetto TaskStackBuilder, utilizzato per creare il backstack
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotificationService.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    // PendingIntent che contiene l'intero backstack
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    // TODO switchare sul tipo di post e creare notifiche diverse a seconda del tipo di messaggio
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, POST_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_message)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationContent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(true);

                    if (!messageNotificationAlreadyDispatched) {
                        builder.setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(Html.fromHtml(getString(R.string.notification_new_post_extended, post.getAuthor(), post.getContent()))));
                        newMessages = 1;
                    }

                    notificationManager.notify(NOTIFICATIONS_TAG, notificationId, builder.build());
                    currentlyDisplayedNotifications.get(POST_CHANNEL_ID).add(notificationId);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        // TODO qui per consistenza si dovrebbero rimuovere tutte le notifiche currently shown

        // TODO codice scopiazzato da telegram - togliere se non serve
//        Intent intent = new Intent("com.unison.appartment.start");
//        sendBroadcast(intent);
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
