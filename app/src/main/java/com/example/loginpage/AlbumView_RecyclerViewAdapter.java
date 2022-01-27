package com.example.loginpage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumView_RecyclerViewAdapter extends RecyclerView.Adapter<AlbumView_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Song> songs = new ArrayList<Song>();
    private Context mContext;

    private ViewHolder selectedHolder;

    private Boolean liked = false;

    public AlbumView_RecyclerViewAdapter(Context mContext,ArrayList<Song> songs) {
        this.songs = songs;
        this.mContext = mContext;
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

        holder.songName.setText(songs.get(position).name_song);
        holder.artistName.setText(songs.get(position).name_artist);
        
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();

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
            }
        });
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

            /*TextView menuText = itemView.findViewById(R.id.songOptions);

            menuText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, menuText);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.song_options_menu);
                    Menu menu = popup.getMenu();
                    if(liked == true)
                        menu.getItem(0).setTitle("Unfavorite");
                    else
                        menu.getItem(0).setTitle("Favorite");

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.favButton:
                                    if(liked == true)
                                        UnfavoriteSong(likeButton);
                                    else
                                        FavoriteSong(likeButton);
                                    break;
                                case R.id.ShareButton:
                                    //Share(,); //TODO: WHEN YOU FIGURE OUT HOW TO LOAD SONG TITLES AND IMAGES AND STUFF LIKE THAT WRITE THE SHARE CODE THING
                                    break;
                                case R.id.ReportButton:

                                    break;
                            }
                            return true;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });*/
        }
    }
}
