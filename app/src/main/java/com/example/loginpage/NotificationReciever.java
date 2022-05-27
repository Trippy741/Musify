package com.example.loginpage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String action=intent.getStringExtra("action");
        Toast.makeText(context,action,Toast.LENGTH_SHORT).show();
        if(action.equals("ACTION_PAUSE")){
            Pause();
        }
        else if(action.equals("ACTION_PREV")){
            Prev(context);

        }
        else if(action.equals("ACTION_NEXT")){
            Next(context);

        }
        //This is used to close the notification tray
        /*Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);*/
    }
    public void Pause(){
        boolean playerState = MusicService.isPlaying;
        if(playerState == true)
        {
            MusicService.pausePlayer();
            MediaNotificationHandler.pauseNotif();
        }
        else
        {
            MusicService.resumePlayer();
            MediaNotificationHandler.resumeNotif();
        }
    }

    public void Prev(Context context){
        Toast.makeText(context,"previous",Toast.LENGTH_SHORT).show();
    }

    public void Next(Context context){
        Toast.makeText(context,"next",Toast.LENGTH_SHORT).show();
    }
}
