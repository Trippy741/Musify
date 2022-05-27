package com.example.loginpage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


//import com.google.android.exoplayer2.source.ProgressiveMediaSource;

public class CurrentSongPlayingFragment extends Fragment {

    private MusicService musicService;

    private View contextView;
    private boolean isPlaying = false;

    private int pos = 0;

    private Song songPlaying;
    private ArrayList<Song> songQueue = new ArrayList<Song>();
    private Notification notification;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);
        contextView = view;


        ImageView songImage = view.findViewById(R.id.songImage);

        TextView mainTitle = view.findViewById(R.id.song_playing_mainTitle);
        TextView subTitle = view.findViewById(R.id.song_playing_subTitle);

        if(getArguments() != null)
        {
            Bundle bundle = getArguments();
            songPlaying = (Song) bundle.get("song_obj");
            if(bundle.get("album_obj") != null)
            {
                songQueue = ((Album) bundle.get("album_obj")).songs;
                if(bundle.get("song_index") != null)
                {
                    pos = (int)bundle.get("song_index");
                    songPlaying = songQueue.get(pos);
                }
            }

        }
        else
            Toast.makeText(view.getContext(), "Null song argument", Toast.LENGTH_SHORT).show();

        PlayerView playerView = view.findViewById(R.id.video_view);
        musicService = new MusicService(view.getContext(),playerView,songQueue); //Init Music service

        playerView.findViewById(R.id.exo_rew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Back", Toast.LENGTH_SHORT).show();
                if(pos > 0)
                {
                    musicService.playSongFromURL(songQueue.get(pos-1).song_URL);
                }
            }
        });
        ImageButton b1 =playerView.findViewById(R.id.exo_ffwd);


        //TODO: COME BACK TO THIS LATER TO FIX THE FAST FORWARD BUTTONS
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Forward", Toast.LENGTH_SHORT).show();
                if(pos < songQueue.size())
                {
                    musicService.playSongFromURL(songQueue.get(pos+1).song_URL);
                }
            }
        });

        ImageButton b2 =playerView.findViewById(R.id.exo_rew);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Back", Toast.LENGTH_SHORT).show();
                if(pos > 0)
                {
                    musicService.playSongFromURL(songQueue.get(pos-1).song_URL);
                }
            }
        });

        playerView.setControllerHideOnTouch(false);
        playerView.setControllerAutoShow(true);

        playerView.setPlayer(musicService.getPlayer());

        if(songPlaying.image_URL.isEmpty())
            songImage.setImageURI(Uri.parse(songPlaying.image_uri));
        else
            Picasso.with(view.getContext()).load(songPlaying.image_URL).into(songImage);
        mainTitle.setText(songPlaying.song_title);
        subTitle.setText(songPlaying.artist_title);

        musicService.playSongFromURL(songPlaying.song_URL);

        contextView = view;

        //Creating the Notification Channel

        Intent prevIntent = new Intent(view.getContext(), NotificationReciever.class);
        prevIntent.putExtra("action","ACTION_PREV");
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(view.getContext(),111,prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(view.getContext(), NotificationReciever.class);
        pauseIntent.putExtra("action","ACTION_PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(view.getContext(),112,pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(view.getContext(), NotificationReciever.class);
        nextIntent.putExtra("action","ACTION_NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(view.getContext(),113,nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap iconBitmap = null;

        /*if(songPlaying.image_uri != null && songPlaying.image_uri != "")
        {
            if(Uri.parse(songPlaying.image_uri) != Uri.EMPTY)
            {
                try {
                    iconBitmap = Picasso.with(view.getContext()).load(songPlaying.image_uri).get(); //TODO: FIX THIS LINE BECUASE THAT'S NOT YOU'RE SUPPOSED TO LOAD THE IMAGE URIS
                } catch (IOException e) {
                    Toast.makeText(view.getContext(), "Failed to set Notification large image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }else
        {

        }*/

/*        try {
            URL url = new URL(songPlaying.image_URL);
            iconBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            Toast.makeText(view.getContext(), "Failed fetching song image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        /*try {

        }catch (Exception e)
        {
            Toast.makeText(view.getContext(), "Failed to create Notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/

        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);

        CharSequence name = getString(R.string.notification_channel_id);
        String description = getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("musicNotif", name, importance);
        channel.setDescription(description);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mail_icon);

        notificationManager.createNotificationChannel(channel);
        Notification.Builder builder = new Notification.Builder(view.getContext(), getString(R.string.notification_channel_id))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .addAction(R.drawable.exo_controls_previous, "Previous", prevPendingIntent) // #0
                .addAction(R.drawable.exo_controls_pause, "Pause", pausePendingIntent)  // #1
                .addAction(R.drawable.exo_controls_next, "Next", nextPendingIntent)     // #2
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(1 /* #1: pause button */)
                        .setMediaSession(musicService.getMediaCompatToken()))
                .setContentTitle(songPlaying.song_title)
                .setContentText(songPlaying.artist_title)
                .setLargeIcon(bMap);

        notificationManager.notify(R.string.notification_channel_id , builder.build());
        return view;
    }
    //TODO: FIX THE IMAGE UPLOAD AND DOWNLOAD THROUGH FIREBASE
    //TODO: MAKE IT SO THAT THE "CURRENT SONG PLAYING" CLASS TAKES IN AN ALBUM OBJECT INSTEAD OF A SINGLE SONG AND ALLOWS THE USER TO SKIP THE SONG
    //TODO: IMPLEMENT EXOPLAYER CACHING SO THAT SONGS LOAD BEFOREHAND

}
