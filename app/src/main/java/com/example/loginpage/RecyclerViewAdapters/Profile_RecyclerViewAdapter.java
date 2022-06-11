package com.example.loginpage.RecyclerViewAdapters;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginpage.R;

import java.util.ArrayList;

public class Profile_RecyclerViewAdapter extends RecyclerView.Adapter<Profile_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Integer> resourceIDs = new ArrayList<Integer>();
    private final Context mContext;

    public Profile_RecyclerViewAdapter(Context mContext, ArrayList<Integer> resourceIDs) {
        this.resourceIDs = resourceIDs;
        this.mContext = mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_fragment_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        holder.imageView.setImageResource(resourceIDs.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();


            }
        });
    }
    @Override
    public int getItemCount() {
        return resourceIDs.size();
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void vibrate()
    {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.profile_fragment_imageView);
        }
    }
}
