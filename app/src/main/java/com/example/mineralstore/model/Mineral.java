package com.example.mineralstore.model;

import com.google.gson.annotations.SerializedName;

public class Mineral {
    @SerializedName("id")
    public long id;
    @SerializedName("name")
    public String name;
    @SerializedName("price")
    public int price;
    @SerializedName("image_res")
    public String imageRes;

    public Mineral() {}

    public Mineral(String name, int price, String imageRes) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
    }

    public Mineral(long id, String name, int price, String imageRes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getImageRes() { return imageRes; }
    public void setImageRes(String imageRes) { this.imageRes = imageRes; }

    @Override
    public String toString() {
        return "Mineral{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageRes='" + imageRes + '\'' +
                '}';
    }
}