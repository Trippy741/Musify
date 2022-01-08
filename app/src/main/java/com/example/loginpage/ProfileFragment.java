package com.example.loginpage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private Button takeProfilePicBtn;
    private ImageView profilePicImg;
    private View thisView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle saveInstanceState)
    {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }
    @Override
    public void onStart()
    {
        super.onStart();
        /*profilePicImg = thisView.findViewById(R.id.profilePicImg);
        takeProfilePicBtn = thisView.findViewById(R.id.takeProfilePicBtn);
        takeProfilePicBtn.setOnClickListener(this);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //profilePicImg.setImageBitmap(bitmap);
        }
    }

        @Override
        public void onClick(View view) {
            //profilePicImg = (ImageView) getView().findViewById(R.id.profilePicImg);

            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.CAMERA
                },100);
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,100);
        }
    }
