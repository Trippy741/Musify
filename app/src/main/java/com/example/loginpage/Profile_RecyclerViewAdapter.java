package com.example.loginpage;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Profile_RecyclerViewAdapter extends RecyclerView.Adapter<Profile_RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Integer> resourceIDs = new ArrayList<Integer>();
    private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
    private ArrayList<ImageView> strokes = new ArrayList<ImageView>();
    private CarouselRecyclerView recyclerView;

    private int currentEnlargedPosition = -1;

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
        focusImg(0);
        return holder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        holder.imageView.setImageResource(resourceIDs.get(position));
        imageViews.add(holder.imageView);
        strokes.add(holder.stroke);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Clicked!", Toast.LENGTH_SHORT).show();


            }
        });
    }
    public void focusImg(int position)
    {
        Toast.makeText(mContext, "Position: " + position, Toast.LENGTH_SHORT).show();



        for(int i = 0; i < imageViews.size();i++)
        {
            if(i != position)
            {
                strokes.get(i).setVisibility(View.INVISIBLE);
            }
            if(i == position)
            {
                strokes.get(position).setVisibility(View.VISIBLE);
            }
        }
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
        ImageView stroke;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.profile_fragment_imageView);
            stroke = itemView.findViewById(R.id.profile_fragment_stroke);

        }
    }
}
