package com.example.loginpage

import android.content.Context
import android.util.SparseArray
import android.view.View
import at.huber.youtubeExtractor.VideoMeta
import com.google.android.exoplayer2.SimpleExoPlayer
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource


class MusicService {
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var playbackPosition: Long = 0

    fun initPlayer(Context:Context,VID_URL:String)
    {
        player = SimpleExoPlayer.Builder(Context).build()

        val videoURL = VID_URL
        object : YouTubeExtractor(Context)
        {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {
                if(ytFiles != null)
                {
                    val audioTag = 140
                    val video_url = ytFiles[audioTag].url

                    val audioSource : MediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(video_url))

                    player!!.setMediaSource(audioSource)

                    player!!.prepare()
                    player!!.playWhenReady = playWhenReady
                    player!!.seekTo(playbackPosition)
                }
            }
        }.extract(videoURL,false,true)
    }
    fun setPlayerPosition(position:Long)
    {
        player!!.seekTo(position)
    }
    fun stopPlayer()
    {
        player!!.stop()
    }
    fun getProgress():Long
    {
        return player!!.currentPosition
    }
    fun getPlayer():SimpleExoPlayer
    {
        return player!!
    }
}