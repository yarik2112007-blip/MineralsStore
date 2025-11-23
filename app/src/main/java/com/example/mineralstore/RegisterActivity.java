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
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etLogin, etEmail, etPassword, etConfirmPassword;
    private TextInputLayout tilLogin, tilEmail, tilPassword, tilConfirmPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilLogin = findViewById(R.id.tilLogin);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etLogin = findViewById(R.id.etLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        findViewById(R.id.tvLogin).setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            // ... валидация ...

            Map<String, Object> body = Map.of(
                    "login", login,
                    "email", email,
                    "password", password,
                    "created_at", (int) (System.currentTimeMillis() / 1000)
            );

            // ВОТ ПРАВИЛЬНЫЙ ПОРЯДОК ПАРАМЕТРОВ:
            SupabaseClient.getApi().register(SupabaseClient.getAnonKey(), "return=representation", body)
                    .enqueue(new Callback<List<User>>() {  // ← List<User> вместо User
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                User user = response.body().get(0);  // Берём первый (и единственный) элемент

                                getSharedPreferences("app", MODE_PRIVATE)
                                        .edit()
                                        .putInt("user_id", (int) user.id)
                                        .putString("user_login", user.login)
                                        .putString("user_email", user.email)
                                        .apply();

                                Toast.makeText(RegisterActivity.this, "Добро пожаловать, " + user.login + "!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Нет интернета: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}