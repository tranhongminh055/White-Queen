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
import android.widget.ProgressBar;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.graphics.Color;
import androidx.appcompat.app.AlertDialog;

import com.whitequeen.app.adapters.CategoryAdapter;
import com.whitequeen.app.adapters.ProductAdapter;
import com.whitequeen.app.api.ApiService;
import com.whitequeen.app.api.RetrofitClient;
import com.whitequeen.app.models.Category;
import com.whitequeen.app.models.Product;
import com.whitequeen.app.models.ProductResponse;
import com.whitequeen.app.models.UserLogoutRequest;

import com.whitequeen.app.utils.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private View btnUser;
    private String currentUsername;
    private String currentEmail;
    private RecyclerView rvCategories, rvProducts;
    private EditText etSearch;
    private Button btnTabAll, btnTabShopee, btnTabLazada;
    private ProgressBar pbLoadingProducts;
    private TextView tvSectionTitle;

    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private String currentPlatform = "all";
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.enableFullscreenImmersiveMode(this);
        setContentView(R.layout.activity_main);

        btnUser = findViewById(R.id.btnUser);
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);
        btnTabAll = findViewById(R.id.btnTabAll);
        btnTabShopee = findViewById(R.id.btnTabShopee);
        btnTabLazada = findViewById(R.id.btnTabLazada);
        pbLoadingProducts = findViewById(R.id.pbLoadingProducts);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);

        currentUsername = getIntent().getStringExtra("USERNAME");
        if (currentUsername == null) {
            currentUsername = "Unknown";
        }
        
        currentEmail = getIntent().getStringExtra("EMAIL");
        if (currentEmail == null) {
            currentEmail = "Chưa cập nhật";
        }

        setupCategories();
        setupProductsRecyclerView();
        setupPlatformTabs();
        setupSearch();

        btnUser.setOnClickListener(v -> showUserProfileDialog());

        // Initial fetch from backend (Shopee + Lazada)
        loadProductsFromBackend(currentKeyword, currentPlatform);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UIUtils.enableFullscreenImmersiveMode(this);
        }
    }

    private void setupPlatformTabs() {
        btnTabAll.setOnClickListener(v -> {
            updateTabStyle(btnTabAll, btnTabShopee, btnTabLazada);
            currentPlatform = "all";
            tvSectionTitle.setText("TẤT CẢ SẢN PHẨM");
            loadProductsFromBackend(currentKeyword, currentPlatform);
        });

        btnTabShopee.setOnClickListener(v -> {
            updateTabStyle(btnTabShopee, btnTabAll, btnTabLazada);
            currentPlatform = "shopee";
            tvSectionTitle.setText("SẢN PHẨM SHOPEE MALL");
            loadProductsFromBackend(currentKeyword, currentPlatform);
        });

        btnTabLazada.setOnClickListener(v -> {
            updateTabStyle(btnTabLazada, btnTabAll, btnTabShopee);
            currentPlatform = "lazada";
            tvSectionTitle.setText("SẢN PHẨM LAZMALL");
            loadProductsFromBackend(currentKeyword, currentPlatform);
        });
    }

    private void updateTabStyle(Button activeBtn, Button inactiveBtn1, Button inactiveBtn2) {
        activeBtn.setBackgroundColor(Color.parseColor("#EE4D2D"));
        activeBtn.setTextColor(Color.WHITE);

        inactiveBtn1.setBackgroundColor(Color.parseColor("#EEEEEE"));
        inactiveBtn1.setTextColor(Color.parseColor("#555555"));

        inactiveBtn2.setBackgroundColor(Color.parseColor("#EEEEEE"));
        inactiveBtn2.setTextColor(Color.parseColor("#555555"));
    }

    private void setupSearch() {
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                currentKeyword = etSearch.getText().toString().trim();
                loadProductsFromBackend(currentKeyword, currentPlatform);
                return true;
            }
            return false;
        });
    }

    private void setupProductsRecyclerView() {
        productAdapter = new ProductAdapter(productList);
        productAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_EXTRA", product);
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void loadProductsFromBackend(String keyword, String platform) {
        if (pbLoadingProducts != null) {
            pbLoadingProducts.setVisibility(View.VISIBLE);
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getProducts(keyword, platform, 1, 40).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (pbLoadingProducts != null) {
                    pbLoadingProducts.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fetched = response.body().getData();
                    if (fetched != null && !fetched.isEmpty()) {
                        productList = fetched;
                        productAdapter.updateData(productList);
                        return;
                    }
                }
                loadFallbackProducts();
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                if (pbLoadingProducts != null) {
                    pbLoadingProducts.setVisibility(View.GONE);
                }
                loadFallbackProducts();
            }
        });
    }

    private void loadFallbackProducts() {
        List<Product> fallback = new ArrayList<>();
        fallback.add(new Product("[Shopee] Áo Thun Nam Nữ Form Rộng Cotton 100%", "₫149.000", "Đã bán 15.4k", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500", "Shopee", "-35%"));
        fallback.add(new Product("[Lazada] Áo Khoác Gió Nam Nữ 2 Lớp Chống Thấm", "₫219.000", "Đã bán 6.4k", "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=500", "Lazada", "-38%"));
        fallback.add(new Product("[Shopee] Tai Nghe Bluetooth 5.3 Chống Ồn ENC", "₫289.000", "Đã bán 8.2k", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500", "Shopee", "-42%"));
        fallback.add(new Product("[Lazada] Chuột Không Dây Gaming Silent Click RGB", "₫165.000", "Đã bán 11.2k", "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=500", "Lazada", "-45%"));
        fallback.add(new Product("[Shopee] Giày Sneaker Thể Thao Nam Nữ Siêu Nhẹ", "₫320.000", "Đã bán 12.8k", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500", "Shopee", "-20%"));
        fallback.add(new Product("[Lazada] Bình Giữ Nhiệt Inox 316 Cao Cấp 24H 800ml", "₫139.000", "Đã bán 7.8k", "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=500", "Lazada", "-25%"));
        productList = fallback;
        productAdapter.updateData(productList);
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
        categoryList.add(new Category(1, "ShopeeFood\nFlash 50%", android.R.drawable.ic_menu_agenda));
        categoryList.add(new Category(2, "Shopee Mart", android.R.drawable.ic_menu_manage));
        categoryList.add(new Category(3, "ShopeeVIP", android.R.drawable.ic_menu_view));
        categoryList.add(new Category(4, "Deal Từ 1.000Đ", android.R.drawable.ic_menu_directions));
        categoryList.add(new Category(5, "Shopee Siêu Rẻ", android.R.drawable.ic_menu_compass));
        categoryList.add(new Category(6, "Thời Trang Nam", android.R.drawable.ic_menu_camera));
        categoryList.add(new Category(7, "Điện Thoại", android.R.drawable.ic_menu_call));
        categoryList.add(new Category(8, "Mỹ Phẩm", android.R.drawable.ic_menu_gallery));

        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);
    }
}
