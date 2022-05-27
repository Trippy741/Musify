package com.example.loginpage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.loginpage.R;

public class MediaNotificationHandler {

    public static NotificationManager notificationManagerForClass;
    public static Notification.Builder builderForClass;

    public static PendingIntent pendingIntentForPause;
    public static PendingIntent pendingIntentForPrev;
    public static PendingIntent pendingIntentForNext;

    public static void CreateNotification(Context mContext, Song songPlaying, NotificationManager notifManagerService)
    {
        Intent prevIntent = new Intent(mContext, NotificationReciever.class);
        prevIntent.putExtra("action","ACTION_PREV");
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(mContext,111,prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(mContext, NotificationReciever.class);
        pauseIntent.putExtra("action","ACTION_PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(mContext,112,pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(mContext, NotificationReciever.class);
        nextIntent.putExtra("action","ACTION_NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(mContext,113,nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap iconBitmap = null;

        NotificationManager notificationManager = notifManagerService;

        CharSequence name = mContext.getString(R.string.notification_channel_id);
        String description = mContext.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("musicNotif", name, importance);
        channel.setDescription(description);

        Bitmap bMap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_mail_icon);

        notificationManager.createNotificationChannel(channel);
        Notification.Builder builder = new Notification.Builder(mContext, mContext.getString(R.string.notification_channel_id))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .addAction(R.drawable.exo_controls_previous, "Previous", prevPendingIntent) // #0
                .addAction(R.drawable.exo_controls_pause, "Pause", pausePendingIntent)  // #1
                .addAction(R.drawable.exo_controls_next, "Next", nextPendingIntent)     // #2
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(1 /* #1: pause button */)
                        .setMediaSession(MusicService.getMediaCompatToken()))
                .setContentTitle(songPlaying.song_title)
                .setContentText(songPlaying.artist_title)
                .setLargeIcon(bMap);

        notificationManager.notify(R.string.notification_channel_id , builder.build());

        builderForClass = builder;
        notificationManagerForClass = notificationManager;
        pendingIntentForPause = pausePendingIntent;
        pendingIntentForPrev = prevPendingIntent;
        pendingIntentForNext = nextPendingIntent;
    }
    public static void resumeNotif()
    {
        Notification notif = builderForClass.build();
        builderForClass.setActions(new Notification.Action[]{
                new Notification.Action(R.drawable.exo_controls_previous, "Previous", pendingIntentForPrev), // #0
                new Notification.Action(R.drawable.exo_controls_pause, "Pause", pendingIntentForPause),  // #1
                new Notification.Action(R.drawable.exo_controls_next, "Next", pendingIntentForNext)
        });
        notificationManagerForClass.notify(R.string.notification_channel_id , builderForClass.build());
    }
    public static void pauseNotif()
    {
        Notification notif = builderForClass.build();
        builderForClass.setActions(new Notification.Action[]{
                new Notification.Action(R.drawable.exo_controls_previous, "Previous", pendingIntentForPrev), // #0
                new Notification.Action(R.drawable.exo_controls_play, "Pause", pendingIntentForPause),  // #1
                new Notification.Action(R.drawable.exo_controls_next, "Next", pendingIntentForNext)
        });
        notificationManagerForClass.notify(R.string.notification_channel_id , builderForClass.build());
    }
    public static void prevNotif()
    {

    }
    public static void nextNotif()
    {

    }
}
