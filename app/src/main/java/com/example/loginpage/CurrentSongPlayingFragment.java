package com.example.loginpage;

import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private final boolean isPlaying = false;

    private Song songPlaying;
    private ArrayList<Song> songQueue = new ArrayList<Song>();

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);

        PlayerView playerView = view.findViewById(R.id.video_view);
        musicService = new MusicService(view.getContext(),playerView,songQueue); //Init Music service
        contextView = view;

        ImageView songImage = view.findViewById(R.id.songImage);

        TextView mainTitle = view.findViewById(R.id.song_playing_mainTitle);
        TextView subTitle = view.findViewById(R.id.song_playing_subTitle);

        if(getArguments() != null)
        {
            Bundle bundle = getArguments();
            songPlaying = (Song) bundle.get("song_obj");
            if(bundle.get("album_obj") != null)
                songQueue = ((Album) bundle.get("album_obj")).songs;
        }
        else
            Toast.makeText(view.getContext(), "Null song argument", Toast.LENGTH_SHORT).show();

        if(songPlaying.image_URL.isEmpty())
            songImage.setImageURI(Uri.parse(songPlaying.image_uri));
        else
            Picasso.with(view.getContext()).load(songPlaying.image_URL).into(songImage);
        mainTitle.setText(songPlaying.song_title);
        subTitle.setText(songPlaying.artist_title);

        if(songQueue.isEmpty())
            musicService.playSongFromURL(songPlaying.song_URL);
        else
            musicService.playAlbumFromURL();

        return view;
    }

}
