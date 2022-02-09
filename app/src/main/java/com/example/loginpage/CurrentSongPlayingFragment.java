package com.example.loginpage;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;


//import com.google.android.exoplayer2.source.ProgressiveMediaSource;

public class CurrentSongPlayingFragment extends Fragment {

    private ImageView songImage;
    private TextView bandName;
    private TextView songName;
    private TextView currentTime;
    private TextView timeLeft;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    private ImageView playButton;
    private ImageView fwrdImage;
    private ImageView bckdImage;

    private MusicService musicService;

    private View contextView;
    private final boolean isPlaying = false;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);

        musicService = new MusicService(); //Init Music service


        contextView = view;

        songImage = view.findViewById(R.id.songImage);
        timeLeft = view.findViewById(R.id.timeLeft);
        currentTime = view.findViewById(R.id.currentTime);
        seekBar = view.findViewById(R.id.seekbar);

        view.findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                {
                    musicService.stopPlayer();
                }else
                {
                    startPlayer();
                }
            }
        });

        return view;
    }
    private void updateProgressBar(SimpleExoPlayer player) {
        long duration = player == null ? 0 : player.getDuration();
        long position = player == null ? 0 : player.getCurrentPosition();

        if (!seekBar.isPressed()) {
            seekBar.setProgress((int)(position/ duration));
            Log.d("SEEKBAR","PROGRESS: " + (int)(position/ duration));
        }
        long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
        seekBar.setSecondaryProgress((int)(position/ duration));
        // Remove scheduled updates.
        handler.removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgressBar(musicService.getPlayer());
        }
    };
    private void startPlayer()
    {
        int trackLength = 197000;
        String URL = "https://www.youtube.com/watch?v=fahcCePs9O0&list=RDfahcCePs9O0&start_radio=1&ab_channel=Koma";

        musicService.initPlayer(getContext(),URL);

        Picasso.with(getContext()).load(getImageUrlQuietly(URL)).into(songImage);
        //songName.setText(getTitleQuietly(URL));
        timeLeft.setText("/ " + (trackLength / 60000) + ":" + (trackLength % 60000) / 1000);
        /*seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                int seekVal;
                seekVal = trackLength / seekBar.getProgress();
                musicService.setPlayerPosition(seekVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int seekVal;
                seekVal = trackLength / seekBar.getProgress();
                musicService.setPlayerPosition(seekVal);
            }
        });*/

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateProgressBar(musicService.getPlayer());
            }
        }, 0, 1000);

        //timer.cancel();//stop the timer

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
}
