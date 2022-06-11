package com.example.loginpage.CustomDialogs;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.loginpage.R;

public class loading_custom_dialog extends Dialog {


    public loading_custom_dialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.custom_loading_dialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
