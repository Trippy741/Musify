package com.example.loginpage.RecyclerViewAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.CustomXML_elements.Custom2ChoiceAlertDialog;
import com.example.loginpage.CustomDataTypes.Album;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("images/user_"+ FirebaseAuth
                        .getInstance()
                        .getCurrentUser()
                        .getUid()+"/custom_playlists/"+albums.get(position).albumTitle);

        if(albums.get(position).albumImage != null && !albums.get(position).albumImage.isEmpty())
            Picasso.with(mContext).load(albums.get(position).albumImage).into(holder.albumImage);
        else if(!albums.get(position).imageURI.isEmpty() && albums.get(position).imageURI != null && albums.get(position).imgBitmap == null)
        {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.albumImage.setVisibility(View.INVISIBLE);
            try {
                final File localFile = File.createTempFile(albums.get(position).albumTitle,"jpg");
                storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        holder.albumImage.setImageBitmap(bitmap);
                        albums.get(position).imgBitmap = bitmap;
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.albumImage.setVisibility(View.VISIBLE);
                    }
                });
            } catch (IOException e) {
                Toast.makeText(mContext, "Error loading album image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(!albums.get(position).imageURI.isEmpty() && albums.get(position).imageURI != null && albums.get(position).imgBitmap != null)
            holder.albumImage.setImageBitmap(albums.get(position).imgBitmap);

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

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Custom2ChoiceAlertDialog d = new Custom2ChoiceAlertDialog(mContext,"Are you sure you want to remove " + albums.get(position).albumTitle
                        + " By " + albums.get(position).artistTitle + "?");

                View.OnClickListener confirmListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.startLoading();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document("user_"+ FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid())
                                .collection("user_custom_playlists")
                                .document(albums.get(position).album_id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(mContext, "Removed: " + albums.get(position).albumTitle, Toast.LENGTH_SHORT).show();
                                albums.remove(albums.get(position));
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
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumTitle = itemView.findViewById(R.id.custom_album_adapter_item_albumTitle);
            artistTitle = itemView.findViewById(R.id.custom_album_adapter_item_artistTitle);
            parentLayout = itemView.findViewById(R.id.custom_album_adapter_item_bg);
            albumImage = itemView.findViewById(R.id.custom_album_adapter_item_albumImageView);
            editButton = itemView.findViewById(R.id.custom_album_adapter_item_editImgV);
            progressBar = itemView.findViewById(R.id.custom_album_adapter_item_progressBar);
        }
    }
}
