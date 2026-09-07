package com.whitequeen.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.whitequeen.app.models.Product;
import com.whitequeen.app.utils.UIUtils;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView btnBack, btnShare, btnCart, ivDetailProductImage, ivShopAvatar;
    private TextView tvHeaderTitle, tvDetailPlatformBadge, tvDetailDiscountBadge;
    private TextView tvDetailPrice, tvDetailOriginalPrice, tvDetailProductName;
    private TextView tvDetailRating, tvDetailSold, tvGuaranteeText, tvShopName;
    private TextView tvDetailCategory, tvDetailPlatformSource, tvDetailDescription;
    private MaterialButton btnViewShop;
    private Button btnBuyNow;
    private LinearLayout btnChat, btnAddToCart, layoutVoucherSection;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UIUtils.enableFullscreenImmersiveMode(this);
        setContentView(R.layout.activity_product_detail);

        initViews();
        getIntentData();
        setupListeners();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            UIUtils.enableFullscreenImmersiveMode(this);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnCart = findViewById(R.id.btnCart);
        ivDetailProductImage = findViewById(R.id.ivDetailProductImage);
        ivShopAvatar = findViewById(R.id.ivShopAvatar);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvDetailPlatformBadge = findViewById(R.id.tvDetailPlatformBadge);
        tvDetailDiscountBadge = findViewById(R.id.tvDetailDiscountBadge);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailOriginalPrice = findViewById(R.id.tvDetailOriginalPrice);
        tvDetailProductName = findViewById(R.id.tvDetailProductName);
        tvDetailRating = findViewById(R.id.tvDetailRating);
        tvDetailSold = findViewById(R.id.tvDetailSold);
        tvGuaranteeText = findViewById(R.id.tvGuaranteeText);
        tvShopName = findViewById(R.id.tvShopName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailPlatformSource = findViewById(R.id.tvDetailPlatformSource);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnViewShop = findViewById(R.id.btnViewShop);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnChat = findViewById(R.id.btnChat);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        layoutVoucherSection = findViewById(R.id.layoutVoucherSection);

        // Strikethrough for original price
        tvDetailOriginalPrice.setPaintFlags(tvDetailOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void getIntentData() {
        product = (Product) getIntent().getSerializableExtra("PRODUCT_EXTRA");

        if (product == null) {
            product = new Product("[Shopee] Áo Thun Nam Nữ Form Rộng Cotton 100%", "₫149.000", "Đã bán 15.4k", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500", "Shopee", "-35%");
            product.setCategory("Thời Trang Nam / Áo");
            product.setRating(4.9f);
        }
        populateProductData(product);
    }

    private void populateProductData(Product item) {
        tvDetailProductName.setText(item.getName());
        tvDetailPrice.setText(item.getPrice());
        tvDetailSold.setText(item.getSold());

        // Platform Styling
        String platform = item.getPlatform() != null ? item.getPlatform() : "Shopee";
        btnBuyNow.setText("MUA NGAY");
        if (platform.equalsIgnoreCase("Lazada")) {
            tvDetailPlatformBadge.setText("LazMall");
            tvDetailPlatformBadge.setBackgroundColor(Color.parseColor("#0F1568"));
            tvShopName.setText("Lazada Flagship Store");
            tvGuaranteeText.setText("Lazada Guarantee | 15 Ngày Đổi Trả Đảm Bảo");
            tvDetailPlatformSource.setText("Lazada Vietnam Official");
            btnBuyNow.setBackgroundColor(Color.parseColor("#0F1568"));
            btnViewShop.setTextColor(Color.parseColor("#0F1568"));
            btnViewShop.setStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#0F1568")));
        } else {
            tvDetailPlatformBadge.setText("Shopee Mall");
            tvDetailPlatformBadge.setBackgroundColor(Color.parseColor("#EE4D2D"));
            tvShopName.setText("Shopee Official Store");
            tvGuaranteeText.setText("Shopee Đảm Bảo | 3 Ngày Trả Hàng / Hoàn Tiền");
            tvDetailPlatformSource.setText("Shopee Vietnam Official");
            btnBuyNow.setBackgroundColor(Color.parseColor("#EE4D2D"));
            btnViewShop.setTextColor(Color.parseColor("#EE4D2D"));
            btnViewShop.setStrokeColor(android.content.res.ColorStateList.valueOf(Color.parseColor("#EE4D2D")));
        }

        // Discount
        if (item.getDiscount() != null && !item.getDiscount().isEmpty()) {
            tvDetailDiscountBadge.setVisibility(View.VISIBLE);
            tvDetailDiscountBadge.setText(item.getDiscount());
            // Calculate a estimated original price for display
            tvDetailOriginalPrice.setVisibility(View.VISIBLE);
            tvDetailOriginalPrice.setText(calculateOriginalPrice(item.getPrice()));
        } else {
            tvDetailDiscountBadge.setVisibility(View.GONE);
            tvDetailOriginalPrice.setVisibility(View.GONE);
        }

        // Category & Rating
        if (item.getCategory() != null && !item.getCategory().isEmpty()) {
            tvDetailCategory.setText(item.getCategory());
        } else {
            tvDetailCategory.setText("Hàng Tiêu Dùng / Nổi Bật");
        }

        if (item.getRating() != null) {
            tvDetailRating.setText("★ " + item.getRating());
        } else {
            tvDetailRating.setText("★ 4.9");
        }

        // Image Loading via Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(item.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivDetailProductImage);
        } else if (item.getImageResId() != 0) {
            ivDetailProductImage.setImageResource(item.getImageResId());
        } else {
            ivDetailProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private String calculateOriginalPrice(String currentPriceStr) {
        try {
            String clean = currentPriceStr.replaceAll("[^0-9]", "");
            if (!clean.isEmpty()) {
                long price = Long.parseLong(clean);
                long original = (long) (price * 1.45);
                return "₫" + String.format("%,d", original).replace(',', '.');
            }
        } catch (Exception ignored) {}
        return "₫290.000";
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, product.getName());
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem ngay sản phẩm " + product.getName() + " giá " + product.getPrice() + " trên ứng dụng White Queen!");
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ sản phẩm"));
        });

        btnCart.setOnClickListener(v -> Toast.makeText(this, "Giỏ hàng của bạn đang có 1 sản phẩm", Toast.LENGTH_SHORT).show());

        btnChat.setOnClickListener(v -> Toast.makeText(this, "Đang mở cửa sổ chat với " + tvShopName.getText() + "...", Toast.LENGTH_SHORT).show());

        btnAddToCart.setOnClickListener(v -> Toast.makeText(this, "Đã thêm \"" + product.getName() + "\" vào giỏ hàng!", Toast.LENGTH_SHORT).show());

        btnViewShop.setOnClickListener(v -> Toast.makeText(this, "Đang mở trang cửa hàng...", Toast.LENGTH_SHORT).show());

        if (layoutVoucherSection != null) {
            layoutVoucherSection.setOnClickListener(v -> showVoucherBottomSheet());
        }

        btnBuyNow.setOnClickListener(v -> {
            if (product.getProductUrl() != null && !product.getProductUrl().isEmpty() && product.getProductUrl().startsWith("http")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getProductUrl()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "Đang chuyển hướng tới trang mua hàng " + product.getPlatform() + "...", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showVoucherBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_voucher, null);
        dialog.setContentView(dialogView);

        ImageView btnClose = dialogView.findViewById(R.id.btnCloseVoucherDialog);
        Button btnClaim1 = dialogView.findViewById(R.id.btnClaimVoucher1);
        Button btnClaim2 = dialogView.findViewById(R.id.btnClaimVoucher2);
        Button btnClaim3 = dialogView.findViewById(R.id.btnClaimVoucher3);
        Button btnDone = dialogView.findViewById(R.id.btnDoneVoucher);

        if (btnClose != null) btnClose.setOnClickListener(v -> dialog.dismiss());
        if (btnDone != null) btnDone.setOnClickListener(v -> dialog.dismiss());

        setupClaimButton(btnClaim1, "Giảm ₫15.000");
        setupClaimButton(btnClaim2, "Freeship Extra");
        setupClaimButton(btnClaim3, "Hoàn 8% Xu");

        dialog.show();
    }

    private void setupClaimButton(Button button, String voucherTitle) {
        if (button == null) return;
        button.setOnClickListener(v -> {
            button.setText("ĐÃ LƯU");
            button.setEnabled(false);
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));
            Toast.makeText(this, "Đã lưu mã \"" + voucherTitle + "\" vào Ví Voucher!", Toast.LENGTH_SHORT).show();
        });
    }
}
