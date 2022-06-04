package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recyclerview_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        ImageView img = holder.imageView;
        if(albums.get(position).albumImage != "")
        {
            Picasso.with(mContext).load(albums.get(position).albumImage).into(img);
        }else
        {
            loadAlbumImageFrom_URI(albums.get(position),img,holder.progressBar);
        }

        holder.artistText.setText(albums.get(position).artistTitle);
        holder.albumText.setText(albums.get(position).albumTitle);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();

                Album parcelAlbum = albums.get(position);

                bundle.putParcelable("album_obj",parcelAlbum);

                Custom_AlbumView frag = new Custom_AlbumView();
                frag.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.fragment_container,frag).commit();

            }
        });
    }
    public void loadAlbumImageFrom_URI(Album album, ImageView img,ProgressBar progressBar)
    {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("images/user_"+ FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid()+"/custom_playlists/"+album.albumTitle);
        if(!album.imageURI.isEmpty() && album.imageURI != null && album.imgBitmap == null)
        {
            try {
                final File localFile = File.createTempFile(album.albumTitle,"jpg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        album.imgBitmap = bitmap;
                        img.setImageBitmap(album.imgBitmap);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (IOException e) {
                Toast.makeText(mContext, "Error loading album image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public int getItemCount() {
        return albums.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout layout;
        ImageView imageView;
        TextView albumText;
        TextView artistText;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.home_recyclerview_item_layout);
            imageView = itemView.findViewById(R.id.home_recyclerview_item_imageView);
            albumText = itemView.findViewById(R.id.home_recyclerview_item_albumName);
            artistText = itemView.findViewById(R.id.home_recyclerview_item_artistName);
            progressBar = itemView.findViewById(R.id.home_recyclerview_item_progressBar);
        }
    }
}
