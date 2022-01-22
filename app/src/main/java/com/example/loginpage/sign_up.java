package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class sign_up extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Button testButton;

    private TextInputLayout displayTIL;
    private TextInputLayout emailTIL;
    private TextInputLayout passwordTIL;
    private TextInputLayout confirmPasswordTIL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }

        super.onCreate(savedInstanceState);


        Log.d("MyApp","test");

        setContentView(R.layout.activity_sign_up);

        displayTIL = findViewById(R.id.signup_displayName_textInputLayout);
        emailTIL = findViewById(R.id.signup_email_textInputLayout);
        passwordTIL = findViewById(R.id.signup_password_TextInputLayout);
        confirmPasswordTIL = findViewById(R.id.signup_confirmPassword_textInputLayout);

        passwordTIL.setEndIconActivated(false);
        confirmPasswordTIL.setEndIconActivated(false);

        firebaseAuth = FirebaseAuth.getInstance();

        View signupButtonView = findViewById(R.id.signup_button_View);

        ProgressButton progressButton = new ProgressButton(sign_up.this,signupButtonView);
        progressButton.changeButtonText("Sign Up");

        signupButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupButtonClick(view); //TODO: CHANGE THIS AND IMPLEMENT AN ONCLICKLISTENER PLEASE
            }
        });

        testButton = findViewById(R.id.testProfileButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),createCustomProfile.class));
                overridePendingTransition(R.anim.slide_from_top_to_center, R.anim.slide_from_center_to_bottom);
                //finish();
            }
        });

        gradientAnimation();
        textListeners();
        showIntroDialog();

    }
    private void showIntroDialog()
    {
        Dialog d = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_signup_instructions_dialog);

        Button closeButton = d.findViewById(R.id.signup_instructions_confirm_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });


        d.getWindow().setWindowAnimations(R.style.CustomDialogAnims_slide);

        //TODO: ANIMATE THE LINEAR LAYOUT OF THE DIALOG AND FADE THE BACKGROUND (APPLY THE PATHINTERPOLATOR) INSTEAD OF RELYING ON BUILT IN ANIM STATES

        d.show();
    }
    private void gradientAnimation()
    {
        LinearLayout signUpPageLayout = findViewById(R.id.signUpPageLayout);
        AnimationDrawable animationDrawable1 = (AnimationDrawable) signUpPageLayout.getBackground();
        animationDrawable1.setEnterFadeDuration(2000);
        animationDrawable1.setExitFadeDuration(4000);
        animationDrawable1.start();
    }
    private void textListeners()
    {

        emailTIL.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailString = emailTIL.getEditText().getText().toString();
                Boolean isCorrectlyFormatted = android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches();

                if(isCorrectlyFormatted != true)
                {
                    emailTIL.setErrorEnabled(true);
                    emailTIL.setError("Invalid email Address!");
                }
                else
                {
                    emailTIL.setErrorEnabled(false);
                }
            }
        });

        passwordTIL.getEditText().addTextChangedListener(new TextWatcher() {

            String string = passwordTIL.getEditText().getText().toString();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() <= 6)
                {
                    passwordTIL.setErrorEnabled(true);
                    passwordTIL.setError("Current password is Too short!");
                }
                else if(containsUpperOrSpecialChars(editable.toString()) != true)
                {
                    passwordTIL.setErrorEnabled(true);
                    passwordTIL.setError("Password MUST contain atleast 1 digit,special and uppercase letter!");
                }
                else
                {
                    passwordTIL.setErrorEnabled(false);
                }
            }
        });

        confirmPasswordTIL.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!passwordTIL.getEditText().getText().toString().equals(confirmPasswordTIL.getEditText().getText().toString()))
                {
                    confirmPasswordTIL.setErrorEnabled(true);
                    confirmPasswordTIL.setError("Passwords do not match!");
                }
                else if(editable.toString().isEmpty())
                {
                    confirmPasswordTIL.setErrorEnabled(true);
                    confirmPasswordTIL.setError("*Required Field");
                }
                else if(!editable.toString().isEmpty())
                {
                    confirmPasswordTIL.setErrorEnabled(false);
                }
                else
                {
                    confirmPasswordTIL.setErrorEnabled(false);
                }
            }
        });
    }
    private boolean containsUpperOrSpecialChars(String string)
    {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        boolean isSpecial = m.find();

        boolean isUppercase = false;
        boolean isDigit = false;

        for(int i=0;i<string.length();i++){
            if(Character.isUpperCase(string.charAt(i))){
                isUppercase = true;
            }
            if(Character.isDigit(string.charAt(i)))
            {
                isDigit = true;
            }
        }

        if(isSpecial && isDigit && isUppercase)
        {
            return true;
        }
        return false;
    }

    public void signupButtonClick(View view) {

        String emailString = emailTIL.getEditText().getText().toString();
        String passwordString = passwordTIL.getEditText().getText().toString();
        String confirmPasswordString = confirmPasswordTIL.getEditText().getText().toString();

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            Toast.makeText(sign_up.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
        }
        else if (passwordString.isEmpty()) {
            Toast.makeText(sign_up.this, "Empty Password String", Toast.LENGTH_SHORT).show();
        }
        else if(!passwordString.equals(confirmPasswordString)) {
            Toast.makeText(sign_up.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            Toast.makeText(sign_up.this, passwordString, Toast.LENGTH_SHORT).show();
            Toast.makeText(sign_up.this, confirmPasswordString, Toast.LENGTH_SHORT).show();
        }
        else{
            ProgressButton progressButton = new ProgressButton(sign_up.this,view);
            progressButton.onButtonClick(true,"Please Wait...");

            firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String displayNameString = displayTIL.getEditText().getText().toString();
                        if(displayNameString.isEmpty())
                            displayNameString = generateDisplayName();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayNameString)
                                .build();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateProfile(profileUpdates);

                        Toast.makeText(sign_up.this, "User Created! Welcome!", Toast.LENGTH_SHORT).show();

                        SharedPreferences preferences = getSharedPreferences("FIRST_AUTHENTICATION", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("FIRST_TIME_AUTH",true);

                        generateDisplayName();

                        startActivity(new Intent(getApplicationContext(), createCustomProfile.class));
                        finish();

                    } else {
                        Toast.makeText(sign_up.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        showCustomFailedDialog();
                    }
                }
            });
        }
    }

    private String generateDisplayName()
    {
        String randDisplayName = "BeepBoop_" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        return randDisplayName;
    }
    private void showCustomFailedDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_signup_failed_dialog);

        TextView cancelButton = dialog.findViewById(R.id.cancelButton);
        Button reportBugButton = dialog.findViewById(R.id.reportBugButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        reportBugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send to report bugs activity **or send to MainActivity and open report bugs fragment** **or open custom dialog or some crap idk**
            }
        });


        dialog.show();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,LoginPage.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        super.onBackPressed();
        finish();
    }


}