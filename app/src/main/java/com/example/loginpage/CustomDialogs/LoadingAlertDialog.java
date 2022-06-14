package com.example.loginpage.CustomDialogs;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.loginpage.R;

public class LoadingAlertDialog{

    private Context context;
    private Dialog dialog;

    public LoadingAlertDialog(@NonNull Context context) {
        this.context = context;
    }
    public void show()
    {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.show();
    }
    public void dismiss()
    {
        dialog.dismiss();
    }
}
