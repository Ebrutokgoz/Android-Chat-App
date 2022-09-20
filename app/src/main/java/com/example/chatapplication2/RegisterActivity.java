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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar actionBar;
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference rootReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();

        actionBar = (Toolbar) findViewById(R.id.actionBarRegister);
        setSupportActionBar(actionBar);
        getSupportActionBar().setTitle("Hesap Oluştur");

        etUsername = (EditText) findViewById(R.id.editTextUsernameRegister);
        etEmail = (EditText) findViewById(R.id.editTextEmailRegister);
        etPassword = (EditText) findViewById(R.id.editTextPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        rootReference = FirebaseDatabase.getInstance().getReference();



        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }
    private void createNewAccount() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        //Bu alanlar boş mu değil mi kontrolü için
        if(TextUtils.isEmpty(username)) {
            Toast.makeText(RegisterActivity.this, "Kullanıcı adınızı giriniz", Toast.LENGTH_SHORT).show();
            etUsername.setBackgroundColor(Color.RED);
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"E-postanızı giriniz", Toast.LENGTH_SHORT).show();
            etEmail.setBackgroundColor(Color.RED);
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Şifrenizi giriniz", Toast.LENGTH_SHORT).show();
            etPassword.setBackgroundColor(Color.RED);
        }
        else{

            btnRegister.setEnabled(false);

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Hesap Oluşturuldu", Toast.LENGTH_LONG).show();


                        String currentUserId = auth.getCurrentUser().getUid();
                        rootReference.child("Users").child(currentUserId).setValue("");

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        String mesaj = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Hesap Oluşturulamadı!\n"+mesaj, Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    }
                }
            });
        }
    }

    public void goLogin(View v){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}