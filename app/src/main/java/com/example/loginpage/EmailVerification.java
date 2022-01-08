package com.example.loginpage;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        sendMail(); //Send the mail (first time)

        TextView userMail = findViewById(R.id.verfMail_userMail_textView);
        Button resendMailButton = findViewById(R.id.verfMail_resend_button);
        Button continueButton = findViewById(R.id.verfMail_continue_button);

        resendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
                //TODO: And then start a timer so that the user cant just spam himself
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void sendMail()
    {
        try
        {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
        }
        catch (Exception e)
        {
            Toast.makeText(EmailVerification.this, "Error sending verification mail: " + e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(EmailVerification.this,LoginPage.class));
        }
    }
}