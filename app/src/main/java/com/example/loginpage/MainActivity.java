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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        BottomNavigationView bottomNavMenu = findViewById(R.id.bottom_navigation);
        bottomNavMenu.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new CurrentSongPlayingFragment()).commit();
    }
    private void ClickListeners()
    {

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Toast.makeText(MainActivity.this, "clicked!", Toast.LENGTH_SHORT).show();

        switch (item.getItemId()) {
            case R.id.nav_profile:
                Toast.makeText(MainActivity.this, "clicked Favorites!", Toast.LENGTH_SHORT).show();
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



        //FirebaseAuth.getInstance().signOut();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d("banan", user.getEmail());

        if(user != null)
        {
            updateProfileUI();
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
    public void updateProfileUI()
    {
       TextView profileName = findViewById(R.id.drawerMenu_displayName);
        TextView profileEmail = findViewById(R.id.drawerMenu_emailAddress);
        ImageView profilePic = findViewById(R.id.drawerMenu_profilePic);

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String displayName = user.getDisplayName();
        String email = user.getEmail();
        String profilePicUri = String.valueOf(user.getPhotoUrl());*/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String displayName = "";
        String email = "";
        Uri profilePicUri = Uri.EMPTY;

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                displayName = profile.getDisplayName();
                email = profile.getEmail();
                profilePicUri = profile.getPhotoUrl();
                Toast.makeText(MainActivity.this, email, Toast.LENGTH_SHORT).show();
            }
        }

        View headerContainer = navigationView.getHeaderView(0); // This returns the container layout from your navigation drawer header layout file (e.g., the parent RelativeLayout/LinearLayout in your my_nav_drawer_header.xml file)


        if(displayName != "")
        {
            Log.d("FIREBASE",displayName);
            profileName.setText(displayName);
        }
        if(email != "")
        {
            TextView emailText = headerContainer.findViewById(R.id.drawerMenu_emailAddress);
            emailText.setText(email);
        }
        if(profilePicUri != Uri.EMPTY)
        {
            ImageView profileImage = headerContainer.findViewById(R.id.drawerMenu_profilePic);
            profileImage.setImageURI(profilePicUri);
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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

