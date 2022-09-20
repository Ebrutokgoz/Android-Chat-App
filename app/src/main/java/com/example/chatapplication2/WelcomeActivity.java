package com.example.chatapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        btnLogin = (Button) findViewById(R.id.btnWelcomeLogin);
        btnRegister = (Button) findViewById(R.id.btnWelcomeRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intentLogin);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentReg = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intentReg);
                finish();
            }
        });
    }
}