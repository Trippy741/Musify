package com.example.loginpage;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.util.ArrayList;

public class ProfileFragment extends Fragment{

    private FirebaseFirestore db;
    private CarouselRecyclerview profilePicRecyclerView;

    private ImageView profilePicImgView;
    private TextView mainTitle;
    private TextView subTitle;

    private ProgressButton confirmButton;
    private ProgressBar progressBar;

    private View view;

    private String UpdatedEmail;
    private String UpdatedName;
    private String updatedProfileIndex;

    private boolean isUpdatedName = false;
    private boolean isUpdatedEmail = false;
    private boolean isUpdatedProfile = false;


    private final ArrayList<Integer> profileResources = new ArrayList<Integer>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle saveInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        this.view = view;
        db = FirebaseFirestore.getInstance();//TODO: Use this to update the user profile credentials later on

        profilePicImgView = view.findViewById(R.id.profile_fragment_profileImage);
        progressBar = view.findViewById(R.id.profile_fragment_progressBar);
        mainTitle = view.findViewById(R.id.profile_fragment_mainTitle);
        subTitle = view.findViewById(R.id.profile_fragment_subTitle);
        View buttonView = view.findViewById(R.id.profile_fragment_update_button);

        progressBar.setVisibility(View.VISIBLE);

        confirmButton = new ProgressButton(view.getContext(),buttonView);
        confirmButton.changeButtonText("Update Profile Details");

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();

        mainTitle.setText(auth.getCurrentUser().getDisplayName());
        subTitle.setText(auth.getCurrentUser().getEmail());

        profilePicImgView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                openProfileChangeDialog();
            }
        });
        mainTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeMainTitleDialog();
            }
        });
        subTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeSubTitleDialog();
            }
        });


        //Loading the profile pics into the ArrayList
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                String profileID = "ic_profile_"+i+j;
                int profile_res_id = getResources().getIdentifier(profileID,"drawable", "com.example.loginpage");
                profileResources.add(profile_res_id);
            }
        }

        updateUI();

        return view;
    }
    private void updateUI()
    {
        DocumentReference docRef = db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String profilePicIndex = "-1";
                    DocumentSnapshot document = task.getResult();
                    String profile_index = document.get("profile_pic_index").toString();
                    if (document.exists() && !profile_index.isEmpty()) {
                        profilePicIndex = profile_index;
                    } else {
                        profilePicIndex = "-1";
                    }

                    String profileID = "ic_profile_def";

                    if(profilePicIndex != "-1")
                    {
                        profileID = "ic_profile_"+profilePicIndex;
                    }

                    int resID = getResources().getIdentifier(profileID,"drawable","com.example.loginpage");
                    profilePicImgView.setImageResource(resID);
                    progressBar.setVisibility(View.INVISIBLE);
                }else
                    Toast.makeText(view.getContext(), "Failed to update user UI elements: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openChangeMainTitleDialog()
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_profile_change_titles_dialog);


        TextInputLayout textLayout =  d.getWindow().findViewById(R.id.custom_profileTitlesChange_editText);
        textLayout.getEditText().setHint("Updated Name");
        textLayout.setEndIconDrawable(R.drawable.ic_profile_pic_icon);
         UpdatedName = textLayout.getEditText().getEditableText().toString();

        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainTitle.setText(UpdatedName);
                d.dismiss();
            }
        });

        //d.onBackPressed();
        d.show();
    }
    private void openChangeSubTitleDialog()
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_profile_change_titles_dialog);


        TextInputLayout textLayout = d.getWindow().findViewById(R.id.custom_profileTitlesChange_editText);
        textLayout.getEditText().setHint("Updated Email");
        textLayout.setEndIconDrawable(R.drawable.ic_mail_icon);
        UpdatedEmail = textLayout.getEditText().getEditableText().toString();

        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subTitle.setText(UpdatedEmail);
                d.dismiss();
            }
        });

        d.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openProfileChangeDialog()
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_profile_change_profile_picture_dialog);

        profilePicRecyclerView = d.getWindow().findViewById(R.id.profile_fragment_recyclerView);

        Profile_RecyclerViewAdapter adapter = new Profile_RecyclerViewAdapter(view.getContext(),profileResources);
        profilePicRecyclerView.setAdapter(adapter);

        CarouselRecyclerView carouselRecyclerView = new CarouselRecyclerView();
        carouselRecyclerView.Bind(adapter,profilePicRecyclerView);

        int profilePosition = carouselRecyclerView.getCarouselPosition();

        d.getWindow().findViewById(R.id.custom_profilePicChange_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.getWindow().findViewById(R.id.custom_profilePicChange_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updatedProfileIndex = fromPositionToIndexProfile(profilePosition);
                }catch (Exception e)
                {
                    Toast.makeText(view.getContext(),"Failed to update profile pic: " + e, Toast.LENGTH_SHORT).show();
                }
                d.dismiss();
            }
        });
        d.getWindow().findViewById(R.id.custom_profilePicChange_deny_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }
    private String fromPositionToIndexProfile(int position)
    {
        String returnString = "";
        switch (position)
        {
            case 0:
                returnString = "00";
                break;
            case 1:
                returnString = "01";
                break;
            case 2:
                returnString = "02";
                break;
            case 3:
                returnString = "10";
                break;
            case 4:
                returnString = "11";
                break;
            case 5:
                returnString = "12";
                break;
        }
        return returnString;
    }
    @Override
    public void onStart()
    {
        super.onStart();
    }
    private void updateProfile()
    {
        isUpdatedName = false;
        isUpdatedEmail = false;
        isUpdatedProfile = false;


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mainTitle.getText().toString())
                .build();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                isUpdatedName = true;
                user.updateEmail(subTitle.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            isUpdatedEmail = true;
                            db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).update("profile_pic_index",updatedProfileIndex).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                isUpdatedProfile = true;
                                            }
                                            else
                                            {
                                                isUpdatedProfile = false;
                                                Toast.makeText(view.getContext(), "Failed to update user profile picture: " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                            updateProfileDialog(isUpdatedName,isUpdatedEmail,isUpdatedProfile);
                                        }
                                    });
                        }else
                        {
                            Toast.makeText(view.getContext(), "Failed to update user email address: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else
            {
                Toast.makeText(view.getContext(), "Failed to update user display name: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        }
    });
    }
    private void updateProfileDialog(boolean isUpdatedName, boolean isUpdatedMail, boolean isUpdatedProfile)
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_update_profile_dialog);

        if(!isUpdatedName)
        {
            ImageView img = d.getWindow().findViewById(R.id.updateProfile_dialog_displayName_check);
            img.setImageResource(R.drawable.ic_x_mark);
        }
        if(!isUpdatedMail)
        {
            ImageView img = d.getWindow().findViewById(R.id.updateProfile_dialog_emailAddress_check);
            img.setImageResource(R.drawable.ic_x_mark);
        }
        if(!isUpdatedProfile)
        {
            ImageView img = d.getWindow().findViewById(R.id.updateProfile_dialog_profilePic_check);
            img.setImageResource(R.drawable.ic_x_mark);
        }

        d.getWindow().findViewById(R.id.updateProfile_dialog_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.getWindow().findViewById(R.id.updateProfile_dialog_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }
}
