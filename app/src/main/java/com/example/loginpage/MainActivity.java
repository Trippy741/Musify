package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);
        bottomNavMenu.setOnNavigationItemSelectedListener(navListener);

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                //new CurrentSongPlayingFragment()).commit();
    }
    private void ClickListeners()
    {

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("Nav","Clicked");

        switch (item.getItemId()) {
            case R.id.nav_profile:
                Log.d("Nav","Clicked 1");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PlaylistsFragment()).commit();
                break;
            case R.id.nav_likedSongs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LikedSongsFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.nav_reportBugs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BugReportFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AboutFragment()).commit();
                break;

            case R.id.nav_signOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginPage.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onStart()
    {
        super.onStart();



        FirebaseAuth.getInstance().signOut();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d("banan", user.getEmail());

        if(user != null)
        {
            //updateProfileUI(user);
        }
        else
        {
            startActivity(new Intent(this,LoginPage.class));
            finish();
        }

        Dialog d = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        d.setContentView(R.layout.custom_signup_completed_dialog);

        d.getWindow().setWindowAnimations(R.style.CustomDialogAnims);

        d.getWindow().findViewById(R.id.signup_completion_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();
    }
    public void updateProfileUI(FirebaseUser user)
    {
        TextView profileName = findViewById(R.id.profileName);
        TextView profileEmail = findViewById(R.id.emailName);
        ImageView profilePic = findViewById(R.id.profilePic);

        String displayName = user.getDisplayName().toString();
        String email = user.getDisplayName().toString();
        String profilePicUri = String.valueOf(user.getPhotoUrl());



        if(displayName.toString() != "")
        {
            Log.d("FIREBASE",displayName.toString());
            profileName.setText(displayName);
        }
        if(email.toString() != "")
        {
            profileEmail.setText(user.getEmail().toString());
        }
        if(profilePicUri != "")
        {
            Picasso.with(this).load(Uri.parse(profilePicUri));
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public void showLoginPage() {
        Intent loginIntent = new Intent(MainActivity.this, LoginPage.class);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    public void showSignupPage() {
        Intent signupIntent = new Intent(MainActivity.this, sign_up.class);
        startActivity(signupIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
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
}

