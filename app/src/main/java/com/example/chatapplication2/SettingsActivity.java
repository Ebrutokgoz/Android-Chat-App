package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateButton;
    private EditText username, about;
    private CircleImageView profilPhoto;

    private FirebaseAuth auth;
    private DatabaseReference dataPath;
    private StorageReference userPhotosPath;
    private StorageTask loadTask;

    private String currentUserId;

    //REsim seçmek için

    private Toolbar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateButton = (Button) findViewById(R.id.updateButton);
        username = (EditText) findViewById(R.id.editTextUsernameSettings);
        about = (EditText) findViewById(R.id.editTextAboutSettings);
        profilPhoto = (CircleImageView) findViewById(R.id.profilPhotoSettings);

        actionBar = (Toolbar) findViewById(R.id.appBarSettings);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ayarlar");


        auth = FirebaseAuth.getInstance();
        dataPath = FirebaseDatabase.getInstance().getReference();
        userPhotosPath = FirebaseStorage.getInstance().getReference().child("Profil Photos");

        currentUserId = auth.getCurrentUser().getUid();

        getUserInformation();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });



    }

    private void getUserInformation() {
        dataPath.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists()) && snapshot.hasChild("Photo") && snapshot.hasChild("About")){
                    String getUsername = snapshot.child("Name").getValue().toString();
                    String getAbout = snapshot.child("About").getValue().toString();
                    String getPhoto = snapshot.child("Photo").getValue().toString();

                    username.setText(getUsername);
                    about.setText(getAbout);
                    //profil fotoğrafını ayarla
                }
                else if((snapshot.exists()) && snapshot.hasChild("Photo")){
                    String getUsername = snapshot.child("Name").getValue().toString();
                    String getPhoto = snapshot.child("Photo").getValue().toString();
                    username.setText(getUsername);
                }
                else {
                    String getUsername = snapshot.child("Name").getValue().toString();
                    username.setText(getUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateSettings() {

        String getUsername = username.getText().toString();
        String getAbout = about.getText().toString();

        if(TextUtils.isEmpty(getUsername)){
            Toast.makeText(SettingsActivity.this, "Kullanıcı adınızı giriniz", Toast.LENGTH_SHORT).show();
            username.setBackgroundColor(Color.RED);
        }
    }
}