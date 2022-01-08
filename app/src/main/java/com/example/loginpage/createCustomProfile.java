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


public class createCustomProfile extends AppCompatActivity {
    private Context context;
    private ImageView profilePicImg;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_custom_profile);

        context = this;

        profilePicImg = findViewById(R.id.signup_profilePic_CircleImageView);

        storageReference = FirebaseStorage.getInstance().getReference();

        clickListeners();
        animateGradient();
    }

    private void clickListeners()
    {
        TextView skipText = findViewById(R.id.signup_skip_textView);
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Moves to different activity
                startActivity(new Intent(context,MainActivity.class));
                finish();
            }
        });

        Button setProfileButton = findViewById(R.id.signup_setProfilePic_button);
        setProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,1000);
            }
        });
    }
    @Override
    public void onBackPressed() {
        //TODO: Create an alert dialog asking if the user wants to terminate the registration process and return to login page.
        super.onBackPressed();
        finish();
    }
    private void animateGradient()
    {
        ImageView glowingCircle = findViewById(R.id.createProfile_glowingCircle);
        AnimationDrawable animationDrawable = (AnimationDrawable) glowingCircle.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000)
        {
            Uri imageURI = data.getData();
            profilePicImg.setImageURI(imageURI);
            uploadImageToFirebase(imageURI);
        }
    }
    private void uploadImageToFirebase(Uri imageURI)
    {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference fireref = storageReference.child(uid+"/profile.jpg");

        fireref.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}