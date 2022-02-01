package com.example.loginpage;

public class Song{

    public String image_URL;
    public String artist_title;
    public String song_title;

    public Song(String image_URL, String name_artist, String song_title) {
        this.image_URL = image_URL;
        this.artist_title = name_artist;
        this.song_title = song_title;
    }
}
