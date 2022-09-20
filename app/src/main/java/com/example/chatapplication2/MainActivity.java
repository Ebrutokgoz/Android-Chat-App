package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Toolbar actionBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccessAdapter tabAccessAdapter;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = (Toolbar) findViewById(R.id.actionBarMain);
        setSupportActionBar(actionBar);
        getSupportActionBar().setTitle(R.string.app_name);

        viewPager = (ViewPager) findViewById(R.id.viewPagerMain);
        tabAccessAdapter = new TabAccessAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAccessAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutMain);
        tabLayout.setupWithViewPager(viewPager);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    protected void onStart() {
        if (currentUser == null){
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish(); //MainActivity'i sonlandırır. geri butonuna basılınca MainActivity'e dönülmez
        }

        super.onStart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.searchFriendMenuItem){
            Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.settingsMenuItem){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.exitMenuItem){
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }
}