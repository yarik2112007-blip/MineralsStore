package com.example.mineralstore;

import android.content.Context;
import android.util.Log;

import com.example.mineralstore.model.CartItem;
import com.example.mineralstore.model.Mineral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartManager {
    private static final String TAG = "CartManager";
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();
    private Context context;

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        loadCartFromSupabase();
    }

    public void addToCart(Mineral mineral) {
        CartItem existing = findItem((int) mineral.getId());
        if (existing != null) {
            existing.quantity++;
            updateCartItem(existing);
        } else {
            CartItem newItem = new CartItem();
            newItem.mineral = mineral;
            newItem.quantity = 1;
            newItem.user_id = getUserId();
            newItem.mineral_id = (int) mineral.getId();
            cartItems.add(newItem);
            insertCartItem(newItem);
        }
        notifyHomeActivity();
    }

    public void removeFromCart(CartItem item) {
        cartItems.remove(item);
        deleteCartItem(item);
        notifyHomeActivity();
    }

    public void updateQuantity(CartItem item, int quantity) {
        if (quantity <= 0) {
            removeFromCart(item);
        } else {
            item.quantity = quantity;
            updateCartItem(item);
            notifyHomeActivity();
        }
    }

    private void loadCartFromSupabase() {
        int userId = getUserId();
        if (userId == 0) return;

        SupabaseClient.getApi().getCartItemsForUser(
                SupabaseClient.getAnonKey(),
                "eq." + userId
        ).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body());
                    Log.d(TAG, "Корзина загружена: " + cartItems.size() + " товаров");
                    for (CartItem item : cartItems) {
                        Log.d(TAG, "Товар: " + item.mineral.getName() + " × " + item.quantity);
                    }
                    notifyHomeActivity();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.e(TAG, "Не удалось загрузить корзину", t);
            }
        });
    }

    private void insertCartItem(CartItem item) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", item.user_id);
        body.put("mineral_id", item.mineral_id);
        body.put("quantity", item.quantity);

        Log.d(TAG, "Отправляем в Supabase: " + body);

        SupabaseClient.getApi().addToCartClean(SupabaseClient.getAnonKey(), body)
                .enqueue(new Callback<CartItem>() {
                    @Override
                    public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            item.id = response.body().id;
                            Log.d(TAG, "Успешно добавлено! Новый id = " + item.id);
                        } else {
                            Log.e(TAG, "Ошибка добавления: " + response.code() + " " + response.message());
                            if (response.errorBody() != null) {
                                try {
                                    Log.e(TAG, "Ошибка: " + response.errorBody().string());
                                } catch (Exception e) { e.printStackTrace(); }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CartItem> call, Throwable t) {
                        Log.e(TAG, "Не удалось добавить в корзину", t);
                    }
                });
    }

    private void updateCartItem(CartItem item) {
        if (item.id <= 0) return;
        Map<String, Object> body = new HashMap<>();
        body.put("quantity", item.quantity);

        SupabaseClient.getApi().updateCartItem(
                SupabaseClient.getAnonKey(),
                "eq." + item.id,
                body
        ).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void deleteCartItem(CartItem item) {
        if (item.id <= 0) {
            cartItems.remove(item);
            return;
        }
        SupabaseClient.getApi().deleteCartItem(
                SupabaseClient.getAnonKey(),
                "eq." + item.id
        ).enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "Элемент удалён из корзины (id=" + item.id + ")");
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private CartItem findItem(int mineralId) {
        for (CartItem item : cartItems) {
            if (item.mineral_id == mineralId) return item;
        }
        return null;
    }

    private int getUserId() {
        return context.getSharedPreferences("app", Context.MODE_PRIVATE)
                .getInt("user_id", 0);
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getTotalPrice() {
        int total = 0;
        for (CartItem item : cartItems) {
            if (item.mineral != null) {
                total += item.mineral.getPrice() * item.quantity;
            }
        }
        return total;
    }

    public int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.quantity;
        }
        return count;
    }

    public void clear() {
        for (CartItem item : cartItems) {
            deleteCartItem(item);
        }
        cartItems.clear();
        notifyHomeActivity();
    }

    private void notifyHomeActivity() {
        if (HomeActivity.instance != null) {
            HomeActivity.instance.runOnUiThread(() -> {
                HomeActivity.instance.updateCartCounter();
                if (CartActivity.class.getSimpleName().equals(
                        HomeActivity.instance.getLocalClassName())) {
                }
            });
        }
    }
}