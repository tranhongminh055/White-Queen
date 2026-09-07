package com.whitequeen.app.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("sold")
    private String sold;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("platform")
    private String platform;

    @SerializedName("discount")
    private String discount;

    @SerializedName("productUrl")
    private String productUrl;

    @SerializedName("category")
    private String category;

    @SerializedName("rating")
    private Float rating;

    // Fallback resource id if URL cannot load or in offline mock mode
    private int imageResId;

    public Product() {
    }

    public Product(String name, String price, String sold, String imageUrl, String platform, String discount) {
        this.name = name;
        this.price = price;
        this.sold = sold;
        this.imageUrl = imageUrl;
        this.platform = platform;
        this.discount = discount;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getSold() { return sold != null ? sold : "Đã bán 0"; }
    public void setSold(String sold) { this.sold = sold; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPlatform() { return platform != null ? platform : "Shopee"; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Float getRating() { return rating != null ? rating : 4.8f; }
    public void setRating(Float rating) { this.rating = rating; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
