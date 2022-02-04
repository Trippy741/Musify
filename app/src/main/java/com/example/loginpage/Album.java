package com.example.loginpage;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Album implements Parcelable {
    public String albumTitle;
    public String artistTitle;
    public String albumImage;
    public String albumDuration;
    public String albumReleaseDate;
    public String album_id;
    public String database_path;
    public ArrayList<Song> songs = new ArrayList<Song>();

    public Album(String database_path,String album_id,String albumTitle, String artistTitle,String albumImage ,ArrayList<Song> songs,String albumDuration,String albumReleaseDate) {
        this.albumTitle = albumTitle;
        this.artistTitle = artistTitle;
        this.albumImage = albumImage;
        this.songs = songs;
        this.albumDuration = albumDuration;
        this.albumReleaseDate = albumReleaseDate;
        this.album_id = album_id;
        this.database_path = database_path;
    }
    public Album(String database_path,String album_id,String albumTitle, String artistTitle,String albumImage,String albumDuration,String albumReleaseDate) {
        this.albumTitle = albumTitle;
        this.artistTitle = artistTitle;
        this.albumImage = albumImage;
        this.albumDuration = albumDuration;
        this.albumReleaseDate = albumReleaseDate;
        this.album_id = album_id;
        this.database_path = database_path;
    }

    protected Album(Parcel in) {
        albumTitle = in.readString();
        artistTitle = in.readString();
        albumImage = in.readString();
        albumDuration = in.readString();
        albumReleaseDate = in.readString();
        album_id = in.readString();
        database_path = in.readString();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public void addSong(Song song)
    {
        songs.add(song);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(albumTitle);
        parcel.writeString(artistTitle);
        parcel.writeString(albumImage);
        parcel.writeString(albumDuration);
        parcel.writeString(albumReleaseDate);
        parcel.writeString(album_id);
        parcel.writeString(database_path);
    }
}
