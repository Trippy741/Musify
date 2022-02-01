package com.example.loginpage;

import java.util.ArrayList;

public class Album{
    public String albumTitle;
    public String artistTitle;
    public String albumImage;
    public ArrayList<Song> songs;

    public Album(String albumTitle, String artistTitle,String albumImage ,ArrayList<Song> songs) {
        this.albumTitle = albumTitle;
        this.artistTitle = artistTitle;
        this.albumImage = albumImage;
        this.songs = songs;
    }
    public Album(String albumTitle, String artistTitle,String albumImage) {
        this.albumTitle = albumTitle;
        this.artistTitle = artistTitle;
        this.albumImage = albumImage;
    }
    public void addSong(Song song)
    {
        songs.add(song);
    }
}
