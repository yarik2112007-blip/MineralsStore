package com.example.mineralstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView tvLogin, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = getSharedPreferences("app", MODE_PRIVATE);

        tvLogin = findViewById(R.id.tvLogin);
        tvEmail = findViewById(R.id.tvEmail);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        String login = prefs.getString("user_login", "Неизвестно");
        String email = prefs.getString("user_email", "email не указан");

        tvLogin.setText("Логин: " + login);
        tvEmail.setText("Email: " + email);

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        });
    }
}