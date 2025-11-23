package com.example.mineralstore;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://rpotspdnfepsgiwrcllq.supabase.co/";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJwb3RzcGRuZmVwc2dpd3JjbGxxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM1Njc1NDgsImV4cCI6MjA3OTE0MzU0OH0.ZqYMAVfWBflA0y0_nrUSAjCcg5atMOLvQvw15WWDhd8";

    private static Retrofit retrofit;
    private static SupabaseApi api;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SupabaseApi getApi() {
        if (api == null) {
            api = getRetrofit().create(SupabaseApi.class);
        }
        return api;
    }

    public static String getAnonKey() {
        return SUPABASE_ANON_KEY;
    }
}