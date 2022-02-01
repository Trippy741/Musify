package com.example.loginpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Object> queries = new ArrayList<>();
    private Search_RecyclerViewAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.main_search_recyclerView);
        progressBar = view.findViewById(R.id.main_search_progressBar);

        progressBar.setVisibility(View.VISIBLE);

        queries.add(new Album("ALBUM","ARTIST","IMAGE_URL"));
        searchDatabase("TOOL");

        return view;
    }
    private void searchDatabase(String searchText)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("artists").orderBy("artist_title").startAt(searchText).endAt("$searchText\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot doc : task.getResult())
                    {
                        Artist tmpArtist = new Artist(doc.get("artist_title").toString(),doc.get("artist_quote").toString(),doc.get("artist_image_url").toString());

                        queries.add(tmpArtist);//TODO: ARTISTS
                        String artistID = doc.get("artist_title").toString();

                        db.collection("artists").document(doc.getId()).collection("albums").orderBy("album_title").endAt("$searchText\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    for(QueryDocumentSnapshot doc : task.getResult())
                                    {
                                        Album tmpAlbum = new Album(doc.get("album_title").toString(),tmpArtist.artistTitle,doc.get("album_image_url").toString());
                                        tmpArtist.addAlbum(tmpAlbum);
                                        queries.add(tmpAlbum);//TODO: ALBUMS //ADD TO TMP_ARTIST ASWELL

                                        db.collection("artists").document(artistID).collection("albums").document(doc.getId()).collection("songs").orderBy("song_title").endAt("$searchText\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    for(QueryDocumentSnapshot doc : task.getResult())
                                                    {
                                                        Song tmpSong = new Song(tmpAlbum.albumImage,tmpArtist.artistTitle,doc.get("song_title").toString());
                                                        tmpAlbum.addSong(tmpSong);
                                                        queries.add(tmpSong);//TODO: SONGS
                                                    }

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    adapter = new Search_RecyclerViewAdapter(view.getContext(),queries);
                                                    recyclerView.setAdapter(adapter); //TODO: FIND OUT WHY THE APP NEVER REACHES THIS STAGE
                                                }
                                                else
                                                    Toast.makeText(view.getContext(), "Error fetching search data: "+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                else
                                    Toast.makeText(view.getContext(), "Error fetching search data: "+task.getException(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
                else
                    Toast.makeText(view.getContext(), "Error fetching search data: "+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*inflater.inflate(R.menu.main_menu_search,menu);
        MenuItem item = menu.findItem(R.id.main_search_recyclerView);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/

        super.onCreateOptionsMenu(menu, inflater);
    }
}
