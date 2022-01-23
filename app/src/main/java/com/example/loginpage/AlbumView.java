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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AlbumView extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Song> Songs = new ArrayList<Song>();
    private String artistName;
    private String albumName;

    private ProgressBar loadingBar;
    private ImageView albumImg;
    private TextView albumNameTextView;
    private TextView artistNameTextView;
    private TextView albumDurationTextView;

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

        artistNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODo: Move to artist view P.S, make the artist view ;)
            }
        });

        db.collection("artists")
                .document("TOOL")
                .collection("albums")
                .document("lateralus").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    String albumIMG_url = doc.get("album_img_url").toString();
                    Picasso.with(view.getContext()).load(albumIMG_url).into(albumImg);

                    String albumName = doc.getId();
                    albumNameTextView.setText(albumName);

                    String albumDuration = doc.get("album_duration").toString();
                    albumDurationTextView.setText(albumDuration);
                }
            }
        });

        db.collection("artists")
                .document("TOOL")
                .collection("albums")
                .document("lateralus")
                .collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docString = document.get("song_title").toString();

                                Song tempSong = new Song("","",docString);


                                db.collection("artists").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        artistName = document.getId().toString();
                                                        tempSong.name_artist = artistName;

                                                        Songs.add(tempSong);

                                                        recyclerView = view.findViewById(R.id.album_view_songRecyclerView);
                                                        AlbumView_RecyclerViewAdapter adapter = new AlbumView_RecyclerViewAdapter(view.getContext(),Songs,artistName);
                                                        recyclerView.setAdapter(adapter);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                                        artistNameTextView.setText(artistName);

                                                        loadingBar.setVisibility(View.INVISIBLE);

                                                        albumImg.setVisibility(View.VISIBLE);
                                                        albumNameTextView.setVisibility(View.VISIBLE);
                                                        artistNameTextView.setVisibility(View.VISIBLE);
                                                        albumDurationTextView.setVisibility(View.VISIBLE);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return view;
    }


}
