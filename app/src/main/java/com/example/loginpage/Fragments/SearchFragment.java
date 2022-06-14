package com.example.loginpage.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.CustomDataTypes.Album;
import com.example.loginpage.CustomDataTypes.Artist;
import com.example.loginpage.R;
import com.example.loginpage.RecyclerViewAdapters.Search_RecyclerViewAdapter;
import com.example.loginpage.CustomDataTypes.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private final ArrayList<Object> queries = new ArrayList<>();
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

        return view;
    }
    private void searchDatabase(String searchText)
    {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("artists").orderBy("artist_title").startAt(searchText).endAt(searchText+"\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot doc : task.getResult())
                        {
                            Artist tmpArtist = new Artist(doc.get("artist_title").toString(),doc.get("artist_quote").toString(),doc.get("artist_image_url").toString());
                            if(queries.size() <= 20 && !queries.contains(tmpArtist))
                                queries.add(tmpArtist);//TODO: ARTISTS
                            String artistID = doc.get("artist_title").toString();

                            db.collection("artists").document(doc.getId()).collection("albums").orderBy("album_title").endAt(searchText+"\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot doc : task.getResult())
                                        {
                                            Album tmpAlbum = new Album("artists/"+tmpArtist.artistTitle+"/albums/"+doc.getId(),doc.getId(),doc.get("album_title").toString()
                                                    ,tmpArtist.artistTitle
                                                    ,doc.get("album_img_url").toString(),doc.get("album_release_date").toString(),doc.get("album_duration").toString());

                                            tmpArtist.addAlbum(tmpAlbum);
                                            if(queries.size() <= 20 && !queries.contains(tmpArtist))
                                                queries.add(tmpAlbum);

                                            db.collection("artists").document(artistID).collection("albums").document(doc.getId()).collection("songs").orderBy("song_title").endAt(searchText+"\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        for(QueryDocumentSnapshot doc : task.getResult())
                                                        {
                                                            Song tmpSong = new Song(doc.get("song_URL").toString(),tmpAlbum.albumImage,tmpArtist.artistTitle,doc.get("song_title").toString());
                                                            tmpAlbum.addSong(tmpSong);
                                                            if(queries.size() <= 20 && !queries.contains(tmpArtist))
                                                                queries.add(tmpSong);
                                                        }

                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        FragmentManager manager = getParentFragmentManager();
                                                        adapter = new Search_RecyclerViewAdapter(view.getContext(),manager,queries);

                                                        recyclerView.setAdapter(adapter);
                                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext()); //TODO: USE THESE 2 LINES IF YOU'RE HAVING TROUBLE MAKING THINGS APPEAR IN YOUR RECYCLERVIEW
                                                        recyclerView.setLayoutManager(linearLayoutManager);
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
        }   catch (Exception e)
        {
            Toast.makeText(view.getContext(), "Failed Searching for song, Please try again later: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu_search,menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(view.getContext(), "Expanded", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Toast.makeText(view.getContext(), "Collapsed", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        menu.findItem(R.id.search_menu_search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_menu_search).getActionView();

        searchView.setQueryHint("Enter your search query here...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                queries.clear();
                searchDatabase(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
