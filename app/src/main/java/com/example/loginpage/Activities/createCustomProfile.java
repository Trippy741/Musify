package com.example.loginpage.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.R;
import com.example.loginpage.CustomDataTypes.local_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class createCustomProfile extends AppCompatActivity {
    private Context context;

    private final ImageView[][] profilesPics = new ImageView[2][3];
    private String selectedImageIndex = "-1";

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

                String profileID = "ic_profile_"+row+col;
                int profile_res_id = getResources().getIdentifier(profileID,"drawable",getPackageName());

                profilesPics[row][col].setImageResource(profile_res_id);
                profilesPics[row][col].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(selectedImageIndex != "-1")
                        {
                            profilesPics[Character.getNumericValue(selectedImageIndex.charAt(0))][Character.getNumericValue(selectedImageIndex.charAt(1))].setBackgroundResource(android.R.color.transparent);
                        }
                        selectedImageIndex = String.valueOf(imgID.charAt(4)) + imgID.charAt(5);
                        view.setBackgroundResource(R.drawable.stroke_purple);
                    }
                });
            }
        }

        Button setProfileButton = findViewById(R.id.signup_setProfilePic_button);
        setProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUser_db(FirebaseAuth.getInstance().getCurrentUser(),selectedImageIndex);
                startActivity(new Intent(createCustomProfile.this,EmailVerification.class));
            }
        });
    }
    @Override
    public void onBackPressed() {
        //TODO: Create an alert dialog asking if the user wants to terminate the registration process and return to login page.
        super.onBackPressed();
        finish();
    }
    private void insertUser_db(FirebaseUser createdUser,String profileIndex)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String user_id = "user_"+createdUser.getUid();

        local_user user = new local_user(user_id,createdUser.getDisplayName(),createdUser.getEmail(),profileIndex);

        //firebase store
        db.collection("users").document(user_id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(createCustomProfile.this, "User created & stored successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Failed to store user: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}