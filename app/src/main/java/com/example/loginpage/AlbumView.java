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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

    private Album album;

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

        String artist_id = "";
        String album_id = "";

        if(getArguments() != null)
        {
            Bundle bundle = getArguments();
            album = (Album) bundle.get("album_obj");
            /*artist_id = bundle.getString("artist_id");
            album_id = bundle.getString("album_id");

            artistName = artist_id;
            albumName = album_id;*/
        }
        else
        {
            /*artistName = "TOOL";
            albumName = "lateralus";*/
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

        String albumIMG_url = album.albumImage;
        Picasso.with(view.getContext()).load(albumIMG_url).into(albumImg);

        String albumName = album.albumTitle;
        albumNameTextView.setText(albumName);

        String albumDuration = album.albumDuration;
        albumDurationTextView.setText(albumDuration);

        db.collection("artists")
                .document(album.artistTitle)
                .collection("albums")
                .document(album.album_id)
                .collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String docString = document.get("song_title").toString();

                                //Songs.add(new Song("",artistName,docString));
                                Songs.add(new Song("",album.artistTitle,docString));
                            }
                            recyclerView = view.findViewById(R.id.album_view_songRecyclerView);
                            recyclerView.setHasFixedSize(false);
                            AlbumView_RecyclerViewAdapter adapter = new AlbumView_RecyclerViewAdapter(view.getContext(),Songs);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                            artistNameTextView.setText(album.artistTitle);

                            loadingBar.setVisibility(View.INVISIBLE);

                            albumImg.setVisibility(View.VISIBLE);
                            albumNameTextView.setVisibility(View.VISIBLE);
                            artistNameTextView.setVisibility(View.VISIBLE);
                            albumDurationTextView.setVisibility(View.VISIBLE);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return view;
    }
}
