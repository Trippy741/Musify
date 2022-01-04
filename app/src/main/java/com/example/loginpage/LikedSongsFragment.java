package com.example.loginpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LikedSongsFragment extends Fragment {

    public ArrayList<Song> songs = new ArrayList<>();
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_likesongs, container, false);

        // Add the following lines to create RecyclerView
        ArrayList<String> imageURL = new ArrayList<>();
        ArrayList<String> songNames = new ArrayList<>();
        ArrayList<String> bandNames = new ArrayList<>();


        initSongs();
        recyclerView = view.findViewById(R.id.likedSongsRecycler); //TODO: FIX CRASHING!!!

        for(int i = 0; i < songs.size(); i++)
        {
            imageURL.add(songs.get(i).image_URL);
            songNames.add(songs.get(i).name_artist);
            bandNames.add(songs.get(i).name_song);

        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(),imageURL,songNames,bandNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
    //
    private void initSongs()
    {
        songs.add(new Song("Afterlife","Avenged Sevenfold","https://upload.wikimedia.org/wikipedia/en/7/76/Avenged_Sevenfold_cover_2007.jpg"));
        songs.add(new Song("Prison Sex","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Sober","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Undertow","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
        songs.add(new Song("Bottom","TOOL","https://static.wikia.nocookie.net/songmeaning/images/1/13/Album-undertow.jpg/revision/latest?cb=20091006235818"));
    }

}
