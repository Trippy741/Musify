package com.example.loginpage;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class loading_custom_dialog extends Dialog {


    protected loading_custom_dialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
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
