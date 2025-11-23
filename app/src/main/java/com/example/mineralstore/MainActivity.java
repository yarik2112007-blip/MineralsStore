package com.example.mineralstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mineralstore.SupabaseClient;
import com.example.mineralstore.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextInputEditText etLogin = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);

        etLogin.setHint("Логин");

        findViewById(R.id.tvRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            SupabaseClient.getApi().loginByLogin(
                    SupabaseClient.getAnonKey(),
                    "eq." + login,
                    "eq." + password,
                    "*"
            ).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        User user = response.body().get(0);

                        getSharedPreferences("app", MODE_PRIVATE)
                                .edit()
                                .putInt("user_id", (int) user.id)
                                .putString("user_login", user.login)
                                .putString("user_email", user.email != null ? user.email : "")
                                .apply();

                        Toast.makeText(MainActivity.this, "Привет, " + user.login + "!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Нет интернета: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}