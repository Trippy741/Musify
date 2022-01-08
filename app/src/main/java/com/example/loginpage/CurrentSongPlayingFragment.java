package com.example.loginpage;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

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

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_currentsongplaying, container, false);

        songImage = (ImageView) view.findViewById(R.id.songImage);
        bandName = view.findViewById(R.id.bandName);
        songName = view.findViewById(R.id.songName);
        currentTime = view.findViewById(R.id.currentTime);
        seekBar = (SeekBar) view.findViewById(R.id.seekbar);

        playImage = view.findViewById(R.id.play_button);
        fwrdImage = view.findViewById(R.id.forward_button);
        bckdImage = view.findViewById(R.id.backwards_button);


        //ReadImage();


        player = new MediaPlayer();
        seekBar.setMax(100);

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying())
                {
                    handler.removeCallbacks(updater);
                    player.pause();
                    playImage.setImageResource(R.drawable.ic_pauseicon);
                }else
                {
                    player.start();
                    playImage.setImageResource(R.drawable.ic_playicon);
                    UpdateSeekbar();
                }
            }
        });

        //InitMediaPlayer("https://www.youtube.com/watch?v=KVjBCT2Lc94&list=RDKVjBCT2Lc94&start_radio=1&ab_channel=AvengedSevenfold.mp3");
        return view;
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            UpdateSeekbar();
            long currentDuration = player.getCurrentPosition();
            currentTime.setText(millisToTime(currentDuration));
        }
    };/*
    private void ReadImage()
    {
        File file = new  File(getExternalStorageDirectory()+"Musify/");
        File imgFile = file.listFiles()[2];
        if(imgFile.exists())
        {
           try {
               Log.d("Files","File Loaded");
               Picasso.with(getContext()).load(imgFile).into(songImage);
           }
           catch (Exception e)
           {
                Log.d("Files",e.getMessage());
           }
        }
        else
            Log.d("Files","File does not exist");
    }*/

    private void InitMediaPlayer(String URL_PATH)
    {
        try
        {
            player.setDataSource(URL_PATH);
            player.prepare();

        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateSeekbar()
    {
        if(player.isPlaying())
        {
            seekBar.setProgress((int) ((float) player.getCurrentPosition() / player.getDuration()));
            handler.postDelayed(updater,1000);
        }
    }
    private String millisToTime(long millis)
    {
        String timer = "";
        String secondsString = "";

        int hours = (int)(millis/(1000*60*60));
        int minutes = (int)(millis % (1000*60*60) / (1000*60));
        int seconds = (int)((millis % (1000*60*60)) % (1000*60) / 1000);

        if(hours > 0)
        {
            timer = hours + ":";
        }
        if(seconds < 10)
        {
            secondsString = "0" + seconds;
        }else
        {
            secondsString = "" + seconds;
        }
        timer = timer + minutes + ":" + secondsString;
        return timer;
    }
}
