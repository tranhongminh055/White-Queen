package com.whitequeen.app.api;

import com.whitequeen.app.models.UserCreateRequest;
import com.whitequeen.app.models.UserLoginRequest;
import com.whitequeen.app.models.UserLogoutRequest;
import com.whitequeen.app.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.Map;
import com.whitequeen.app.models.SendOTPRequest;

public interface ApiService {

    @POST("users/")
    Call<UserResponse> registerUser(@Body UserCreateRequest request);

    @POST("users/login")
    Call<UserResponse> loginUser(@Body UserLoginRequest request);
    @POST("users/send-otp")
    Call<Map<String, String>> sendOtp(@Body SendOTPRequest request);

    @POST("users/logout")
    Call<Map<String, String>> logoutUser(@Body UserLogoutRequest request);
}
