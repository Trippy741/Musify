package com.example.loginpage;

import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class ArtistView extends Fragment {

    private TextView artistTitleTextView;
    private TextView artistQuoteTextView;
    private ImageView artistImageView;

    private ProgressBar progressBar;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artist_view, container, false);

        db = FirebaseFirestore.getInstance();

        artistTitleTextView = view.findViewById(R.id.artist_view_artistTitleTextView);
        artistQuoteTextView = view.findViewById(R.id.artist_view_artistQuoteTextView);
        artistImageView = view.findViewById(R.id.artist_view_artistImageImageView);
        progressBar = view.findViewById(R.id.artist_view_progressBar);

        artistTitleTextView.setVisibility(View.INVISIBLE);
        artistQuoteTextView.setVisibility(View.INVISIBLE);
        artistImageView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("artists").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        artistTitleTextView.setText(document.getId());
                        artistQuoteTextView.setText(document.get("artist_quote").toString());
                        Picasso.with(view.getContext()).load(document.get("artist_image_url").toString()).into(artistImageView);
                        artistImageView.setAdjustViewBounds(true);
                    }

                    artistTitleTextView.setVisibility(View.VISIBLE);
                    artistQuoteTextView.setVisibility(View.VISIBLE);
                    artistImageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    Toast.makeText(view.getContext(), "Fetch failed: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
