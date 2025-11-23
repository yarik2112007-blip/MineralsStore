package com.example.mineralstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private CartAdapter cartAdapter;
    private TextView tvTotalPrice;
    private Button btnClearCart, btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView rvCart = findViewById(R.id.rvCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateTotal);
        rvCart.setAdapter(cartAdapter);

        updateTotal();

        btnClearCart.setOnClickListener(v -> {
            CartManager.getInstance().clear();
            cartAdapter.updateList(new ArrayList<>());
            updateTotal();
            Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show();
        });

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getItemCount() == 0) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Заказ оформлен на " + CartManager.getInstance().getTotalPrice() + " ₽!", Toast.LENGTH_LONG).show();
                CartManager.getInstance().clear();
                cartAdapter.updateList(new ArrayList<>());
                updateTotal();
            }
        });
        CartManager.getInstance().init(this);
    }

    private void updateTotal() {
        int total = CartManager.getInstance().getTotalPrice();
        int count = CartManager.getInstance().getItemCount();
        tvTotalPrice.setText(String.format("%,d ₽", total));
        setTitle("Корзина (" + count + ")");
    }



    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.notifyDataSetChanged();
        updateTotal();
        // Обновляем счётчик в HomeActivity
        if (HomeActivity.instance != null) {
            HomeActivity.instance.updateCartCounter();
        }
    }
}