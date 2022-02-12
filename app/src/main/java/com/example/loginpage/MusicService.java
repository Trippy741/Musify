package com.example.loginpage;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MusicService {

    private final Context mContext;
    private SimpleExoPlayer player;
    public Boolean isPlaying = false;

    public MusicService(Context mContext)
    {
        this.mContext = mContext;
        player = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultTrackSelector());
    }
    public SimpleExoPlayer getPlayer()
    {
        return player;
    }
    public void resumePlayer()
    {
        if(!isPlaying)
        {
            player.play();
            isPlaying = true;
        }
    }
    public void stopPlayer()
    {
        if(isPlaying)
        {
            player.stop();
            isPlaying = false;
        }
    }
    public void playFromURL(String URL)
    {
        new YouTubeExtractor(mContext)
        {
            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                if(ytFiles != null)
                {
                    int audioTag = 140;
                    MediaSource audioSource = new ProgressiveMediaSource
                            .Factory(new DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));

                    player.setMediaSource(audioSource,true);
                    player.prepare();
                    player.setPlayWhenReady(true);
                    isPlaying = true;
                }
            }
        }.extract(URL,false,true);
    }
}
