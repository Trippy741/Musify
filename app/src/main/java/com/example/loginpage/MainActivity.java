package com.example.loginpage;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private String profilePicIndex = "-1";
    private String displayName = "";
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /*BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);
        bottomNavMenu.setOnNavigationItemSelectedListener(navListener);*/
    }
    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences1 = getSharedPreferences("FIRST_AUTHENTICATION", Context.MODE_PRIVATE);
        boolean isUserFirstTimeAuth = preferences1.getBoolean("FIRST_TIME_AUTH",false);

        SharedPreferences preferences2 = getSharedPreferences("LOGIN_REMEMBER_CHECK", Context.MODE_PRIVATE);
        boolean isUserRemembered = preferences2.getBoolean("REMEMBER_ME",false);

        if(user != null) //TODO: ADD  && isUserRemembered == true LATER PLEASE :)
        {
            //updateProfileUI();
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

        updateAlbumScrollview();
    }
    private void updateAlbumScrollview()
    {
        LinearLayout albumLayout = findViewById(R.id.horizontal_scroll_view_0);

        for(int i = 0; i < 7; i++)
        {
            ImageView iv = new ImageView(getApplicationContext());

            // Set an image for ImageView
            iv.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round));

            // Add layout parameters to ImageView
            iv.setLayoutParams(new LinearLayout.LayoutParams(600, 600));

            // Finally, add the ImageView to layout
            albumLayout.addView(iv);
        }
    }
    public void updateProfileUI()
    {
        View headerContainer = navigationView.getHeaderView(0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                String profile_index = document.get("profile_pic_index").toString();
                if (document.exists() && !profile_index.isEmpty()) {
                    profilePicIndex = profile_index;
                } else {
                    profilePicIndex = "-1";
                }

                Toast.makeText(MainActivity.this, "Profile Index: " + profilePicIndex, Toast.LENGTH_SHORT).show();

                String DN = document.get("displayName").toString();
                if (document.exists() && !DN.isEmpty()) {
                    displayName = DN;
                }

                Toast.makeText(MainActivity.this, "Display Name: " + displayName, Toast.LENGTH_SHORT).show();

                String mail = document.get("email").toString();
                if (document.exists() && !mail.isEmpty()) {
                    email = mail;
                }
                Toast.makeText(MainActivity.this, "Email Address: " + email, Toast.LENGTH_SHORT).show();
            }
        });

        ImageView prof_pic_view = headerContainer.findViewById(R.id.drawerMenu_profilePic);

        String profileID = "ic_profile_"+profilePicIndex;
        int resID = getResources().getIdentifier(profileID,"drawable",getPackageName());
        prof_pic_view.setImageResource(resID);

        TextView displayText = headerContainer.findViewById(R.id.drawerMenu_displayName);
        displayText.setText(displayName);

        TextView emailText = headerContainer.findViewById(R.id.drawerMenu_emailAddress);
        emailText.setText(email);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId())
            {
                case R.id.nav_favorites:
                    selectedFragment = new LikedSongsFragment();
                    break;
                case R.id.nav_search:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
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

                        SharedPreferences preferences = getSharedPreferences("FIRST_AUTHENTICATION", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("FIRST_TIME_AUTH",false);

                        startActivity(new Intent(getApplicationContext(),LoginPage.class));
                        d.dismiss();
                    }
                });

                d.show();
                break;
            case R.id.nav_player:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new deviceMusicPlayerFragment()).commit();
                break;
            case R.id.nav_currentsongplaying:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CurrentSongPlayingFragment()).commit();
                break;
            case R.id.nav_playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AlbumView()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

