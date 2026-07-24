package com.whitequeen.app.models;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("_id")
    private String id;
    private String username;
    private String email;
    private String created_at;

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getCreatedAt() { return created_at; }
}
