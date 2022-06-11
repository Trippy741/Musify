package com.example.loginpage.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.animation.PathInterpolatorCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.CustomDataTypes.local_user;
import com.example.loginpage.CustomXML_elements.ProgressButton;
import com.example.loginpage.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity{

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView signupText;

    private ImageButton googleLoginButton;

    private Context context;

    private TextView introText1;
    private TextView introText2;
    private TextView introText3;
    private TextView introText4;

    private CheckBox rememberMe;

    Button animButton;

    private GoogleSignInClient googleClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        context = this;

        googleLoginButton = findViewById(R.id.loginGoogleButton);

        mAuth = FirebaseAuth.getInstance();

        signupText = findViewById(R.id.signUpText);

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,sign_up.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_bottom_to_center, R.anim.slide_from_center_to_top);
                finish();
            }
        });

        View loginButtonView = findViewById(R.id.login_button_View);

        ProgressButton progressButton = new ProgressButton(LoginPage.this,loginButtonView);
        progressButton.changeButtonText("Login");

        loginButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginClick(view);
            }
        });

        introTextAnimation();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleClient = GoogleSignIn.getClient(this,googleSignInOptions);

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);  UPDATE THE UI OF THE USER
        animateGradient();

    }

    private void animateGradient()
    {
        LinearLayout loginPageLayout = findViewById(R.id.loginPageLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) loginPageLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
    private void resetButton(View view)
    {
        ProgressButton progressButton = new ProgressButton(LoginPage.this,view);
        progressButton.onButtonClick(false,"Login");
    }

    public void backToMain()
    {
        Intent mainIntent = new Intent(this,MainActivity.class);
        startActivity(mainIntent);
    }
    private void LoginClick(View view)
    {
        ProgressButton progressButton = new ProgressButton(LoginPage.this,view);
        progressButton.onButtonClick(true,"Please Wait...");

        TextInputLayout emailTIL = findViewById(R.id.emailEditText);
        TextInputLayout passwordTIL = findViewById(R.id.passwordEditText);

        String email_string = emailTIL.getEditText().getEditableText().toString();
        String password_string = passwordTIL.getEditText().getEditableText().toString();

        email_string = email_string.replaceAll("\\s", "");
        password_string = password_string.replaceAll("\\s", "");

        Toast.makeText(context, email_string, Toast.LENGTH_SHORT).show();

        Boolean isCorrectlyFormatted = android.util.Patterns.EMAIL_ADDRESS.matcher(email_string).matches();

        if(email_string == "")
        {
            emailTIL.setError("*This is a required field!");
            resetButton(view);
        }
        if(password_string == "")
        {
            emailTIL.setError("*This is a required field!");
            resetButton(view);
        }
        if(isCorrectlyFormatted == false)
        {
            emailTIL.setError("Email address incorrectly formatted!");
            resetButton(view);
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email_string, password_string)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FIREBASE", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.isEmailVerified())
                                {
                                    startActivity(new Intent(LoginPage.this,MainActivity.class));

                                    rememberMe = findViewById(R.id.login_rememberMe_checkbox);

                                    SharedPreferences preferences = getSharedPreferences("LOGIN_REMEMBER_CHECK", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("REMEMBER_ME",rememberMe.isChecked());

                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(LoginPage.this, "User email is not verified!!!", Toast.LENGTH_SHORT).show();
                                    resetButton(view);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FIREBASE", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginPage.this, "Authentication failed: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                resetButton(view);
                            }
                        }
                    });
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(context, "Successfully Signed-In With Google!", Toast.LENGTH_SHORT).show();
                Log.d("FIREBASE", "firebaseAuthWithGoogle:" + account.getId());
                FirebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(context, "Google Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.w("FIREBASE", "Google sign in failed", e);
            }
        }
    }

    private void introTextAnimation()
    {
        introText1 = findViewById(R.id.introText1);
        Animation fadeInAnimIntroText1 = AnimationUtils.loadAnimation(context,R.anim.intro_text_anim);
        fadeInAnimIntroText1.setInterpolator(PathInterpolatorCompat.create(0.400f, 0.005f, 0.365f, 1.325f));
        fadeInAnimIntroText1.setStartOffset(1050);
        introText1.startAnimation(fadeInAnimIntroText1);

        introText1.setTextColor(getResources().getColor(R.color.darkest_grey));
        introText1.setShadowLayer(3f,1.5f,1.5f,getResources().getColor(R.color.purple));

        introText2 = findViewById(R.id.introText2);
        Animation fadeInAnimIntroText2 = AnimationUtils.loadAnimation(context,R.anim.intro_text_anim);
        fadeInAnimIntroText2.setInterpolator(PathInterpolatorCompat.create(0.400f, 0.005f, 0.365f, 1.325f));
        fadeInAnimIntroText2.setStartOffset(1100);
        introText2.startAnimation(fadeInAnimIntroText2);

        introText2.setTextColor(getResources().getColor(R.color.darkest_grey));
        introText2.setShadowLayer(3f,1.5f,1.5f,getResources().getColor(R.color.purple));

        introText4 = findViewById(R.id.introText4);
        Animation fadeInAnimIntroText4 = AnimationUtils.loadAnimation(context,R.anim.intro_text_anim);
        fadeInAnimIntroText4.setInterpolator(PathInterpolatorCompat.create(0.400f, 0.005f, 0.365f, 1.325f));
        fadeInAnimIntroText4.setStartOffset(1150);
        introText4.startAnimation(fadeInAnimIntroText4);

        introText4.setTextColor(getResources().getColor(R.color.darkest_grey));
        introText4.setShadowLayer(3f,1.5f,1.5f,getResources().getColor(R.color.purple));

        introText3 = findViewById(R.id.introText);
        Animation fadeInAnimIntroText3 = AnimationUtils.loadAnimation(context,R.anim.intro_text_anim);
        fadeInAnimIntroText3.setInterpolator(PathInterpolatorCompat.create(0.400f, 0.005f, 0.365f, 1.325f));
        fadeInAnimIntroText3.setStartOffset(1000);
        introText3.startAnimation(fadeInAnimIntroText3);
    }
    private void FirebaseAuthWithGoogle(GoogleSignInAccount Account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(Account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(context, "Log in Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginPage.this,MainActivity.class);

                //Create firebase document for this user
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser createdUser = FirebaseAuth.getInstance().getCurrentUser();

                String user_id = "user_"+createdUser.getUid();

                local_user user = new local_user(user_id,createdUser.getDisplayName(),createdUser.getEmail(),"00");
                user.isUsingGoogle = true;
                //firebase store
                db.collection("users").document(user_id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(LoginPage.this, "User created & stored successfully", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Failed to store user: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
                //Start the MainActivity
                startActivity(intent);
            }
        });
    }
}
