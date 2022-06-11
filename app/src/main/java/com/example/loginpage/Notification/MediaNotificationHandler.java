package com.example.loginpage.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.example.loginpage.CustomServices.MusicService;
import com.example.loginpage.R;
import com.example.loginpage.CustomDataTypes.Song;

public class MediaNotificationHandler {

    public static NotificationManager notificationManagerForClass;
    public static NotificationCompat.Builder builderForClass;

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

        if(songPlaying.song_bitmap != null)
            bMap = songPlaying.song_bitmap;

        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, mContext.getString(R.string.notification_channel_id))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_music_note)
                .addAction(R.drawable.exo_controls_previous, "Previous", prevPendingIntent) // #0
                .addAction(R.drawable.exo_controls_pause, "Pause", pausePendingIntent)  // #1
                .addAction(R.drawable.exo_controls_next, "Next", nextPendingIntent)     // #2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
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
    public static void Notify()
    {
        notificationManagerForClass.notify(R.string.notification_channel_id , builderForClass.build());
    }
    public static void resumeNotif()
    {
        Notification notif = builderForClass.build();
        builderForClass.clearActions();

        builderForClass.addAction(R.drawable.exo_controls_previous, "Previous", pendingIntentForPrev);
        builderForClass.addAction(R.drawable.exo_controls_pause, "Pause", pendingIntentForPause);
        builderForClass.addAction(R.drawable.exo_controls_next, "Next", pendingIntentForNext);
        notificationManagerForClass.notify(R.string.notification_channel_id , builderForClass.build());
    }
    public static void pauseNotif()
    {
        Notification notif = builderForClass.build();
        builderForClass.clearActions();

        builderForClass.addAction(R.drawable.exo_controls_previous, "Previous", pendingIntentForPrev);
        builderForClass.addAction(R.drawable.exo_controls_play, "Pause", pendingIntentForPause);
        builderForClass.addAction(R.drawable.exo_controls_next, "Next", pendingIntentForNext);
        notificationManagerForClass.notify(R.string.notification_channel_id , builderForClass.build());
    }
    public static void prevNotif()
    {

    }
    public static void nextNotif()
    {

    }
}
