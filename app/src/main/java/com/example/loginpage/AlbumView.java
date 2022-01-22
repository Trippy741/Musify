package com.example.loginpage;

import static com.google.android.exoplayer2.ExoPlayerLibraryInfo.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AlbumView extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Song> Songs = new ArrayList<Song>();
    private String artistName;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_view, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ProgressBar loadingBar = view.findViewById(R.id.album_view_loadingBar);
        loadingBar.setVisibility(View.VISIBLE);

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

                                                        loadingBar.setVisibility(View.INVISIBLE);
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
