package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LikedSongs_RecyclerViewAdapter extends RecyclerView.Adapter<LikedSongs_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> songImages = new ArrayList<>();
    private ArrayList<String> bandNames = new ArrayList<>();
    private ArrayList<String> songNames = new ArrayList<>();
    private final Context mContext;

    private Boolean liked = false;

    private Animation Liked_pop;
    private Animation Disliked_pop;

    public LikedSongs_RecyclerViewAdapter(Context mContext, ArrayList<String> songImages, ArrayList<String> bandNames, ArrayList<String> songNames) {
        this.songImages = songImages;
        this.bandNames = bandNames;
        this.songNames = songNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view);

        Liked_pop = AnimationUtils.loadAnimation(mContext,R.anim.pop);
        Disliked_pop = AnimationUtils.loadAnimation(mContext,R.anim.dis_pop);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        Glide.with(mContext).asBitmap().load(songImages.get(position)).into(holder.songImage);
        holder.songName.setText(songNames.get(position));
        holder.bandName.setText(bandNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(TAG,"onClick: clicked on: " + bandNames.get(position));
                //onClick
            }
        });
    }

    public void UnfavoriteSong(ImageView likeButton)
    {
        likeButton.setImageResource(R.drawable.ic_likedborder);
        likeButton.startAnimation(Disliked_pop);
        liked = false;
    }
    public void FavoriteSong(ImageView likeButton)
    {
        likeButton.setImageResource(R.drawable.ic_likedsongsicon);
        likeButton.startAnimation(Liked_pop);
        liked = true;
    }
    public void Share(String songName, String bandName)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "You Should really listen to: " + songName + " by: " + bandName);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        mContext.startActivity(shareIntent);
    }
    @Override
    public int getItemCount() {
        return songImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView songImage;
        ImageView likeButton;
        TextView songName;
        TextView bandName;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.ImageListItem);
            likeButton = itemView.findViewById(R.id.likeSong);
            songName = itemView.findViewById(R.id.item_SongName);
            bandName = itemView.findViewById(R.id.subItem_bandName);
            parentLayout = itemView.findViewById(R.id.parent_layout);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(liked == true)
                    {
                        UnfavoriteSong(likeButton);
                    }
                    else if(liked == false)
                    {
                        FavoriteSong(likeButton);
                    }
                }
            });

            TextView menuText = itemView.findViewById(R.id.songOptions);

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
            });
        }
    }
}
