package com.example.mineralstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mineralstore.SupabaseClient;
import com.example.mineralstore.model.Mineral;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private MineralAdapter adapter;
    private TextView tvWelcome;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getSharedPreferences("app", MODE_PRIVATE);

        // Проверка авторизации
        int userId = prefs.getInt("user_id", 0);
        if (userId == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Приветствие
        tvWelcome = findViewById(R.id.tvWelcome);
        String login = prefs.getString("user_login", "Пользователь");
        tvWelcome.setText("Привет, " + login + "!");

        // Кнопка выхода
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply(); // Удаляем все данные
            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // Настройка RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        adapter = new MineralAdapter();
        recyclerView.setAdapter(adapter);

        loadMinerals();
    }

    private void loadMinerals() {
        SupabaseClient.getApi().getMinerals(SupabaseClient.getAnonKey())
                .enqueue(new Callback<List<Mineral>>() {
                    @Override
                    public void onResponse(Call<List<Mineral>> call, Response<List<Mineral>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.submitList(response.body());
                        } else {
                            Toast.makeText(HomeActivity.this, "Не удалось загрузить минералы", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Mineral>> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Нет интернета", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}