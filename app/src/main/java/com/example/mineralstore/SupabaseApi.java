package com.example.mineralstore;

import com.example.mineralstore.model.Mineral;
import com.example.mineralstore.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.List;
import java.util.Map;

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
}