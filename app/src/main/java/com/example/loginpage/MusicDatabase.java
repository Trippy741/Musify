package com.example.loginpage;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.List;

public class MusicDatabase {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    CollectionReference songCollection = firestore.collection("songs");
    private List<Song> songList;

    private void getAllSongs()
    {
        new Runnable() {

            @Override
            public void run() {
                try
                {
                    songList = (List<Song>) songCollection.get();
                }
                catch(Exception e)
                {
                    Log.d("Song Database","Error: " + e.getMessage());
                }
            }
        }.run();
    }
    public List<Song> getSongList()
    {
        return songList;
    }
}
