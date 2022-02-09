package com.example.loginpage;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.util.ArrayList;

public class ProfileFragment extends Fragment{

    private FirebaseFirestore db;
    private CarouselRecyclerview profilePicRecyclerView;

    private TextView mainTitle;
    private TextView subTitle;

    private ProgressButton confirmButton;

    private View view;

    private String UpdatedEmail;
    private String UpdatedName;


    private final ArrayList<Integer> profileResources = new ArrayList<Integer>();
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle saveInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        this.view = view;
        db = FirebaseFirestore.getInstance();//TODO: Use this to update the user profile credentials later on
        //profilePicRecyclerView = view.findViewById(R.id.profile_fragment_recyclerView);

        mainTitle = view.findViewById(R.id.profile_fragment_mainTitle);
        subTitle = view.findViewById(R.id.profile_fragment_subTitle);
        View buttonView = view.findViewById(R.id.profile_fragment_update_button);

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

        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                String profileID = "ic_profile_"+i+j;
                int profile_res_id = getResources().getIdentifier(profileID,"drawable", "com.example.loginpage");
                profileResources.add(profile_res_id);
            }
        }

        /*Profile_RecyclerViewAdapter adapter = new Profile_RecyclerViewAdapter(view.getContext(),profileResources);
        profilePicRecyclerView.setAdapter(adapter);

        CarouselRecyclerView carouselRecyclerView = new CarouselRecyclerView();
        carouselRecyclerView.Bind(adapter,profilePicRecyclerView);*/

        return view;
    }
    private void openChangeMainTitleDialog()
    {
        Dialog d = new Dialog(view.getContext(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_profile_change_titles_dialog);


        TextInputLayout textLayout =  d.getWindow().findViewById(R.id.custom_profileTitlesChange_editText);
         UpdatedName = textLayout.getEditText().getEditableText().toString();

        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainTitle.setText(textLayout.getEditText().getEditableText().toString()+" (Not Saved)");
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
        UpdatedEmail = textLayout.getEditText().getEditableText().toString();

        d.getWindow().findViewById(R.id.custom_profileTitlesChange_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subTitle.setText(textLayout.getEditText().getEditableText().toString()+" (Not Saved)");
                d.dismiss();
            }
        });

        d.show();
    }
    @Override
    public void onStart()
    {
        super.onStart();
    }
    private void updateProfile()
    {
        //FirebaseAuth.getInstance().getCurrentUser().updateProfile();
    }
}
