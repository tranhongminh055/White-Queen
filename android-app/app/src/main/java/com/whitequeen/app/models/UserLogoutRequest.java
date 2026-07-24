package com.whitequeen.app.models;

public class UserLogoutRequest {
    private String username;

    public UserLogoutRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
