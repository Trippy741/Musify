package com.example.loginpage;

import static com.google.android.exoplayer2.ExoPlayerLibraryInfo.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Custom_AlbumView extends Fragment implements IOnBackPressed{

    private RecyclerView recyclerView;
    private final ArrayList<Song> Songs = new ArrayList<Song>();

    private ProgressBar loadingBar;
    private ImageView albumImg;
    private TextView albumNameTextView;
    private TextView artistNameTextView;
    private TextView albumDurationTextView;

    private FloatingActionButton addSong_fab;

    private Album album;

    private View view;
    private AlbumView_RecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom_album_view, container, false);
        setHasOptionsMenu(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        artistNameTextView = view.findViewById(R.id.custom_albumView_artistName);
        albumNameTextView = view.findViewById(R.id.custom_albumView_albumName);
        albumImg = view.findViewById(R.id.custom_albumView_albumImg);
        loadingBar = view.findViewById(R.id.custom_albumView_progressBar);
        albumDurationTextView = view.findViewById(R.id.custom_albumView_duration);
        addSong_fab = view.findViewById(R.id.custom_albumView_fab);

        loadingBar.setVisibility(View.VISIBLE);

        albumImg.setVisibility(View.INVISIBLE);
        albumNameTextView.setVisibility(View.INVISIBLE);
        artistNameTextView.setVisibility(View.INVISIBLE);
        albumDurationTextView.setVisibility(View.INVISIBLE);

        /*if(getArguments() != null)
        {
            Bundle bundle = getArguments();
            album = (Album) bundle.get("album_obj");
        }*/

        if(getArguments() != null)
        {
            Bundle bundle = getArguments();
            album = bundle.getParcelable("album_obj");
        }


        artistNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTextChangeDialog(view,artistNameTextView);
            }
        });
        albumNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTextChangeDialog(view,albumNameTextView);
            }
        });
        addSong_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Opening Song dialog...", Toast.LENGTH_SHORT).show();
                Song tmpSong = new Song();
                openAddSongDialog(view,tmpSong);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView = view.findViewById(R.id.custom_albumView_recyclerView);
        adapter = new AlbumView_RecyclerViewAdapter(view.getContext(),getFragmentManager(),album.songs,true,album.albumTitle);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        artistNameTextView.setText(album.artistTitle);
        albumNameTextView.setText(album.albumTitle);

        loadingBar.setVisibility(View.INVISIBLE);

        albumImg.setVisibility(View.VISIBLE);
        albumNameTextView.setVisibility(View.VISIBLE);
        artistNameTextView.setVisibility(View.VISIBLE);
        albumDurationTextView.setVisibility(View.VISIBLE);

        if(!album.imageURI.isEmpty())
            albumImg.setImageURI(Uri.parse(album.imageURI));
        else
            Picasso.with(view.getContext()).load(album.albumImage).into(albumImg);



        return view;
    }

    @Override
    public boolean onBackPressed() {
        getFragmentManager().popBackStackImmediate();
        return false;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.custom_album_save_menu_button,menu);
        MenuItem item = menu.findItem(R.id.custom_album_save);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //SAVE
                Toast.makeText(view.getContext(), "Saving Custom Album...", Toast.LENGTH_SHORT).show();
                //saveAlbumToFirebase(album);
                getFragmentManager().popBackStackImmediate();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }
    private Song openAddSongDialog(View view,Song tempSong)
    {

        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_album_add_song_dialog);

        ImageButton confirmButton = d.getWindow().findViewById(R.id.custom_add_song_confirm);
        ImageButton denyButton = d.getWindow().findViewById(R.id.custom_add_song_deny);
        TextInputLayout songTitle_TIL = d.getWindow().findViewById(R.id.custom_add_song_songTitle_TIL);
        TextInputLayout songURL_TIL = d.getWindow().findViewById(R.id.custom_add_song_songURL_TIL);
        TextInputLayout artistTitle_TIL = d.getWindow().findViewById(R.id.custom_add_song_artistTitle_TIL);
        ProgressBar progressBar = d.getWindow().findViewById(R.id.custom_add_song_loadingBar);
        RelativeLayout parentLayout = d.getWindow().findViewById(R.id.custom_add_song_bg);

        progressBar.setVisibility(View.INVISIBLE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!songTitle_TIL.getEditText().getText().toString().isEmpty() || !songURL_TIL.getEditText().getText().toString().isEmpty() || !artistTitle_TIL.getEditText().getText().toString().isEmpty())
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    tempSong.song_title = songTitle_TIL.getEditText().getText().toString();
                    tempSong.song_URL = songURL_TIL.getEditText().getText().toString();
                    if(!album.albumImage.isEmpty())
                        tempSong.image_URL = "http://img.youtube.com/vi/"+ getYouTubeId(songURL_TIL.getEditText().getText().toString()) +"/0.jpg";
                    else
                        tempSong.image_uri = album.imageURI;

                    tempSong.artist_title = artistTitle_TIL.getEditText().getText().toString();
                    album.addSong(tempSong);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    adapter = new AlbumView_RecyclerViewAdapter(view.getContext(),getFragmentManager(),album.songs);
                    recyclerView.setVisibility(View.VISIBLE);

                    Toast.makeText(view.getContext(), "Size: " + album.songs.size(), Toast.LENGTH_SHORT).show();

                    Toast.makeText(view.getContext(), "Adding Song...", Toast.LENGTH_SHORT).show();

                    d.dismiss();
                }
                else
                    Toast.makeText(view.getContext(), "Both Fields must not be empty!", Toast.LENGTH_SHORT).show();
            }
        });
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Discarding Changes...", Toast.LENGTH_SHORT).show();
                d.dismiss();
            }
        });
        d.show();
        return tempSong;
    }
    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }
    private void openTextChangeDialog(View view,TextView textView)
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_profile_change_titles_dialog);


        TextInputLayout textLayout = d.getWindow().findViewById(R.id.custom_profileTitlesChange_editText);
        textLayout.setHint("Change Title");


        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(textLayout.getEditText().getEditableText().toString());
                d.dismiss();
            }
        });

        d.show();
    }
    /*private void saveAlbumToFirebase(Album currentAlbum)
    {

        album.artistTitle = artistNameTextView.getText().toString();
        album.albumTitle = albumNameTextView.getText().toString();

        loadingBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        *//*db.collection("artists").document("my_songs").collection("albums").add(album).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(view.getContext(), "Working!", Toast.LENGTH_SHORT).show();
                    loadingBar.setVisibility(View.INVISIBLE);
                }
            }
        });*//*

        Album tempAlbum = new Album();
        tempAlbum.albumImage = album.albumImage;
        tempAlbum.albumTitle = album.albumTitle;
        tempAlbum.albumDuration = album.albumDuration;
        tempAlbum.albumReleaseDate = album.albumReleaseDate;
        tempAlbum.album_id = album.album_id;

        final String[] albumID = {""};

        db.collection("artists").document("my_songs").collection("albums").add(tempAlbum).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                albumID[0] = task.getResult().getId();

                for (Song song : currentAlbum.songs)
                {
                    String a = albumID[0];
                    db.collection("artists")
                            .document("my_songs")
                            .collection("albums")
                            .document(albumID[0])
                            .collection("songs").add(song).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(view.getContext(), "Song " + song.song_title + " Was Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(view.getContext(), "Failed Uploading " + song.song_title + ". " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }*/
}
