package com.example.chatapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Toolbar actionBar;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth auth;
    private DatabaseReference userPath;
    //private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar = (Toolbar) findViewById(R.id.actionBarLogin);
        setSupportActionBar(actionBar);
        getSupportActionBar().setTitle("Giriş Yap");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etEmail = (EditText) findViewById(R.id.editTextEmailLogin);
        etPassword = (EditText) findViewById(R.id.editTextPasswordLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();
        userPath = FirebaseDatabase.getInstance().getReference().child("Users");
        //currentUser = auth.getCurrentUser();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });


    }

    private void login() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this,"E-postanızı giriniz", Toast.LENGTH_SHORT).show();
            etEmail.setBackgroundColor(Color.RED);
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Şifrenizi giriniz", Toast.LENGTH_SHORT).show();
            etPassword.setBackgroundColor(Color.RED);
        }
        else{

            btnLogin.setEnabled(false);

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String currentUserId =auth.getCurrentUser().getUid();

                        Toast.makeText(LoginActivity.this,"Giriş Yapılıyor...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        String mesaj = task.getException().toString();
                        Toast.makeText(LoginActivity.this,"Giriş Yapılamadı!\n"+mesaj, Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                    }
                }
            });
        }
    }

    public void goRegister (View v){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}