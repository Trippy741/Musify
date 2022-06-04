package com.example.loginpage;

import static com.example.loginpage.MainActivity.contextOfApplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View v;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        v = view;
        initAlbumCarouselRecyclerView();
        return view;
    }
    private void initAlbumCarouselRecyclerView()
    {

        String user_id = "user_" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {//Collection Exists

            db.collection("users")
                    .document(user_id)
                    .collection("user_custom_playlists")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        db.collection("users").document(user_id).collection("user_custom_playlists").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    ArrayList<Album> temp_albums = new ArrayList<Album>();
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        temp_albums.add(doc.toObject(Album.class));
                                    }
                                    MainActivityAlbums_RecyclerViewAdapter albumAdapter = new MainActivityAlbums_RecyclerViewAdapter(v.getContext(),getActivity().getSupportFragmentManager(),temp_albums);
                                    RecyclerView customAlbumRecyclerView = v.findViewById(R.id.home_customPlaylists_recyclerView);
                                    customAlbumRecyclerView.setAdapter(albumAdapter);
                                }
                            }
                        });
                    }
                }
            });
        }catch (Exception e)//Collection DOES NOT Exist
        {
            Toast.makeText(contextOfApplication, "Failed to load custom Playlists: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
