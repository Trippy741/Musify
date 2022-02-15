package com.example.loginpage;

import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    private final ArrayList<Album> likedAlbums = new ArrayList<Album>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //private ArrayList<RecyclerView> recyclers = new ArrayList<RecyclerView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); if (getSupportActionBar() != null){ getSupportActionBar().hide(); }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //updateAlbumScrollview();
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
    /*private void updateAlbumScrollview()
    {
        //LinearLayout albumLayout = findViewById(R.id.horizontal_scroll_view_0);

        db.collection("users").document("user_"+FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("user_liked_albums").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot doc : task.getResult())
                {

                    Album tmpAlbum = new Album("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/user_liked_albums/TOOL/albums/"+doc.getId()
                            ,doc.getId()
                            ,doc.get("album_title").toString()
                            ,"TOOL"
                            ,doc.get("album_img_url").toString()
                            ,doc.get("album_release_date").toString()
                            ,doc.get("album_duration").toString());

                    likedAlbums.add(tmpAlbum);

                    MainActivityAlbums_RecyclerViewAdapter adapter = new MainActivityAlbums_RecyclerViewAdapter(MainActivity.this,getSupportFragmentManager(),likedAlbums);
                    recyclers.get(0).setAdapter(adapter);

                    *//*ImageView iv = new ImageView(getApplicationContext());
                    Picasso.with(MainActivity.this).load(document.get("album_image_url").toString()).into(iv);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(600, 600));
                    albumLayout.addView(iv);
                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            *//**//*bundle.putString("artist_id",document.getId());
                            bundle.putString("album_id",document.get("album_name").toString());*//**//*

                            bundle.putString("artist_id","TOOL");
                            bundle.putString("album_id","lateralus");

                            AlbumView frag = new AlbumView();
                            frag.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                        }
                    });
                    likedAlbums.add(iv);*//*
                }
            }
        });
    }*/

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

                    String mail = document.get("email").toString();
                    if (document.exists() && !mail.isEmpty()) {
                        email = mail;
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

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SearchFragment()).commit();
                break;
            case R.id.nav_currentsongplaying:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CurrentSongPlayingFragment()).commit();
                break;
            case R.id.nav_playlists:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AlbumView()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ArtistView()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

