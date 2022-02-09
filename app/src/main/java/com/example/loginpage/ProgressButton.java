package com.example.loginpage;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton {

    private final CardView cardView;
    private final ProgressBar progressBar;
    private final TextView textView;
    private final ConstraintLayout constraintLayout;

    Animation fade_in;

    ProgressButton(Context ct, View view){
        cardView = view.findViewById(R.id.progressButton_cardView);
        progressBar = view.findViewById(R.id.progressButton_progressBar);
        textView = view.findViewById(R.id.progressButton_textView);
        constraintLayout = view.findViewById(R.id.progressButton_constraintLayout);

        progressBar.setVisibility(View.INVISIBLE);
    }
    void changeButtonText(String text)
    {
        textView.setText(text);
    }
    void onButtonClick(boolean progressBarVisibilty,String text)
    {
        if(progressBarVisibilty)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.INVISIBLE);

        textView.setText(text);
    }
    void onButtonFinishedLoading(int color)
    {
        constraintLayout.setBackgroundColor(color);
        progressBar.setVisibility(View.GONE);
    }
}
