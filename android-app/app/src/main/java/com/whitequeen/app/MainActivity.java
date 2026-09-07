package com.whitequeen.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.LayoutInflater;
import androidx.appcompat.app.AlertDialog;

import com.whitequeen.app.adapters.CategoryAdapter;
import com.whitequeen.app.adapters.ProductAdapter;
import com.whitequeen.app.api.ApiService;
import com.whitequeen.app.api.RetrofitClient;
import com.whitequeen.app.models.Category;
import com.whitequeen.app.models.Product;
import com.whitequeen.app.models.UserLogoutRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ImageView btnUser;
    private String currentUsername;
    private String currentEmail;
    private RecyclerView rvCategories, rvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUser = findViewById(R.id.btnUser);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);

        currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) {
            currentUsername = "Unknown";
        }
        
        currentEmail = getIntent().getStringExtra("EMAIL");
        if (currentEmail == null) {
            currentEmail = "Chưa cập nhật";
        }
        setupCategories();
        setupProducts();

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserProfileDialog();
            }
        });
    }

    private void showUserProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_profile, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        TextView tvUsername = dialogView.findViewById(R.id.tvProfileUsername);
        TextView tvEmail = dialogView.findViewById(R.id.tvProfileEmail);
        EditText etPhone = dialogView.findViewById(R.id.etProfilePhone);
        EditText etAddress = dialogView.findViewById(R.id.etProfileAddress);
        Button btnSave = dialogView.findViewById(R.id.btnProfileSave);
        Button btnLogout = dialogView.findViewById(R.id.btnProfileLogout);

        tvUsername.setText(currentUsername);
        tvEmail.setText(currentEmail);
        
        btnSave.setOnClickListener(view -> {
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            Toast.makeText(MainActivity.this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnLogout.setOnClickListener(view -> {
            dialog.dismiss();
            performLogout();
        });

        dialog.show();
    }
    
    private void performLogout() {
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
                Toast.makeText(MainActivity.this, "Đã đăng xuất (Offline)!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });
    }

    private void setupCategories() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(1, "Thời Trang", android.R.drawable.ic_menu_camera));
        categoryList.add(new Category(2, "Điện Thoại", android.R.drawable.ic_menu_call));
        categoryList.add(new Category(3, "Mỹ Phẩm", android.R.drawable.ic_menu_view));
        categoryList.add(new Category(4, "Đồ Gia Dụng", android.R.drawable.ic_menu_manage));
        categoryList.add(new Category(5, "Giày Dép", android.R.drawable.ic_menu_directions));
        categoryList.add(new Category(6, "Thể Thao", android.R.drawable.ic_menu_compass));
        categoryList.add(new Category(7, "Sách", android.R.drawable.ic_menu_agenda));
        categoryList.add(new Category(8, "Mẹ & Bé", android.R.drawable.ic_menu_gallery));

        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);
    }

    private void setupProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1, "Áo Thun Nam Cao Cấp", "₫150.000", "Đã bán 5k", android.R.drawable.ic_menu_gallery));
        productList.add(new Product(2, "Tai Nghe Bluetooth", "₫250.000", "Đã bán 12k", android.R.drawable.ic_menu_gallery));
        productList.add(new Product(3, "Son Môi Chính Hãng", "₫300.000", "Đã bán 2k", android.R.drawable.ic_menu_gallery));
        productList.add(new Product(4, "Giày Thể Thao Đẹp", "₫450.000", "Đã bán 800", android.R.drawable.ic_menu_gallery));
        productList.add(new Product(5, "Ốp Lưng Điện Thoại", "₫25.000", "Đã bán 15k", android.R.drawable.ic_menu_gallery));
        productList.add(new Product(6, "Balo Du Lịch", "₫199.000", "Đã bán 3k", android.R.drawable.ic_menu_gallery));

        ProductAdapter adapter = new ProductAdapter(productList);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(adapter);
    }
}
