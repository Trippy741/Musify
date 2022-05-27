package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.session.MediaSession;
import android.support.v4.media.session.MediaSessionCompat;

import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;


import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MusicService {

    private final Context mContext;
    private static SimpleExoPlayer player;
    public static Boolean isPlaying = false;
    private PlayerView playerView;

    private ArrayList<Song> songs = new ArrayList<Song>();

    /*private SimpleCache simpleCache;*/
    private DefaultTrackSelector trackSelector = new DefaultTrackSelector();

    private ArrayList<MediaSource> mediaSources = new ArrayList<MediaSource>();
    private int currentSourceIndex;
    /*private File cacheFolder;
    private CacheEvictor evictor;*/
    private DefaultHttpDataSource.Factory dataSource;
    /*private CacheDataSourceFactory cacheDataSourceFactory;*/

    private MediaSessionCompat mediaSessionCompat;

    public MusicService(Context mContext, PlayerView playerView,ArrayList<Song> songs)
    {
        this.mContext = mContext;
        this.playerView = playerView;
        player = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        this.songs = songs;

        currentSourceIndex = 0;

        mediaSessionCompat = new MediaSessionCompat(mContext,"tag");

        /*cacheFolder = new File(mContext.getCacheDir(),"audio_cache");
        evictor = new LeastRecentlyUsedCacheEvictor(1920*1920);*/
        /*simpleCache = new SimpleCache(cacheFolder,evictor);*/
        dataSource = new DefaultHttpDataSource.Factory();

        /*cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache,dataSource);*/

        MediaSessionConnector mediaSessionConnector =
                new MediaSessionConnector(mediaSessionCompat);
        mediaSessionConnector.setPlayer(player);
    }
    public static SimpleExoPlayer getPlayer()
    {
        return player;
    }
    public static void resumePlayer()
    {
        if(!isPlaying)
        {
            player.play();
            isPlaying = true;
        }
    }
    public static void pausePlayer()
    {
        if(isPlaying)
        {
            player.pause();
            isPlaying = false;
        }
    }
    public MediaSessionCompat getMediaSessionCompat()
    {
        return mediaSessionCompat;
    }
    public MediaSession.Token getMediaCompatToken()
    {
        return (MediaSession.Token) mediaSessionCompat.getSessionToken().getToken();
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
                                .Factory(dataSource).createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl()));

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
                            .Factory(dataSource).createMediaSource(MediaItem.fromUri(ytFiles.get(audioTag).getUrl())); //TODO: CHANGE THE FACTORY TO THE CACHE FACTORY LATER

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
