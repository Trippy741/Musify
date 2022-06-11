package com.example.loginpage.CustomDataTypes;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

public class Album implements Parcelable {
    public String albumTitle = "Empty";
    public String artistTitle = "Empty";
    public String albumImage = "";
    public String albumDuration = "0 hr, 0 min";
    public String albumReleaseDate = "";
    public String album_id = "";
    public String database_path = "";

    public String imageURI = "";

    @Exclude
    public Bitmap imgBitmap;

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getImageURI() {
        return imageURI;
    }

    public Image getImage() {
        return image;
    }

    public Image image;

    public ArrayList<Song> songs = new ArrayList<Song>();

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public void setArtistTitle(String artistTitle) {
        this.artistTitle = artistTitle;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

    public void setAlbumDuration(String albumDuration) {
        this.albumDuration = albumDuration;
    }

    public void setAlbumReleaseDate(String albumReleaseDate) {
        this.albumReleaseDate = albumReleaseDate;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setDatabase_path(String database_path) {
        this.database_path = database_path;
    }

    public Album(String database_path, String album_id, String albumTitle, String artistTitle, String albumImage , ArrayList<Song> songs, String albumDuration, String albumReleaseDate) {
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
    public Album()
    {

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
    public void setSongs(ArrayList<Song> songs)
    {
        this.songs = songs;
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
