package com.example.loginpage;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class deviceMusicPlayerFragment extends Fragment implements View.OnClickListener {
    private SimpleExoPlayer simpleExoPlayer;
    private boolean isPlaying = false;
    private Button playButton;
    private View thisView;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_music_player, container, false);

        playButton = view.findViewById(R.id.player_playButton);
        playButton.setOnClickListener(this);

        playerInit();

        return view;
    }
    private void playerInit()
    {
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext(),null,DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        TrackSelector trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory,trackSelector);

        String userAgent = Util.getUserAgent(getContext(),"Play Audio");
        ExtractorMediaSource mediaSource = new ExtractorMediaSource(Uri.parse("https://www.youtube.com/watch?v=iJqVjglvnoc&list=RDGMEMJQXQAmqrnmK1SEjY_rKBGAVMsuG-c_i2fBQ&index=2&ab_channel=Gojira-Topic"),
                new DefaultDataSourceFactory(getContext(),userAgent),
                new DefaultExtractorsFactory(),null,null);
        simpleExoPlayer.prepare(mediaSource);
    }
    private void release_player()
    {
        simpleExoPlayer.release();
    }
    @Override
    public void onDestroy()
    {
        simpleExoPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        simpleExoPlayer.release();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.player_playButton:
                if(!isPlaying)
                {
                    simpleExoPlayer.setPlayWhenReady(true);
                    isPlaying = true;
                }
                else
                {
                    simpleExoPlayer.stop();
                    isPlaying = false;
                }
                break;
        }
    }
}