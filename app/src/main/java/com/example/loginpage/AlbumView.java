package com.example.loginpage;

import static com.google.android.exoplayer2.ExoPlayerLibraryInfo.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumView extends Fragment {

    private RecyclerView recyclerView;
    private final ArrayList<Song> Songs = new ArrayList<Song>();
    /*private String artistName;
    private String albumName;*/

    private ProgressBar loadingBar;
    private ImageView albumImg;
    private TextView albumNameTextView;
    private TextView artistNameTextView;
    private TextView albumDurationTextView;

    private boolean editMode = false;

    private Album album;

    private String artist_id = "";
    private String album_id = "";

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_view, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        artistNameTextView = view.findViewById(R.id.album_view_artistName);
        albumNameTextView = view.findViewById(R.id.album_view_albumName);
        albumImg = view.findViewById(R.id.album_view_albumImgView);
        loadingBar = view.findViewById(R.id.album_view_loadingBar);
        albumDurationTextView = view.findViewById(R.id.album_view_durationTextView);

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
            artist_id = bundle.getString("artist_id");
            album_id = bundle.getString("album_id");
        }


        artistNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new fragment and transaction
                Fragment newFragment = new ArtistView();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        /*String albumIMG_url = album.albumImage;
        Picasso.with(view.getContext()).load(albumIMG_url).into(albumImg);

        String albumName = album.albumTitle;
        albumNameTextView.setText(albumName);

        String albumDuration = album.albumDuration;
        albumDurationTextView.setText(albumDuration);*/

        Album album = new Album();

        db.collection("artists")
                .document(artist_id)
                .collection("albums")
                .document(album_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    album.albumImage = task.getResult().get("album_img_url").toString();
                }else{
                    Toast.makeText(view.getContext(), "Failed Fetching album Picture: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        db.collection("artists")
                .document(artist_id)
                .collection("albums")
                .document(album_id)
                .collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docString = document.get("song_title").toString();

                                //Songs.add(new Song("",artistName,docString));
                                Songs.add(new Song(document.get("song_URL").toString(),album.albumImage,artist_id,docString));
                            }
                            recyclerView = view.findViewById(R.id.album_view_songRecyclerView);
                            recyclerView.setHasFixedSize(false);
                            AlbumView_RecyclerViewAdapter adapter = new AlbumView_RecyclerViewAdapter(view.getContext(),getFragmentManager(),album);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            artistNameTextView.setText(artist_id);

                            loadingBar.setVisibility(View.INVISIBLE);

                            albumImg.setVisibility(View.VISIBLE);
                            albumNameTextView.setVisibility(View.VISIBLE);
                            artistNameTextView.setVisibility(View.VISIBLE);
                            albumDurationTextView.setVisibility(View.VISIBLE);

                            album.setSongs(Songs);
                            Picasso.with(view.getContext()).load(album.albumImage).into(albumImg);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return view;
    }
}
