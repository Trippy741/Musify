package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class createCustomProfile extends AppCompatActivity {
    private Context context;

    private ImageView[][] profilesPics = new ImageView[2][3];
    private int selectedImageIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_custom_profile);

        context = this;

        clickListeners();
    }

    private void clickListeners()
    {
        for (int row = 0; row < 2; row++)
        {
            for (int col = 0; col < 3; col++)
            {
                String imgID = "img_"+row+col;
                int resID = getResources().getIdentifier(imgID,"id",getPackageName());
                profilesPics[row][col] = findViewById(resID);
                profilesPics[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedImageIndex != -1)
                        {
                            profilesPics[selectedImageIndex/10][selectedImageIndex%10].setBackgroundResource(android.R.color.transparent);
                        }
                        selectedImageIndex = (Character.getNumericValue(imgID.charAt(4))*10) + Character.getNumericValue(imgID.charAt(5));
                        view.setBackgroundResource(R.drawable.stroke_purple);
                    }
                });
            }
        }


        TextView skipText = findViewById(R.id.signup_skip_textView);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Moves to different activity
                insertUser_db(FirebaseAuth.getInstance().getCurrentUser(),0);
                startActivity(new Intent(context,MainActivity.class));
                finish();
            }
        });

        Button setProfileButton = findViewById(R.id.signup_setProfilePic_button);
        setProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUser_db(FirebaseAuth.getInstance().getCurrentUser(),selectedImageIndex);
            }
        });
    }
    @Override
    public void onBackPressed() {
        //TODO: Create an alert dialog asking if the user wants to terminate the registration process and return to login page.
        super.onBackPressed();
        finish();
    }
    private void insertUser_db(FirebaseUser createdUser,int profileIndex)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        local_user user = new local_user(createdUser.getDisplayName(),createdUser.getUid(),profileIndex);
        db.collection("users").add("");
    }
}