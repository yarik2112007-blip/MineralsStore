package com.example.mineralstore.model;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    public long id;
    public int user_id;
    public int mineral_id;
    public int quantity;

    @SerializedName("mineral")
    public Mineral mineral;
}