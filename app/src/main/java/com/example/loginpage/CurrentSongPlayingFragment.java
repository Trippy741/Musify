package com.example.loginpage;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


//import com.google.android.exoplayer2.source.ProgressiveMediaSource;

public class CurrentSongPlayingFragment extends Fragment {

    private MusicService musicService;

    private View contextView;
    private boolean isPlaying = false;

    private int pos = 0;

    private Song songPlaying;
    private ArrayList<Song> songQueue = new ArrayList<Song>();
    private Notification notification;

    private Bitmap songBitmap;

    private ImageView songImage;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);
        contextView = view;


        songImage = view.findViewById(R.id.songImage);

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
            /*if(bundle.get("song_bitmap") != null)
            {
                songBitmap = (Bitmap) bundle.get("song_bitmap");
            }*/
            songBitmap = songPlaying.song_bitmap;
        }
        else
            Toast.makeText(view.getContext(), "Null song argument: No Song Loaded!", Toast.LENGTH_SHORT).show();

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
            songImage.setImageBitmap(songBitmap);
        else
            Picasso.with(view.getContext()).load(songPlaying.image_URL).into(songImage);
        mainTitle.setText(songPlaying.song_title);
        subTitle.setText(songPlaying.artist_title);

        musicService.playSongFromURL(songPlaying.song_URL);

        MediaNotificationHandler.CreateNotification(view.getContext(),songPlaying,requireActivity().getSystemService(NotificationManager.class));

        contextView = view;

        if(songPlaying.image_URL != "")
        {
            new GetImageFromUrlToImg(songImage).execute(songPlaying.image_URL); //Setting the song image
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    Bitmap thumb;
                    // Ur URL
                    String link = songPlaying.image_URL.toString();
                    try {
                        URL url = new URL(link);
                        thumb = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        MediaNotificationHandler.builderForClass.setLargeIcon(thumb);
                        MediaNotificationHandler.Notify();

                    } catch (Exception e) {
                        //Toast here
                    }
                }
            });
            thread.start();
        }



        return view;
    }
    //TODO: MAKE IT SO THAT THE "CURRENT SONG PLAYING" CLASS TAKES IN AN ALBUM OBJECT INSTEAD OF A SINGLE SONG AND ALLOWS THE USER TO SKIP THE SONG
    //TODO: IMPLEMENT EXOPLAYER CACHING SO THAT SONGS LOAD BEFOREHAND
}
 class GetImageFromUrlToImg extends AsyncTask<String, Void, Bitmap>{
    ImageView imageView;
    Bitmap bitmap;
    public GetImageFromUrlToImg(ImageView img){
        this.imageView = img;
    }
    @Override
    protected Bitmap doInBackground(String... url) {
        String stringUrl = url[0];
        bitmap = null;
        InputStream inputStream;
        try {
            inputStream = new java.net.URL(stringUrl).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap){
        super.onPostExecute(bitmap);
        imageView.setImageBitmap(bitmap);
    }
}

