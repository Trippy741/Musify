package com.example.loginpage;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Custom2ChoiceAlertDialog extends Dialog {

    private Layout DialogLayout;
    private String customString = "";
    private TextView textView;
    private ImageView confirmButton;
    private ImageView denyButton;
    private ProgressBar loadingBar;

    public Custom2ChoiceAlertDialog(@NonNull Context context,String customString) {
        super(context);
        this.customString = customString;
        Init();
    }

    public Custom2ChoiceAlertDialog(@NonNull Context context,String customString , int themeResId) {
        super(context, themeResId);
        this.customString = customString;
        Init();
    }

    protected Custom2ChoiceAlertDialog(@NonNull Context context,String customString , boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.customString = customString;
        Init();
    }
    public void Init()
    {
        Dialog d = this;
        d.setContentView(R.layout.custom_alert_dialog);

        confirmButton = d.getWindow().findViewById(R.id.alert_dialog_2Choices_confirmButton);
        denyButton = d.getWindow().findViewById(R.id.alert_dialog_2Choices_denyButton);
        textView = d.getWindow().findViewById(R.id.alert_dialog_2Choices_textView);
        loadingBar = d.getWindow().findViewById(R.id.alert_dialog_2Choices_loadingBar);

        textView.setText(customString);

        this.show();
    }
    public void startLoading()
    {
        loadingBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);
        confirmButton.setVisibility(View.INVISIBLE);
        denyButton.setVisibility(View.INVISIBLE);
    }
    public void setListeners(View.OnClickListener confirmListener,View.OnClickListener denyListener)
    {
        confirmButton.setOnClickListener(confirmListener);
        denyButton.setOnClickListener(denyListener);
    }
}
