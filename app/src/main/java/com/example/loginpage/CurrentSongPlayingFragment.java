package com.example.loginpage;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.common.util.IOUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


//import com.google.android.exoplayer2.source.ProgressiveMediaSource;

public class CurrentSongPlayingFragment extends Fragment {

    private ImageView songImage;
    private TextView bandName;
    private TextView songName;
    private TextView currentTime;
    private SeekBar seekBar;
    private MediaPlayer player;
    private Handler handler = new Handler();

    private ImageView playImage;
    private ImageView fwrdImage;
    private ImageView bckdImage;

    private SimpleExoPlayer exoPlayer;

    private View contextView;

    private boolean isPlaying = false;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);

        contextView = view;

        songImage = (ImageView) view.findViewById(R.id.songImage);
        bandName = view.findViewById(R.id.bandName);
        songName = view.findViewById(R.id.songName);
        currentTime = view.findViewById(R.id.currentTime);

        playImage = view.findViewById(R.id.play_button);

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying)
                {
                    startPlayer();
                }else
                {

                }
            }
        });

        return view;
    }
    private void startPlayer()
    {
        String URL = "https://www.youtube.com/watch?v=fahcCePs9O0&list=RDfahcCePs9O0&start_radio=1&ab_channel=Koma";
        MusicService musicService = new MusicService();
        musicService.initPlayer(getContext(),URL);

        Picasso.with(getContext()).load("https://img.youtube.com/vi/"+getImageUrlQuietly(URL)+"/maxresdefault.jpg").into(songImage);
        songName.setText(getTitleQuietly(URL));
    }
    public static String getImageUrlQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                return String.format("http://img.youtube.com/vi/%s/0.jpg", Uri.parse(youtubeUrl).getQueryParameter("v"));
            }
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTitleQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" + youtubeUrl + "&format=json");
                return new JSONObject(IOUtils.toString(embededURL)).getString("title");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getAuthorQuietly(String youtubeUrl) { //Finish this please!!!!!!!!
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" + youtubeUrl + "&format=json");
                return new JSONObject(IOUtils.toString(embededURL)).getString("title");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
