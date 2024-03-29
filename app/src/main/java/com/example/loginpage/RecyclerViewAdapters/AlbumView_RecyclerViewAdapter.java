package com.example.loginpage.RecyclerViewAdapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.CustomXML_elements.Custom2ChoiceAlertDialog;
import com.example.loginpage.CustomDataTypes.Album;
import com.example.loginpage.Fragments.CurrentSongPlayingFragment;
import com.example.loginpage.R;
import com.example.loginpage.CustomDataTypes.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AlbumView_RecyclerViewAdapter extends RecyclerView.Adapter<AlbumView_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private Album album;
    private ArrayList<Song> songs = new ArrayList<Song>();
    private String albumTitle = "";
    private final Context mContext;

    private ViewHolder selectedHolder;

    private final FragmentManager fragmentManager;

    private Boolean editMode = false;

    public AlbumView_RecyclerViewAdapter(Context mContext,FragmentManager fragmentManager,Album album) {
        this.album = album;
        songs = album.songs;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    public AlbumView_RecyclerViewAdapter(Context mContext,FragmentManager fragmentManager,Album album,boolean editMode,String albumTitle) {
        this.album = album;
        songs = album.songs;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        this.editMode = editMode;
        this.albumTitle = albumTitle;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_view_song,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        holder.songName.setText(songs.get(position).song_title);
        holder.artistName.setText(songs.get(position).artist_title);
        
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedHolder == null)
                {
                    selectedHolder = holder;
                }

                selectedHolder.songName.setTextColor(view.getResources().getColor(R.color.white));
                selectedHolder.artistName.setTextColor(view.getResources().getColor(R.color.white));

                holder.songName.setTextColor(view.getResources().getColor(R.color.purple));
                holder.artistName.setTextColor(view.getResources().getColor(R.color.purple_lightgrey));

                //TODO: PLAY "POP" ANIMATION

                selectedHolder = holder;

                try {
                    Bundle bundle = new Bundle();

                    if(songs.size() > 1)
                    {
                        bundle.putParcelable("album_obj",album);
                        bundle.putInt("song_index",position);
                    }
                    else if(songs.size() == 1)
                        bundle.putParcelable("song_obj",(Song)songs.get(position));

                    if(album.imgBitmap != null)
                        songs.get(position).song_bitmap = album.imgBitmap;

                    CurrentSongPlayingFragment frag = new CurrentSongPlayingFragment();
                    frag.setArguments(bundle);
                    fragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_in_left
                    ).replace(R.id.fragment_container,frag).addToBackStack(null).commit();
                }catch (Exception e)
                {
                    Toast.makeText(mContext, "Error starting song playback: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(editMode == true)
        {
            holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Custom2ChoiceAlertDialog d = new Custom2ChoiceAlertDialog(mContext,"Are you sure you want to remove " + songs.get(position).song_title
                            + " By " + songs.get(position).artist_title + "?");

                    View.OnClickListener confirmListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.startLoading();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("artists")
                                    .document(songs.get(position).artist_title)
                                    .collection("albums")
                                    .document(albumTitle)
                                    .collection(songs.get(position).song_title).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    //TODO: REMOVE ACTUAL SONG FROM FIREBASE AND NOT ONLY FROM PROJECT

                                    Toast.makeText(mContext, "Removed: " + songs.get(position).song_title, Toast.LENGTH_SHORT).show();
                                    songs.remove(songs.get(position));
                                    notifyDataSetChanged();
                                    d.dismiss();
                                }
                            });

                        }
                    };

                    View.OnClickListener denyListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    };

                    d.setListeners(confirmListener,denyListener);
                    return true;
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView songName;
        TextView artistName;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.album_view_songName);
            artistName = itemView.findViewById(R.id.album_view_artistName);
            parentLayout = itemView.findViewById(R.id.album_view_container);

        }
    }
}
