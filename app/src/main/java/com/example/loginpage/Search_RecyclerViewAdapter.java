package com.example.loginpage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Search_RecyclerViewAdapter extends RecyclerView.Adapter<Search_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    //private ArrayList<SearchQuery> searches = new ArrayList<SearchQuery>();
    private ArrayList<Object> searches = new ArrayList<>();
    private Context mContext;

    public Search_RecyclerViewAdapter(Context mContext,ArrayList<Object> queries) {
        this.searches = queries;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        if(searches.get(position).equals(Artist.class))
        {
            Artist tmp = (Artist)searches.get(position);
            holder.mainTitle.setText(tmp.artistTitle);
            holder.subTitle.setText("");
            Picasso.with(mContext).load(tmp.artistImage).into(holder.image);
        }
        else if(searches.get(position).equals(Album.class))
        {
            Album tmp = (Album) searches.get(position);
            holder.mainTitle.setText(tmp.albumTitle);
            holder.subTitle.setText("Album by: " + tmp.artistTitle);
            Picasso.with(mContext).load(tmp.albumImage).into(holder.image);
        }
        else if(searches.get(position).equals(Song.class))
        {
            Song tmp = (Song) searches.get(position);
            holder.mainTitle.setText(tmp.song_title);
            holder.subTitle.setText(tmp.artist_title);
            Picasso.with(mContext).load(tmp.image_URL).into(holder.image);
        }

        //holder.songName.setText(songNames.get(position));
        //holder.bandName.setText(bandNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Log.d(TAG,"onClick: clicked on: " + bandNames.get(position));
                //onClick
            }
        });
    }
    @Override
    public int getItemCount() {
        return searches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView mainTitle;
        TextView subTitle;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.search_item_imageView);
            mainTitle = itemView.findViewById(R.id.search_item_mainTitle);
            subTitle = itemView.findViewById(R.id.search_item_subTitle);
            parentLayout = itemView.findViewById(R.id.search_item_container);

            TextView menuText = itemView.findViewById(R.id.search_item_optionMenu);

            menuText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(mContext, menuText);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.song_options_menu);
                    Menu menu = popup.getMenu();

                    //adding click listener
                    /*popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                                    //Share(,);
                                    break;
                                case R.id.ReportButton:

                                    break;
                            }
                            return true;
                        }
                    });*/
                    //displaying the popup
                    popup.show();
                }
            });
        }
    }
}
