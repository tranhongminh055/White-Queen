package com.whitequeen.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

import com.whitequeen.app.api.ApiService;
import com.whitequeen.app.api.RetrofitClient;
import com.whitequeen.app.models.UserLogoutRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText inputMessage;
    private Button btnSend;
    private Button btnLogout;
    private TextView tvChat;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        btnLogout = findViewById(R.id.btnLogout);
        tvChat = findViewById(R.id.tvChat);

        currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) {
            currentUsername = "Unknown";
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMessage.getText().toString();
                if (!message.isEmpty()) {
                    tvChat.append("\nYou: " + message);
                    inputMessage.setText("");

                    tvChat.append("\nWhite Queen: Đang xử lý câu hỏi của bạn...");
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogout.setEnabled(false);
                btnLogout.setText("Đang đăng xuất...");

                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                apiService.logoutUser(new UserLogoutRequest(currentUsername)).enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        Toast.makeText(MainActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        // Logout locally even if API fails
                        Toast.makeText(MainActivity.this, "Đã đăng xuất (Offline)!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finishAffinity();
                    }
                });
            }
        });
    }
}
