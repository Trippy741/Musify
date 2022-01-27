package com.example.loginpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private ArrayList<Song> songs = new ArrayList<Song>();
    private AlbumView_RecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        adapter = new AlbumView_RecyclerViewAdapter(view.getContext(),);//TODO: FIX THIS AND DO THE SEARCH THING PLEASE :)
        recyclerView = view.findViewById(R.id.main_search_recyclerView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("songs").orderBy("title").startAt()

        recyclerView.setAdapter();

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu_search,menu);
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
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
