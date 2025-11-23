package com.example.mineralstore;

import com.example.mineralstore.model.CartItem;
import com.example.mineralstore.model.Mineral;
import com.example.mineralstore.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {

    @POST("rest/v1/users")
    Call<List<User>> register(
            @Header("apikey") String apiKey,
            @Header("Prefer") String prefer,
            @Body Map<String, Object> body
    );

    @GET("rest/v1/users")
    Call<List<User>> loginByLogin(
            @Header("apikey") String apiKey,
            @Query("login") String login,
            @Query("password") String password,
            @Query("select") String select
    );

    @GET("rest/v1/minerals?select=*")
    Call<List<Mineral>> getMinerals(@Header("apikey") String apiKey);

    @GET("rest/v1/cart?select=*,mineral:mineral_id(*)")
    Call<List<CartItem>> getCartItemsForUser(
            @Header("apikey") String apiKey,
            @Query("user_id") String userIdEq
    );

    @POST("rest/v1/cart")
    Call<CartItem> addToCart(
            @Header("apikey") String apiKey,
            @Body CartItem item
    );

    @PATCH("rest/v1/cart")
    Call<Void> updateCartItem(
            @Header("apikey") String apiKey,
            @Query("id") String idEq,
            @Body Map<String, Object> fields
    );

    @DELETE("rest/v1/cart")
    Call<Void> deleteCartItem(
            @Header("apikey") String apiKey,
            @Query("id") String idEq
    );

    @POST("rest/v1/cart")
    Call<CartItem> addToCartClean(
            @Header("apikey") String apiKey,
            @Body Map<String, Object> body
    );
}