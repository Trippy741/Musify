package com.example.loginpage;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class Song implements Parcelable {

    @Exclude
    public Bitmap song_bitmap;

    public String image_URL = "";
    public String artist_title;
    public String song_title;
    public String song_URL = "";

    public String image_uri = "";

    public Song(String song_URL,String image_URL, String name_artist, String song_title) {
        this.image_URL = image_URL;
        this.artist_title = name_artist;
        this.song_title = song_title;
        this.song_URL = song_URL;
    }

    protected Song(Parcel in) {
        image_URL = in.readString();
        artist_title = in.readString();
        song_title = in.readString();
        song_URL = in.readString();
        image_uri = in.readString();
        song_bitmap = Bitmap.CREATOR.createFromParcel(in);
    }
    public Song()
    {

    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image_URL);
        parcel.writeString(artist_title);
        parcel.writeString(song_title);
        parcel.writeString(song_URL);
        parcel.writeString(image_uri);
        song_bitmap.writeToParcel(parcel,0);
    }
}
