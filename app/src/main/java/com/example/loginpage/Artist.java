package com.example.loginpage;

import java.util.ArrayList;

public class Artist{
    public ArrayList<Song> popularSongs = new ArrayList<Song>(5);
    public String artistTitle;
    public String artistQuote;
    public String artistImage;
    public ArrayList<Album> albums = new ArrayList<>();

    public Artist(String artistTitle,String artistQuote,String artistImage,ArrayList<Song> popularSongs,ArrayList<Album> albums)
    {
        this.artistTitle = artistTitle;
        this.artistQuote = artistQuote;
        this.popularSongs = popularSongs;
        this.artistImage = artistImage;
        this.albums = albums;
    }
    public Artist(String artistTitle,String artistQuote,String artistImage)
    {
        this.artistTitle = artistTitle;
        this.artistQuote = artistQuote;
        this.artistImage = artistImage;
    }
    public void addAlbum(Album album)
    {
        albums.add(album);
    }
}
