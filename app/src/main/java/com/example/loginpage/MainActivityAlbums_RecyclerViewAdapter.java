package com.example.loginpage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivityAlbums_RecyclerViewAdapter extends RecyclerView.Adapter<MainActivityAlbums_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Album> albums = new ArrayList<Album>();
    private final FragmentManager fragmentManager;
    private final Context mContext;

    public MainActivityAlbums_RecyclerViewAdapter(Context mContext, FragmentManager fragmentManager, ArrayList<Album> albums) {
        this.albums = albums;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_albums_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        ImageView img = holder.imageView;
        Picasso.with(mContext).load(albums.get(position).albumImage).into(img);
        
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();

                Album parcelAlbum = albums.get(position);

                bundle.putParcelable("album_obj",parcelAlbum);

                AlbumView frag = new AlbumView();
                frag.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.fragment_container,frag).commit();

            }
        });
    }
    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.mainActivity_albums_imageView);

        }
    }
}
