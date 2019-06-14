package com.unison.appartment.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
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
import com.unison.appartment.state.MyApplication;

import java.util.Date;

public class NotificationService extends Service {

    private Query postsRef;
    private ChildEventListener postsListener;
    private final static String POST_CHANNEL_ID = "Posts";
    private final static String POST_CHANNEL_NAME = "Posts";

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Creazione dei canali per le notifiche
        // (necessario per / utilizzato esclusivamente da: Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(POST_CHANNEL_ID, POST_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        }

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

                    // Intent per l'activity che si vuole far partire al tap sulla notifica
                    Intent resultIntent = new Intent(NotificationService.this, MainActivity.class);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Extra utilizzati dall'activity che viene fatta partire al tap sulla notifica

                    //TODO mettere extra per andare nel fragment della bacheca
//                resultIntent.putExtra(TaskDetailActivity.EXTRA_TASK_OBJECT, task);

                    // Creazione dell'oggetto TaskStackBuilder, utilizzato per creare il backstack
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotificationService.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    // PendingIntent che contiene l'intero backstack
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    // TODO switchare sul tipo di post e creare notifiche diverse a seconda del tipo di messaggio
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, POST_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_message)
                            .setContentTitle(getString(R.string.notification_new_post_title))
                            .setContentText(getString(R.string.notification_new_post_content, post.getAuthor()))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(Html.fromHtml(getString(R.string.notification_new_post_extended, post.getAuthor(), post.getContent()))))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(resultPendingIntent)
                            .setAutoCancel(true)
                            .setOnlyAlertOnce(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationService.this);
                    int notificationId = (int)(new Date().getTime() % Integer.MAX_VALUE);
                    notificationManager.notify(notificationId, builder.build());
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
        super.onDestroy();
        if (postsRef != null && postsListener != null) {
            postsRef.removeEventListener(postsListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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
}
