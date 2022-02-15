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
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.squareup.picasso.Picasso;


//import com.google.android.exoplayer2.source.ProgressiveMediaSource;

public class CurrentSongPlayingFragment extends Fragment {

    private MusicService musicService;

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

        PlayerView playerView = view.findViewById(R.id.video_view);
        musicService = new MusicService(view.getContext(),playerView); //Init Music service
        contextView = view;

        ImageView songImage = view.findViewById(R.id.songImage);

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

        return view;
    }
}
