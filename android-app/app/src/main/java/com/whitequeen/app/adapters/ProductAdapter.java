package com.whitequeen.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.whitequeen.app.R;
import com.whitequeen.app.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(product.getPrice());
        holder.tvProductSold.setText(product.getSold());

        // Platform Badge Styling
        String platform = product.getPlatform() != null ? product.getPlatform() : "Shopee";
        holder.tvPlatformBadge.setText(platform);
        if (platform.equalsIgnoreCase("Lazada")) {
            holder.tvPlatformBadge.setBackgroundColor(Color.parseColor("#0F1568")); // Lazada Deep Blue
        } else {
            holder.tvPlatformBadge.setBackgroundColor(Color.parseColor("#EE4D2D")); // Shopee Orange Red
        }

        // Discount Badge
        if (product.getDiscount() != null && !product.getDiscount().isEmpty()) {
            holder.tvDiscountBadge.setVisibility(View.VISIBLE);
            holder.tvDiscountBadge.setText(product.getDiscount());
        } else {
            holder.tvDiscountBadge.setVisibility(View.GONE);
        }

        // Image Loading via Glide
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivProductImage);
        } else if (product.getImageResId() != 0) {
            holder.ivProductImage.setImageResource(product.getImageResId());
        } else {
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvPlatformBadge;
        TextView tvDiscountBadge;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductSold;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvPlatformBadge = itemView.findViewById(R.id.tvPlatformBadge);
            tvDiscountBadge = itemView.findViewById(R.id.tvDiscountBadge);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductSold = itemView.findViewById(R.id.tvProductSold);
        }
    }
}
