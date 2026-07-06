package com.example.project_new_take_out.ui.favorite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.ui.shop.ShopDetailActivity;
import com.example.project_new_take_out.utils.ImageUtils;
import com.example.project_new_take_out.utils.UserManager;
import com.example.project_new_take_out.app.BaseActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 我的收藏页面（按用户隔离存储）
 */
public class FavoriteListActivity extends BaseActivity {

    private static final String PREFS_NAME = "user_favorites";

    // 默认收藏池（新用户首次使用时的预置数据）
    private static final Shop[] DEFAULT_FAVORITES = {
        new Shop(1, "川味居·麻辣烫", "", 4.8f, 3286, 20, 3, 30, 1.2, null),
        new Shop(3, "鱼鲜生·日料寿司", "", 4.9f, 2105, 30, 5, 40, 2.5, null),
        new Shop(5, "甜蜜蜜·奶茶烘焙", "", 4.5f, 7823, 10, 2, 20, 0.5, null),
    };

    private List<Shop> favorites = new ArrayList<>();
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        RecyclerView recycler = findViewById(R.id.recycler_favorites);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteAdapter();
        recycler.setAdapter(adapter);

        loadFavorites();
    }

    private void loadFavorites() {
        Set<String> savedIds = getSavedFavoriteIds();
        favorites.clear();

        if (savedIds.isEmpty()) {
            // 首次使用：填充默认收藏
            for (Shop shop : DEFAULT_FAVORITES) {
                favorites.add(shop);
                saveShopId(shop.getId());
            }
        } else {
            for (String idStr : savedIds) {
                int shopId = Integer.parseInt(idStr);
                Shop shop = findShopById(shopId);
                if (shop != null) favorites.add(shop);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private Shop findShopById(int id) {
        for (Shop s : DEFAULT_FAVORITES) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    private Set<String> getSavedFavoriteIds() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = UserManager.getInstance().getUserId();
        return prefs.getStringSet(userId, new HashSet<>());
    }

    private void saveShopId(int shopId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = UserManager.getInstance().getUserId();
        Set<String> ids = new HashSet<>(prefs.getStringSet(userId, new HashSet<>()));
        ids.add(String.valueOf(shopId));
        prefs.edit().putStringSet(userId, ids).apply();
    }

    private void removeShopId(int shopId) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = UserManager.getInstance().getUserId();
        Set<String> ids = new HashSet<>(prefs.getStringSet(userId, new HashSet<>()));
        ids.remove(String.valueOf(shopId));
        prefs.edit().putStringSet(userId, ids).apply();
    }

    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.VH> {
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(FavoriteListActivity.this).inflate(R.layout.item_shop_card, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Shop shop = favorites.get(pos);
            h.tvName.setText(shop.getName());
            h.tvScore.setText(String.format("%.1f", shop.getScore()));
            h.tvSales.setText("月售" + shop.getMonthlySales());

            Glide.with(FavoriteListActivity.this)
                    .load(ImageUtils.getShopDrawable(shop.getId()))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(8))))
                    .into(h.ivImage);

            h.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(FavoriteListActivity.this, ShopDetailActivity.class);
                intent.putExtra("shop_id", shop.getId());
                intent.putExtra("shop_name", shop.getName());
                startActivity(intent);
            });

            h.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(FavoriteListActivity.this)
                        .setMessage("取消收藏「" + shop.getName() + "」？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", (d, w) -> {
                            removeShopId(shop.getId());
                            favorites.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, favorites.size());
                            Toast.makeText(FavoriteListActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        })
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() { return favorites.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvScore, tvSales;
            VH(@NonNull View v) {
                super(v);
                ivImage = v.findViewById(R.id.iv_shop_image);
                tvName = v.findViewById(R.id.tv_shop_name);
                tvScore = v.findViewById(R.id.tv_score);
                tvSales = v.findViewById(R.id.tv_monthly_sales);
            }
        }

        private int dp2px(int dp) {
            return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
        }
    }
}
