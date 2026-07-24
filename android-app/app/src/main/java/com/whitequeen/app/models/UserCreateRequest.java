package com.whitequeen.app.models;

public class UserCreateRequest {
    private String username;
    private String email;
    private String password;
    private String otp;

    public UserCreateRequest(String username, String email, String password, String otp) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.otp = otp;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
