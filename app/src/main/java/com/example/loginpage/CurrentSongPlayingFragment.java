package com.example.loginpage;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.TimeBar;
import com.squareup.picasso.Picasso;


//import com.google.android.exoplayer2.source.ProgressiveMediaSource;

public class CurrentSongPlayingFragment extends Fragment {

    private SeekBar seekBar;
    private final Handler handler = new Handler();

    private ImageView playButton;
    private ImageView fwrdImage;
    private ImageView bckdImage;

    private MusicService musicService;
    private TimeBar timeBar;

    private View contextView;
    private final boolean isPlaying = false;

    private Song songPlaying;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);

        musicService = new MusicService(view.getContext()); //Init Music service
        contextView = view;

        ImageView songImage = view.findViewById(R.id.songImage);
        TextView timeLeft = view.findViewById(R.id.timeLeft);
        TextView currentTime = view.findViewById(R.id.currentTime);
        //seekBar = view.findViewById(R.id.seekbar);
        timeBar = view.findViewById(R.id.song_playing_timeBar);

        TextView mainTitle = view.findViewById(R.id.song_playing_mainTitle);
        TextView subTitle = view.findViewById(R.id.song_playing_subTitle);

        if(getArguments() != null)
        {
            Bundle bundle = getArguments();
            songPlaying = (Song) bundle.get("song_obj");
        }
        else
            Toast.makeText(view.getContext(), "Null song argument", Toast.LENGTH_SHORT).show();

        Picasso.with(view.getContext()).load(songPlaying.image_URL).into(songImage);
        mainTitle.setText(songPlaying.song_title);
        subTitle.setText(songPlaying.artist_title);

        musicService.playFromURL(songPlaying.song_URL);

        view.findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicService.isPlaying)
                {
                    musicService.stopPlayer();
                }else
                {
                    musicService.resumePlayer();
                }
            }
        });

        timeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {

            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                musicService.getPlayer().seekTo(position);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {

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
}
