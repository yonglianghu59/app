package com.example.project_new_take_out.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.Shop;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺列表 Adapter（支持 Header View）
 */
public class ShopListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;
    private List<Shop> shopList = new ArrayList<>();
    private View headerView;
    private OnShopClickListener onShopClickListener;

    public interface OnShopClickListener {
        void onShopClick(Shop shop, int position);
    }

    public ShopListAdapter(Context context) {
        this.context = context;
    }

    public void setOnShopClickListener(OnShopClickListener listener) {
        this.onShopClickListener = listener;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        notifyDataSetChanged();
    }

    public boolean hasHeader() {
        return headerView != null;
    }

    public void setShopList(List<Shop> shopList) {
        this.shopList = shopList != null ? shopList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeader() && position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && headerView != null) {
            return new HeaderViewHolder(headerView);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            int dataPos = hasHeader() ? position - 1 : position;
            Shop shop = shopList.get(dataPos);
            ((ItemViewHolder) holder).bind(shop);

            int finalDataPos = dataPos;
            holder.itemView.setOnClickListener(v -> {
                if (onShopClickListener != null) {
                    onShopClickListener.onShopClick(shop, finalDataPos);
                }
            });
        }
        // Header 无需绑定数据
    }

    @Override
    public int getItemCount() {
        int count = shopList.size();
        if (hasHeader()) count += 1;
        return count;
    }

    /**
     * Header ViewHolder
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * Item ViewHolder
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivShopImage;
        TextView tvShopName, tvScore, tvMonthlySales, tvMinPrice;
        TextView tvDeliveryFee, tvDeliveryTime, tvDistance, tvTags;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShopImage = itemView.findViewById(R.id.iv_shop_image);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            tvScore = itemView.findViewById(R.id.tv_score);
            tvMonthlySales = itemView.findViewById(R.id.tv_monthly_sales);
            tvMinPrice = itemView.findViewById(R.id.tv_min_price);
            tvDeliveryFee = itemView.findViewById(R.id.tv_delivery_fee);
            tvDeliveryTime = itemView.findViewById(R.id.tv_delivery_time);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvTags = itemView.findViewById(R.id.tv_tags);
        }

        void bind(Shop shop) {
            tvShopName.setText(shop.getName());
            tvScore.setText(String.format("%.1f", shop.getScore()));
            tvMonthlySales.setText("月售" + shop.getMonthlySales());
            tvMinPrice.setText("¥" + (int) shop.getMinPrice() + "起送");
            tvDeliveryFee.setText("配送费¥" + (int) shop.getDeliveryFee());
            tvDeliveryTime.setText(shop.getDeliveryTime() + "分钟");
            tvDistance.setText(shop.getDistance() + "km");

            if (shop.getTags() != null && !shop.getTags().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String tag : shop.getTags()) {
                    sb.append(tag).append(" ");
                }
                tvTags.setText(sb.toString().trim());
                tvTags.setVisibility(View.VISIBLE);
            } else {
                tvTags.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(com.example.project_new_take_out.utils.ImageUtils.getShopDrawable(shop.getId()))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(8))))
                    .placeholder(R.drawable.shop_1)
                    .error(R.drawable.shop_1)
                    .into(ivShopImage);
        }

        private int dp2px(int dp) {
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5f);
        }
    }
}
