package com.example.loginpage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAlbum_RecyclerViewAdapter extends RecyclerView.Adapter<CustomAlbum_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Album> albums = new ArrayList<Album>();
    private final Context mContext;

    private ViewHolder selectedHolder;

    private final FragmentManager fragmentManager;

    private final Boolean liked = false;

    public CustomAlbum_RecyclerViewAdapter(Context mContext,FragmentManager fragmentManager,ArrayList<Album> albums) {
        this.albums = albums;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_album_adapter_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        holder.albumTitle.setText(albums.get(position).albumTitle);
        holder.artistTitle.setText(albums.get(position).artistTitle);

        if(albums.get(position).albumImage != null && !albums.get(position).albumImage.isEmpty())
            Picasso.with(mContext).load(albums.get(position).albumImage).into(holder.albumImage);
        else if(!albums.get(position).imageURI.isEmpty() && albums.get(position).imageURI != null)
            holder.albumImage.setImageURI(Uri.parse(albums.get(position).imageURI));

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Edit Mode", Toast.LENGTH_SHORT).show();
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedHolder == null)
                {
                    selectedHolder = holder;
                }

                selectedHolder.albumTitle.setTextColor(view.getResources().getColor(R.color.white));
                selectedHolder.artistTitle.setTextColor(view.getResources().getColor(R.color.white));

                holder.albumTitle.setTextColor(view.getResources().getColor(R.color.purple));
                holder.artistTitle.setTextColor(view.getResources().getColor(R.color.purple_lightgrey));

                //TODO: PLAY "POP" ANIMATION

                selectedHolder = holder;

                Custom_AlbumView frag = new Custom_AlbumView();

                Bundle bundle = new Bundle();
                bundle.putParcelable("album_obj",albums.get(position));
                frag.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.fragment_container,frag).addToBackStack(null).commit();

                try {

                }catch (Exception e)
                {
                    Toast.makeText(mContext, "Error loading Custom Album: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void addNewAlbums(ArrayList<Album> newAlbums)
    {
        for (Album a :
                newAlbums) {
            albums.add(a);
        }
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView albumTitle;
        TextView artistTitle;
        RelativeLayout parentLayout;
        ImageView albumImage;
        ImageView editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumTitle = itemView.findViewById(R.id.custom_album_adapter_item_albumTitle);
            artistTitle = itemView.findViewById(R.id.custom_album_adapter_item_artistTitle);
            parentLayout = itemView.findViewById(R.id.custom_album_adapter_item_bg);
            albumImage = itemView.findViewById(R.id.custom_album_adapter_item_albumImageView);
            editButton = itemView.findViewById(R.id.custom_album_adapter_item_editImgV);
        }
    }
}
