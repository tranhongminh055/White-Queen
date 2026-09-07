package com.whitequeen.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("total")
    private int total;

    @SerializedName("data")
    private List<Product> data;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public List<Product> getData() { return data; }
    public void setData(List<Product> data) { this.data = data; }
}
