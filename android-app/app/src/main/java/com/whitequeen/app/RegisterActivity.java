package com.whitequeen.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.whitequeen.app.api.ApiService;
import com.whitequeen.app.api.RetrofitClient;
import com.whitequeen.app.models.SendOTPRequest;
import com.whitequeen.app.models.UserCreateRequest;
import com.whitequeen.app.models.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);
        
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        EditText etOtp = findViewById(R.id.etOtp);
        TextView btnSendOtp = findViewById(R.id.btnSendOtp);
        ImageView btnTogglePassword = findViewById(R.id.btnTogglePassword);
        ImageView btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);

        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnTogglePassword.setAlpha(1.0f);
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnTogglePassword.setAlpha(0.5f);
                }
                etPassword.setSelection(etPassword.getText().length());
            }
        });

        btnToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirmPasswordVisible = !isConfirmPasswordVisible;
                if (isConfirmPasswordVisible) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    btnToggleConfirmPassword.setAlpha(1.0f);
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    btnToggleConfirmPassword.setAlpha(0.5f);
                }
                etConfirmPassword.setSelection(etConfirmPassword.getText().length());
            }
        });

        // Initialize icons state
        btnTogglePassword.setAlpha(0.5f);
        btnToggleConfirmPassword.setAlpha(0.5f);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUsername.getText().toString().trim();
                
                // Validate Email
                if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đúng định dạng @gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                btnSendOtp.setEnabled(false);
                btnSendOtp.setText("Đang gửi...");
                
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                apiService.sendOtp(new SendOTPRequest(email)).enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Đã gửi mã OTP đến email của bạn!", Toast.LENGTH_LONG).show();
                            btnSendOtp.setText("Đã gửi");
                        } else {
                            btnSendOtp.setEnabled(true);
                            btnSendOtp.setText("Gửi mã");
                            Toast.makeText(RegisterActivity.this, "Email này đã được đăng ký", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        btnSendOtp.setEnabled(true);
                        btnSendOtp.setText("Gửi mã");
                        Toast.makeText(RegisterActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String otp = etOtp.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || otp.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đủ thông tin và OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.endsWith("@gmail.com")) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng sử dụng tài khoản @gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(password)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu quá yếu! Phải gồm chữ hoa, chữ thường, số, ký tự đặc biệt và tối thiểu 8 ký tự.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (otp.length() != 6) {
                    Toast.makeText(RegisterActivity.this, "Nhập đủ mã OTP nhé", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnRegister.setEnabled(false);
                btnRegister.setText("Đang đăng ký...");

                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                
                String finalUsername = email.split("@")[0];
                
                UserCreateRequest request = new UserCreateRequest(finalUsername, email, password, otp);
                
                apiService.registerUser(request).enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Đăng ký");

                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finishAffinity();
                        } else {
                            if (response.code() == 422) {
                                Toast.makeText(RegisterActivity.this, "Dữ liệu không hợp lệ (Kiểm tra lại email)", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMsg = "Lỗi không xác định";
                                try {
                                    if (response.errorBody() != null) {
                                        String errorString = response.errorBody().string();
                                        // Simple parsing since we expect {"detail":"Message"}
                                        if (errorString.contains("detail")) {
                                            errorMsg = errorString.split("\"detail\":\"")[1].split("\"")[0];
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                
                                // Translate common errors to Vietnamese
                                if (errorMsg.contains("Invalid or expired OTP")) {
                                    Toast.makeText(RegisterActivity.this, "OTP sai vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (errorMsg.contains("Email already registered")) {
                                    errorMsg = "Email này đã được sử dụng";
                                } else if (errorMsg.contains("Username already registered")) {
                                    errorMsg = "Tên tài khoản này đã tồn tại";
                                }
                                
                                Toast.makeText(RegisterActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Đăng ký");
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        View.OnClickListener goToLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        if (tvLoginLink != null) tvLoginLink.setOnClickListener(goToLogin);
        if (tvGoToLogin != null) tvGoToLogin.setOnClickListener(goToLogin);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return Pattern.compile(passwordPattern).matcher(password).matches();
    }
}
