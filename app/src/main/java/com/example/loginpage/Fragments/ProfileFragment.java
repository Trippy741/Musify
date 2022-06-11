package com.example.loginpage.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.example.loginpage.Activities.LoginPage;
import com.example.loginpage.Activities.MainActivity;
import com.example.loginpage.RecyclerViewAdapters.CarouselRecyclerView;
import com.example.loginpage.RecyclerViewAdapters.Profile_RecyclerViewAdapter;
import com.example.loginpage.CustomXML_elements.ProgressButton;
import com.example.loginpage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.util.ArrayList;
import java.util.Locale;

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

        mainTitle.setText(auth.getCurrentUser().getDisplayName().toString());
        subTitle.setText(auth.getCurrentUser().getEmail().toString());

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
                db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if((boolean)task.getResult().get("isUsingGoogle"))
                            {
                                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                                alert.setTitle("Sorry, Can't Do that :(");
                                alert.setMessage("Since you're logged in with an existing google account you can't change your E-mail address.");
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                alert.create().show();
                            }
                            else
                                openChangeSubTitleDialog();
                        }
                    }
                });

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
                if(task.isSuccessful() && task.getResult() != null)
                {
                    String profilePicIndex = "-1";
                    DocumentSnapshot document = task.getResult();

                    String profile_index = "-1";

                    if(document.get("profile_pic_index") != null)
                        profile_index = document.get("profile_pic_index").toString();

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
        textLayout.setHint("Updated Name");
        textLayout.setEndIconDrawable(R.drawable.ic_profile_pic_icon);


        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatedName = textLayout.getEditText().getEditableText().toString();
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
        textLayout.setHint("Updated Email");
        textLayout.setEndIconDrawable(R.drawable.ic_mail_icon);


        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatedEmail = textLayout.getEditText().getEditableText().toString();
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

        d.getWindow().findViewById(R.id.custom_profilePicChange_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Hey! Aren't you forgetting something?");
                alert.setMessage("Are you sure you want to dismiss this dialog and discard your changes?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        d.dismiss();
                        Toast.makeText(view.getContext(), "Discarding Changes...", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Exists alert dialog
                    }
                });
                alert.create().show();
            }
        });
        d.getWindow().findViewById(R.id.custom_profilePicChange_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int profilePosition = carouselRecyclerView.getCarouselPosition();
                    updatedProfileIndex = fromPositionToIndexProfile(profilePosition);

                    String profileID = "ic_profile_"+updatedProfileIndex;
                    int profile_res_id = getResources().getIdentifier(profileID,"drawable", "com.example.loginpage");

                    profilePicImgView.setImageResource(profile_res_id);
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
    private void openConfirmPasswordDialog()
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_update_profile_confirmpass_dialog);


        TextInputLayout textLayout = d.getWindow().findViewById(R.id.custom_profileConfirmPassword_TIL);
        textLayout.setHint("Confirm Password");
        textLayout.setEndIconDrawable(R.drawable.ic_password_icon);

        ProgressBar progressBar = d.getWindow().findViewById(R.id.custom_profileConfirmPassword_progressBar);

        d.getWindow().findViewById(R.id.custom_profileConfirmPassword_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Hey! Aren't you forgetting something?");
                alert.setMessage("Are you sure you want to dismiss this dialog and discard your changes?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        d.dismiss();
                        Toast.makeText(view.getContext(), "Discarding Changes...", Toast.LENGTH_SHORT).show();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Exists alert dialog
                    }
                });
                alert.create().show();
            }
        });
        d.getWindow().findViewById(R.id.custom_profileConfirmPassword_confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); //Making the progress bar visible
                d.getWindow().findViewById(R.id.custom_profileConfirmPassword_confirmButton).setVisibility(View.INVISIBLE);
                String pass = textLayout.getEditText().getEditableText().toString();
                if(pass != "")
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail().toString().toLowerCase(Locale.ROOT),pass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressBar.setVisibility(View.INVISIBLE); //Making the progress bar invisible
                                d.getWindow().findViewById(R.id.custom_profileConfirmPassword_confirmButton).setVisibility(View.VISIBLE);
                                d.dismiss();
                                Toast.makeText(view.getContext(), "Profile Successfully Updated!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                                d.getWindow().findViewById(R.id.custom_profileConfirmPassword_confirmButton).setVisibility(View.VISIBLE);
                                Toast.makeText(view.getContext(), "Failed Updating Profile Details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
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
        if(UpdatedName == FirebaseAuth.getInstance().getCurrentUser().getDisplayName() || UpdatedName == null && UpdatedEmail == FirebaseAuth.getInstance().getCurrentUser().getEmail() || UpdatedEmail == null)
        {
            updateProfilePic();
            ((MainActivity)getActivity()).updateProfileUI();
        }
        if(UpdatedEmail == FirebaseAuth.getInstance().getCurrentUser().getEmail() || UpdatedEmail == null && UpdatedName != FirebaseAuth.getInstance().getCurrentUser().getDisplayName() || UpdatedName != null )
        {
            updateDisplayName();
            ((MainActivity)getActivity()).updateProfileUI();
        }
        else
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(mainTitle.getText().toString())
                    .build();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            openConfirmPasswordDialog();
            updateProfileDialog(user,profileUpdates);
            ((MainActivity)getActivity()).updateProfileUI();
        }
    }
    private void updateProfilePic()
    {
        db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).update("profile_pic_index",updatedProfileIndex).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(view.getContext(), "Successfully Changed Profile Pic!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateDisplayName()
    {
        db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).update("displayName",UpdatedName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(view.getContext(), "Successfully Updated Display Name!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateProfileDialog(FirebaseUser user,UserProfileChangeRequest profileUpdates)
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_update_profile_dialog);
        //Profile Pic
        db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).update("profile_pic_index",updatedProfileIndex).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ProgressBar progressBar = d.getWindow().findViewById(R.id.custom_profile_update_profilePicture_progressBar);
                        ImageView img = d.getWindow().findViewById(R.id.custom_profile_update_profilePicture_image);

                        if(!task.isSuccessful())
                        {
                            img.setImageResource(R.drawable.ic_x_mark);
                            Toast.makeText(view.getContext(), "Failed to update user profile picture: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        img.setVisibility(View.VISIBLE);
                    }
                });
        //Email
        user.updateEmail(subTitle.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                ProgressBar progressBar = d.getWindow().findViewById(R.id.custom_profile_update_displayName_progressBar);
                ImageView img = d.getWindow().findViewById(R.id.custom_profile_update_displayName_image);

                if(!task.isSuccessful())
                {
                    img.setImageResource(R.drawable.ic_x_mark);
                    Toast.makeText(view.getContext(), "Failed to update user profile picture: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
                img.setVisibility(View.VISIBLE);
            }
        });
        //Display Name
        db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).update("displayName",UpdatedName).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ProgressBar progressBar = d.getWindow().findViewById(R.id.custom_profile_update_displayName_progressBar);
                        ImageView img = d.getWindow().findViewById(R.id.custom_profile_update_displayName_image);

                        if(!task.isSuccessful())
                        {
                            img.setImageResource(R.drawable.ic_x_mark);
                            Toast.makeText(view.getContext(), "Failed to update user profile picture: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        img.setVisibility(View.VISIBLE);
                    }
                });
        d.getWindow().findViewById(R.id.custom_profile_update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainTitle.setText(profileUpdates.getDisplayName());
                d.dismiss();
            }
        });
        d.getWindow().findViewById(R.id.custom_profile_update_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }
}
