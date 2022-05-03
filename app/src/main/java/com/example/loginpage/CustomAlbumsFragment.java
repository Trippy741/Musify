package com.example.loginpage;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomAlbumsFragment extends Fragment{

    private ArrayList<Album> albums = new ArrayList<Album>();
    private RecyclerView albumRecycler;

    private FloatingActionButton fab;

    private Context mContext;
    private ArrayList<Album> newAlbums = new ArrayList<Album>();

    private Dialog d;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private CustomAlbum_RecyclerViewAdapter adapter;
    private Uri tempURI = Uri.EMPTY;
    private Album tempAlbum = new Album();

    private boolean tempDoneWithPhotoPick = false;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_albums, container, false);
        setHasOptionsMenu(true);

        adapter = new CustomAlbum_RecyclerViewAdapter(view.getContext(),getFragmentManager(),albums);

        mContext = view.getContext();
        albumRecycler = view.findViewById(R.id.custom_albums_recyclerView);
        fab = view.findViewById(R.id.custom_albums_fab);

        fab = view.findViewById(R.id.custom_albums_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: ADD THE PLAYLIST CREATION DIALOG HERE
                openPlaylistCreationDialog();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_custom_playlists")
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot doc : task.getResult())
                {
                    if(task.isSuccessful())
                    {
                        if(doc.get("album_title") != null)
                        {
                            Album tempAlbum = new Album();
                            tempAlbum.setAlbumTitle(doc.get("album_title").toString());

                            tempAlbum.setAlbumDuration(doc.get("album_duration").toString());
                            tempAlbum.setArtistTitle(doc.get("artist_title").toString());
                            tempAlbum.setAlbumReleaseDate(doc.get("album_release_date").toString());
                            tempAlbum.setAlbumImage(doc.get("album_img_url").toString());

                            db.collection("artists").document("my_songs").collection("albums")
                                    .document(doc.getId()).collection("songs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot doc : task.getResult())
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Song tmpSong = new Song();
                                            tmpSong.song_title = doc.get("song_title").toString();
                                            tmpSong.song_URL = doc.get("song_URL").toString();
                                            tmpSong.artist_title = tempAlbum.artistTitle;
                                            tmpSong.image_URL = tempAlbum.albumImage;
                                            tempAlbum.addSong(tmpSong);
                                        }else
                                            Toast.makeText(view.getContext(), "Failed Fetching songs: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            albums.add(tempAlbum);
                            adapter = new CustomAlbum_RecyclerViewAdapter(view.getContext(),getFragmentManager(),albums);
                            albumRecycler.setAdapter(adapter);
                        }
                    }
                }
            }
        });

        return view;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.custom_album_save_menu_button,menu);
        MenuItem item = menu.findItem(R.id.custom_album_save);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //SAVE
                Toast.makeText(mContext, "Saving Custom Album...", Toast.LENGTH_SHORT).show();
                addAlbumToFirebase(newAlbums);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }
    private void openPlaylistCreationDialog()
    {
        tempAlbum = new Album();

        d = new Dialog(mContext,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_playlist_creation_dialog);

        ImageView image = d.getWindow().findViewById(R.id.album_image);

        TextInputLayout dateTIL = d.getWindow().findViewById(R.id.datePicker);
        TextInputEditText artistTitle = d.getWindow().findViewById(R.id.artist_title);
        TextInputEditText albumTitle = d.getWindow().findViewById(R.id.album_title);

        RelativeLayout bg = d.getWindow().findViewById(R.id.bg);

        ImageButton deny_button = d.getWindow().findViewById(R.id.deny_button);
        ImageButton confirmButton = d.getWindow().findViewById(R.id.confirmButton);


        dateTIL.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateTIL.getEditText().setText(year+" / "+month+" / " + day);
                    }
                }
                        , Calendar.getInstance().YEAR
                        , Calendar.getInstance().MONTH
                        , Calendar.getInstance().DAY_OF_MONTH);

                datePickerDialog.show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent,111);

            }
        });

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDismissAlertDialog(d);
            }
        });

        deny_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDismissAlertDialog(d);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempAlbum.albumTitle = albumTitle.getEditableText().toString();
                tempAlbum.artistTitle = artistTitle.getEditableText().toString();
                tempAlbum.albumReleaseDate = dateTIL.getEditText().getEditableText().toString();

                newAlbums.add(tempAlbum);

                adapter.addNewAlbums(newAlbums);
                d.dismiss();
            }
        });
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                tempURI = Uri.EMPTY;
                tempAlbum = new Album();
            }
        });

        d.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final int unmaskedRequestCode = requestCode & 0x0000ffff;

        if(unmaskedRequestCode == requestCode){
            tempURI = data.getData();
            ImageView img = d.getWindow().findViewById(R.id.album_image);
            tempAlbum.imageURI = data.getData().toString();
            img.setImageURI(data.getData());
        }
    }

    private void showDismissAlertDialog(Dialog d)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle("Hey! Aren't you forgetting something?");
        alert.setMessage("Are you sure you want to dismiss this dialog and discard your changes?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                d.dismiss();
                Toast.makeText(mContext, "Discarding Changes...", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Exists alert dialog
            }
        });
        alert.create().show();

    }

    private void addAlbumToFirebase(ArrayList<Album> albumsToSave)
    {
        for(Album album : albumsToSave)
        {
            loading_custom_dialog loadingDialog = new loading_custom_dialog(mContext,false,null);

            loadingDialog.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_custom_playlists").add(album).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful())
                    {
                        String albumID = task.getResult().getId();
                        Toast.makeText(mContext, "Album Saved!", Toast.LENGTH_SHORT).show();

                        for (Song s :
                                album.songs) {
                            db.collection("users")
                                    .document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("user_custom_playlists").document(albumID).collection("songs").add(s);
                        }
                    }else
                        Toast.makeText(mContext, "Failed Saving Album: " + task.getException(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        }
    }
}
