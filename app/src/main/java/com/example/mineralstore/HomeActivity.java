package com.example.mineralstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mineralstore.model.Mineral;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static HomeActivity instance; // ← Для CartManager

    private MineralAdapter adapter;
    private TextView tvWelcome;
    private SharedPreferences prefs;
    private BottomNavigationView bottomNav;
    private MenuItem cartMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        instance = this;
        CartManager.getInstance().init(this);

        prefs = getSharedPreferences("app", MODE_PRIVATE);

        if (prefs.getInt("user_id", 0) == 0) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Привет, " + prefs.getString("user_login", "Пользователь") + "!");

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Toast.makeText(this, "Вы вышли", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MineralAdapter();
        recyclerView.setAdapter(adapter);
        loadMinerals();

        bottomNav = findViewById(R.id.bottom_navigation);
        cartMenuItem = bottomNav.getMenu().findItem(R.id.nav_cart);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            }
            if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        updateCartCounter();
    }

    private void loadMinerals() {
        SupabaseClient.getApi().getMinerals(SupabaseClient.getAnonKey())
                .enqueue(new Callback<List<Mineral>>() {
                    @Override
                    public void onResponse(Call<List<Mineral>> call, Response<List<Mineral>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.submitList(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Mineral>> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Нет интернета", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateCartCounter() {
        int count = CartManager.getInstance().getItemCount();
        if (count == 0) {
            cartMenuItem.setTitle("Корзина");
        } else {
            cartMenuItem.setTitle("Корзина (" + count + ")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCounter();
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    @Override
    protected void onDestroy() {
        if (instance == this) instance = null;
        super.onDestroy();
    }
}