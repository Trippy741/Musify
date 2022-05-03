package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheEvictor;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;
import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MusicService {

    private final Context mContext;
    private SimpleExoPlayer player;
    public Boolean isPlaying = false;
    private PlayerView playerView;

    private ArrayList<Song> songs = new ArrayList<Song>();

    private SimpleCache simpleCache;
    private DefaultTrackSelector trackSelector = new DefaultTrackSelector();

    private ArrayList<MediaSource> mediaSources = new ArrayList<MediaSource>();
    private int currentSourceIndex;
    private File cacheFolder;
    private CacheEvictor evictor;
    private DefaultHttpDataSource.Factory dataSource;
    private CacheDataSourceFactory cacheDataSourceFactory;

    public MusicService(Context mContext, PlayerView playerView,ArrayList<Song> songs)
    {
        this.mContext = mContext;
        this.playerView = playerView;
        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        this.songs = songs;

        currentSourceIndex = 0;

        cacheFolder = new File(mContext.getCacheDir(),"audio_cache");
        evictor = new LeastRecentlyUsedCacheEvictor(1920*1920);
        simpleCache = new SimpleCache(cacheFolder,evictor);
        dataSource = new DefaultHttpDataSource.Factory();

        cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache,dataSource);
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
    @SuppressLint("StaticFieldLeak")
    public void playAlbumFromURL()
    {
        for (int i = 0; i < songs.size(); i++) {
            new YouTubeExtractor(mContext)
            {
                @Override
                protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                    if(ytFiles != null)
                    {
                        int audioTag = 140;
                        MediaSource audioSource = new ProgressiveMediaSource
                                .Factory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));

                        mediaSources.add(audioSource);
                        Toast.makeText(mContext, "Fetched Song!" , Toast.LENGTH_SHORT).show();
                    }
                }
            }.extract(songs.get(i).song_URL,false,true);
        }
        player.setMediaSources(mediaSources,true);
        player.prepare(mediaSources.get(currentSourceIndex));
        playerView.setPlayer(player);
        playerView.setControllerShowTimeoutMs(-1);
        playerView.setControllerAutoShow(true);
        playerView.setControllerHideOnTouch(false);

        playerView.findViewById(R.id.exo_ffwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.release();
                if(currentSourceIndex < mediaSources.size())
                    player.prepare(mediaSources.get(currentSourceIndex + 1));
                else
                {
                    currentSourceIndex = 0;
                    player.prepare(mediaSources.get(currentSourceIndex));
                }
            }
        });
        playerView.findViewById(R.id.exo_rew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSourceIndex > 0)
                    player.prepare(mediaSources.get(currentSourceIndex));
                else
                {
                    currentSourceIndex = mediaSources.size() - 1;
                    player.prepare(mediaSources.get(currentSourceIndex));
                }
            }
        });

        player.setPlayWhenReady(true);
        isPlaying = true;
    }
    @SuppressLint("StaticFieldLeak")
    public void playSongFromURL(String URL)
    {
        new YouTubeExtractor(mContext)
        {
            @Override
            protected void onExtractionComplete(@Nullable SparseArray<YtFile> ytFiles, @Nullable VideoMeta videoMeta) {
                if(ytFiles != null)
                {
                    int audioTag = 140;
                    MediaSource audioSource = new ProgressiveMediaSource
                            .Factory(cacheDataSourceFactory).createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));

                    Toast.makeText(mContext, "Fetched Song!" , Toast.LENGTH_SHORT).show();

                    player.setMediaSource(audioSource,true);
                    player.prepare();
                    playerView.setPlayer(player);
                    playerView.setControllerShowTimeoutMs(-1);
                    playerView.setControllerHideOnTouch(false);
                }
            }
        }.extract(URL,false,true);



        playerView.findViewById(R.id.exo_ffwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
        });
        playerView.findViewById(R.id.exo_rew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
            }
        });

        player.setPlayWhenReady(true);
        isPlaying = true;
    }
}
