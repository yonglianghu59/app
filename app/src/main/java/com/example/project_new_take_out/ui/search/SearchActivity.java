package com.example.project_new_take_out.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.ui.shop.ShopDetailActivity;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索页面
 * 搜索历史 + 热门搜索 + 实时搜索结果
 */
public class SearchActivity extends BaseActivity {

    private EditText etSearch;
    private RecyclerView recyclerResults;
    private LinearLayout layoutHistory, layoutNoResult;
    private LinearLayout layoutHistoryTags, layoutHotTags;
    private List<String> historyList = new ArrayList<>();
    private List<String> hotList = new ArrayList<>();
    private List<Shop> allShops = new ArrayList<>();
    private List<Shop> resultShops = new ArrayList<>();
    private ShopResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        initViews();
        loadMockData();
        buildHistoryTags();
        buildHotTags();

        // 自动弹出键盘
        etSearch.requestFocus();
        etSearch.postDelayed(() -> {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etSearch, 0);
        }, 300);
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        recyclerResults = findViewById(R.id.recycler_search_results);
        layoutHistory = findViewById(R.id.layout_history);
        layoutNoResult = findViewById(R.id.layout_no_result);
        layoutHistoryTags = findViewById(R.id.layout_history_tags);
        layoutHotTags = findViewById(R.id.layout_hot_tags);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.tv_clear_history).setOnClickListener(v -> {
            historyList.clear();
            buildHistoryTags();
            Toast.makeText(this, "已清空搜索历史", Toast.LENGTH_SHORT).show();
        });

        // 搜索按钮
        findViewById(R.id.tv_search_btn).setOnClickListener(v -> doSearch());
    }

    private void doSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        // 加入搜索历史（去重）
        historyList.remove(keyword);
        historyList.add(0, keyword);
        if (historyList.size() > 10) historyList.remove(historyList.size() - 1);
        buildHistoryTags();

        // 搜索
        resultShops.clear();
        for (Shop shop : allShops) {
            if (shop.getName().contains(keyword) || hasTag(shop, keyword)) {
                resultShops.add(shop);
            }
        }

        // 显示结果
        if (resultShops.isEmpty()) {
            recyclerResults.setVisibility(View.GONE);
            layoutNoResult.setVisibility(View.VISIBLE);
        } else {
            recyclerResults.setVisibility(View.VISIBLE);
            layoutNoResult.setVisibility(View.GONE);
            resultAdapter.notifyDataSetChanged();
        }

        // 隐藏键盘
        android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
    }

    private boolean hasTag(Shop shop, String keyword) {
        if (shop.getTags() != null) {
            for (String tag : shop.getTags()) {
                if (tag.contains(keyword)) return true;
            }
        }
        return false;
    }

    private void buildHistoryTags() {
        layoutHistoryTags.removeAllViews();
        if (historyList.isEmpty()) {
            layoutHistory.setVisibility(View.GONE);
            return;
        }
        layoutHistory.setVisibility(View.VISIBLE);
        buildFlowTags(layoutHistoryTags, historyList, true);
    }

    private void buildHotTags() {
        layoutHotTags.removeAllViews();
        buildFlowTags(layoutHotTags, hotList, false);
    }

    /** 构建流式标签布局（自动换行） */
    private void buildFlowTags(LinearLayout container, List<String> tags, boolean isHistory) {
        final int maxPerRow = 3;
        LinearLayout currentRow = null;
        for (int i = 0; i < tags.size(); i++) {
            if (i % maxPerRow == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                container.addView(currentRow);
            }
            if (currentRow != null) {
                currentRow.addView(createTagView(tags.get(i), isHistory));
            }
        }
    }

    private TextView createTagView(String text, boolean isHistory) {
        TextView tag = new TextView(this);
        tag.setText(text);
        tag.setTextSize(13);
        tag.setTextColor(isHistory ? getColor(R.color.textSecondary) : getColor(R.color.colorPrimary));
        tag.setBackgroundResource(R.drawable.bg_search_bar);
        tag.setPadding(dp2px(12), dp2px(6), dp2px(12), dp2px(6));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, dp2px(8), dp2px(8));
        tag.setLayoutParams(params);
        tag.setOnClickListener(v -> {
            etSearch.setText(text);
            etSearch.setSelection(text.length());
            doSearch();
        });
        return tag;
    }

    private void loadMockData() {
        // 热门搜索
        hotList.add("麻辣烫");
        hotList.add("奶茶");
        hotList.add("汉堡");
        hotList.add("寿司");
        hotList.add("火锅");
        hotList.add("烧烤");

        // 模拟店铺数据
        allShops.add(new Shop(1, "川味居·麻辣烫", "", 4.8f, 3286, 20, 3, 30, 1.2,
                java.util.Arrays.asList("新店", "满减", "辣")));
        allShops.add(new Shop(2, "麦乐送·汉堡炸鸡", "", 4.6f, 5612, 15, 2, 25, 0.8,
                java.util.Arrays.asList("快送", "满减", "首单立减")));
        allShops.add(new Shop(3, "鱼鲜生·日料寿司", "", 4.9f, 2105, 30, 5, 40, 2.5,
                java.util.Arrays.asList("品质", "满减", "红包")));
        allShops.add(new Shop(4, "湘味轩·经典湘菜", "", 4.7f, 4432, 25, 4, 35, 1.5,
                java.util.Arrays.asList("地道", "满减", "新品")));
        allShops.add(new Shop(5, "甜蜜蜜·奶茶烘焙", "", 4.5f, 7823, 10, 2, 20, 0.5,
                java.util.Arrays.asList("人气", "满减", "下午茶")));
        allShops.add(new Shop(6, "粤港茶餐厅", "", 4.4f, 1650, 35, 4, 45, 3.0,
                java.util.Arrays.asList("品质", "满减")));

        // 搜索结果 RecyclerView
        recyclerResults.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new ShopResultAdapter();
        recyclerResults.setAdapter(resultAdapter);
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // ========== 搜索结果 Adapter ==========

    private class ShopResultAdapter extends RecyclerView.Adapter<ShopResultAdapter.VH> {
        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(SearchActivity.this)
                    .inflate(R.layout.item_shop_card, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int pos) {
            Shop shop = resultShops.get(pos);
            h.tvName.setText(shop.getName());
            h.tvScore.setText(String.format("%.1f", shop.getScore()));
            h.tvSales.setText("月售" + shop.getMonthlySales());
            Glide.with(SearchActivity.this)
                    .load(ImageUtils.getShopDrawable(shop.getId()))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(8))))
                    .into(h.ivImage);
            h.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(SearchActivity.this, ShopDetailActivity.class);
                intent.putExtra("shop_id", shop.getId());
                intent.putExtra("shop_name", shop.getName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return resultShops.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvScore, tvSales;
            VH(View v) {
                super(v);
                ivImage = v.findViewById(R.id.iv_shop_image);
                tvName = v.findViewById(R.id.tv_shop_name);
                tvScore = v.findViewById(R.id.tv_score);
                tvSales = v.findViewById(R.id.tv_monthly_sales);
            }
        }
    }
}
