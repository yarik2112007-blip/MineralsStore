package com.example.mineralstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartActivity extends AppCompatActivity {

    public static CartActivity instance;

    private CartAdapter cartAdapter;
    private RecyclerView rvCart;
    private TextView tvTotalPrice;
    private Button btnClearCart, btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        instance = this;

        // ← ПРАВИЛЬНОЕ ПРИВЕДЕНИЕ ТИПОВ!
        rvCart = findViewById(R.id.rvCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems(), this::updateTotal);
        rvCart.setAdapter(cartAdapter);

        updateTotal();

        btnClearCart.setOnClickListener(v -> {
            CartManager.getInstance().clear();
            refreshCartList();
            updateTotal();
            Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show();
        });

        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getItemCount() == 0) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Заказ оформлен на " + CartManager.getInstance().getTotalPrice() + " ₽!", Toast.LENGTH_LONG).show();
                CartManager.getInstance().clear();
                refreshCartList();
                updateTotal();
            }
        });

        CartManager.getInstance().init(this);
    }

    public void updateTotal() {
        int total = CartManager.getInstance().getTotalPrice();
        int count = CartManager.getInstance().getItemCount();
        tvTotalPrice.setText(String.format("%,d ₽", total));  // ← Теперь работает!
        setTitle("Корзина (" + count + ")");
    }

    public void refreshCartList() {
        cartAdapter.updateList(CartManager.getInstance().getCartItems());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCartList();
        updateTotal();
        if (HomeActivity.instance != null) {
            HomeActivity.instance.updateCartCounter();
        }
    }

    @Override
    protected void onDestroy() {
        if (instance == this) {
            instance = null;
        }
        super.onDestroy();
    }
}