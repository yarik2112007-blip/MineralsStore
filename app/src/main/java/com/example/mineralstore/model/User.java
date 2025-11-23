package com.example.mineralstore.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id") public long id;
    @SerializedName("email") public String email;
    @SerializedName("password") public String password;
    @SerializedName("login") public String login;
    @SerializedName("created_at") public long createdAt;

    public User() {}
}