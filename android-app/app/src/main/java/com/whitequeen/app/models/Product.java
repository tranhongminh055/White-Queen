package com.whitequeen.app.models;

public class Product {
    private int id;
    private String name;
    private String price;
    private String soldCount;
    private int imageResId;

    public Product(int id, String name, String price, String soldCount, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.soldCount = soldCount;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getSoldCount() { return soldCount; }
    public int getImageResId() { return imageResId; }
}
