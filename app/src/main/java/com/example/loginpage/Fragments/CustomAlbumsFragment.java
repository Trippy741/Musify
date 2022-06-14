package com.example.loginpage.Fragments;

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

import com.example.loginpage.CustomDataTypes.Album;
import com.example.loginpage.CustomDialogs.LoadingAlertDialog;
import com.example.loginpage.RecyclerViewAdapters.CustomAlbum_RecyclerViewAdapter;
import com.example.loginpage.R;
import com.example.loginpage.CustomDataTypes.Song;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    private CustomAlbum_RecyclerViewAdapter adapter;
    private Uri tempURI = Uri.EMPTY;
    private Album tempAlbum = new Album();

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_albums, container, false);
        setHasOptionsMenu(true);

        albumRecycler = view.findViewById(R.id.custom_albums_recyclerView);

        adapter = new CustomAlbum_RecyclerViewAdapter(view.getContext(),getFragmentManager(),albums);
        adapter.notifyDataSetChanged();
        albumRecycler.setAdapter(adapter);

        mContext = view.getContext();
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


        if(albums.isEmpty())
        {
            db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_custom_playlists")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot doc : task.getResult())
                    {
                        if(task.isSuccessful())
                        {
                            if(doc.get("albumTitle") != null)
                            {
                                Album tempAlbum = new Album();
                                tempAlbum.setAlbumTitle(doc.get("albumTitle").toString());

                                tempAlbum.setAlbumDuration(doc.get("albumDuration").toString());
                                tempAlbum.setArtistTitle(doc.get("artistTitle").toString());
                                tempAlbum.setAlbumReleaseDate(doc.get("albumReleaseDate").toString());

                                tempAlbum.album_id = doc.getId();

                                if(doc.get("albumImage").toString() != null && doc.get("albumImage").toString() != "")
                                    tempAlbum.setAlbumImage(doc.get("albumImage").toString());
                                else if(doc.get("imageURI").toString() != null && doc.get("imageURI").toString() != "")
                                    tempAlbum.imageURI = doc.get("imageURI").toString();



                                db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_custom_playlists")
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
        }

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
                if(albumTitle.getEditableText().toString() != "" && artistTitle.getEditableText().toString() != "" && dateTIL.getEditText().getEditableText().toString() != "" && albumTitle.getEditableText().toString().length() > 5)
                {
                    tempAlbum.albumTitle = albumTitle.getEditableText().toString();
                    tempAlbum.artistTitle = artistTitle.getEditableText().toString();
                    tempAlbum.albumReleaseDate = dateTIL.getEditText().getEditableText().toString();

                    newAlbums.add(tempAlbum);

                    adapter.addNewAlbums(newAlbums);
                    adapter.notifyDataSetChanged();
                    adapter = new CustomAlbum_RecyclerViewAdapter(view.getContext(),getFragmentManager(),albums);
                    albumRecycler.setAdapter(adapter);

                    LoadingAlertDialog loadingD = new LoadingAlertDialog(mContext);
                    loadingD.show();

                    storageReference = FirebaseStorage.getInstance().getReference("images/user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/custom_playlists/"+tempAlbum.albumTitle);

                    storageReference.putFile(tempURI)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(mContext, "Successfully Uploaded Image to database!", Toast.LENGTH_SHORT).show();
                                        try {
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), tempURI);
                                            tempAlbum.imgBitmap = bitmap;
                                        } catch (IOException e) {
                                            Toast.makeText(mContext, "Error storing image Bitmap: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                        Toast.makeText(mContext, "Failed Uploading Image to database: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    loadingD.dismiss();
                                }
                            });

                    d.dismiss();
                }else
                    Toast.makeText(mContext, "All 3 Input fields MUST be filled out, And the title has to consist of 5 or more letters!", Toast.LENGTH_LONG).show();
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
            LoadingAlertDialog loadingDialog = new LoadingAlertDialog(mContext);

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
