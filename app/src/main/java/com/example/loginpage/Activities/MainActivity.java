package com.example.loginpage.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.loginpage.CustomDataTypes.Album;
import com.example.loginpage.Fragments.CustomAlbumsFragment;
import com.example.loginpage.Fragments.HomeFragment;
import com.example.loginpage.CustomServices.MusicService;
import com.example.loginpage.GestureControl.OnSwipeTouchListener;
import com.example.loginpage.Fragments.ProfileFragment;
import com.example.loginpage.R;
import com.example.loginpage.Fragments.SearchFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String profilePicIndex = "-1";
    private String displayName = "";
    private String email = "";

    private TextView usernameText;

    private final ArrayList<Album> likedAlbums = new ArrayList<Album>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //private ArrayList<RecyclerView> recyclers = new ArrayList<RecyclerView>();

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(MusicService.isPlaying)
            MusicService.Destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        super.onCreate(savedInstanceState);

        //FirebaseAuth.getInstance().signOut();
        //GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();

        contextOfApplication = getApplicationContext();

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameText = findViewById(R.id.home_usernameTextView);

        /*String customTextID = "username_greeting_"+ new Random().nextInt(2);
        int customTextRES_ID = getResources().getIdentifier(customTextID,"id",getPackageName());

        usernameText.setText(getString(customTextRES_ID) + " " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());*/

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        drawer.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeRight() {

                if(!drawer.isDrawerVisible(GravityCompat.START))
                    drawer.openDrawer(GravityCompat.START);
            }

            @Override
            public void onSwipeLeft() {
                if(drawer.isDrawerVisible(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onSwipeTop() {

            }

            @Override
            public void onSwipeBottom() {

            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recyclers.add(findViewById(R.id.mainLikedAlbumsRecyclerView));


        /*BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);
        bottomNavMenu.setOnNavigationItemSelectedListener(navListener);*/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences1 = getSharedPreferences("FIRST_AUTHENTICATION", Context.MODE_PRIVATE);
        boolean isUserFirstTimeAuth = preferences1.getBoolean("FIRST_TIME_AUTH",false);

        SharedPreferences preferences2 = getSharedPreferences("LOGIN_REMEMBER_CHECK", Context.MODE_PRIVATE);
        boolean isUserRemembered = preferences2.getBoolean("REMEMBER_ME",false);

        if(user != null) //TODO: ADD  && isUserRemembered == true LATER PLEASE :)
        {
            updateProfileUI();
        }
        else
        {
            startActivity(new Intent(this,LoginPage.class));
            finish();
        }

        if(isUserFirstTimeAuth)
        {
            Dialog d = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            d.setContentView(R.layout.custom_signup_completed_dialog);

            d.getWindow().setWindowAnimations(R.style.CustomDialogAnims_slide);

            d.getWindow().findViewById(R.id.signup_completion_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });

            d.show();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).addToBackStack(null).commit();

    }
    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            showDismissAlertDialog();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
    private void showDismissAlertDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Gone so soon?");
        alert.setMessage("Are you sure you want to quit the app?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.super.onBackPressed();
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

    public void updateProfileUI()
    {
        View headerContainer = navigationView.getHeaderView(0);

        ProgressBar headerProgressBar = headerContainer.findViewById(R.id.drawerMenu_progressBar);
        ImageView prof_pic_view = headerContainer.findViewById(R.id.drawerMenu_profilePic);
        TextView displayText = headerContainer.findViewById(R.id.drawerMenu_displayName);
        TextView emailText = headerContainer.findViewById(R.id.drawerMenu_emailAddress);

        headerProgressBar.setVisibility(View.VISIBLE);
        prof_pic_view.setVisibility(View.INVISIBLE);
        displayText.setVisibility(View.INVISIBLE);
        emailText.setVisibility(View.INVISIBLE);

        DocumentReference docRef = db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        String u = "user_"+FirebaseAuth.getInstance().getCurrentUser().getUid();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String profile_index = "-1";
                    DocumentSnapshot document = task.getResult();
                    if(document.get("profile_pic_index") != null)
                    {
                        profile_index = document.get("profile_pic_index").toString();
                    }
                    if (document.exists() && !profile_index.isEmpty()) {
                        profilePicIndex = profile_index;
                    }

                    //Toast.makeText(MainActivity.this, "Profile Index: " + profilePicIndex, Toast.LENGTH_SHORT).show();
                    String DN = "";
                    if(document.get("displayName") != null)
                    {
                        DN = document.get("displayName").toString();
                    }

                    if (document.exists() && !DN.isEmpty()) {
                        displayName = DN;
                    }

                    //Toast.makeText(MainActivity.this, "Display Name: " + displayName, Toast.LENGTH_SHORT).show();


                    if (document.exists()) {
                        String mail = document.get("email").toString();
                        if(!mail.isEmpty())
                            email = mail;
                        else
                            email = "Email Not Set :(";
                    }
                    //Toast.makeText(MainActivity.this, "Email Address: " + email, Toast.LENGTH_SHORT).show();

                    String profileID = "ic_profile_def";

                    if(profilePicIndex != "-1")
                    {
                        profileID = "ic_profile_"+profilePicIndex;
                    }

                    int resID = getResources().getIdentifier(profileID,"drawable",getPackageName());
                    prof_pic_view.setImageResource(resID);


                    displayText.setText(displayName);


                    emailText.setText(email);

                    headerProgressBar.setVisibility(View.INVISIBLE);
                    prof_pic_view.setVisibility(View.VISIBLE);
                    displayText.setVisibility(View.VISIBLE);
                    emailText.setVisibility(View.VISIBLE);
                }else
                    Toast.makeText(MainActivity.this, "Failed to update user UI elements: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).addToBackStack(null).commit();
                break;
            case R.id.nav_signOut:
                Dialog d = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                d.setContentView(R.layout.custom_signout_dialog);

                d.getWindow().setWindowAnimations(R.style.CustomDialogAnims_fade);

                d.getWindow().findViewById(R.id.signout_no_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

                d.getWindow().findViewById(R.id.signout_yes_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignIn.getClient(MainActivity.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
                        SharedPreferences preferences = getSharedPreferences("FIRST_AUTHENTICATION", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("FIRST_TIME_AUTH",false);

                        startActivity(new Intent(getApplicationContext(),LoginPage.class));
                        d.dismiss();
                    }
                });

                d.show();
                break;
            case R.id.navmenu_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment(), null).addToBackStack(null).commit();
                break;

            case R.id.nav_mySongs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CustomAlbumsFragment(), null).addToBackStack(null).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

