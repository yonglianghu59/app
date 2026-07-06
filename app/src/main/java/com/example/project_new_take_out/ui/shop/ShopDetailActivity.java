package com.example.project_new_take_out.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.adapter.CategoryAdapter;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.adapter.FoodListAdapter;
import com.example.project_new_take_out.model.Category;
import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.ui.food.FoodDetailActivity;
import com.example.project_new_take_out.ui.order.OrderConfirmActivity;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;
import com.example.project_new_take_out.viewmodel.ShopViewModel;

import java.util.List;

/**
 * 店铺详情页
 * 双 RecyclerView 联动：左侧分类 + 右侧菜品列表
 */
public class ShopDetailActivity extends BaseActivity {

    private ShopViewModel viewModel;

    // 店铺头部
    private TextView tvShopName, tvShopTags, tvShopScore, tvShopSales, tvShopDeliveryInfo;

    // 分类标签栏
    private LinearLayout layoutCategoryTabs;

    // 双列表
    private RecyclerView recyclerCategory, recyclerFood;
    private CategoryAdapter categoryAdapter;
    private FoodListAdapter foodListAdapter;

    // 购物车栏
    private View layoutCartBar;
    private TextView tvCartCount, tvCartTotal, btnSettle;

    private int shopId;
    private String shopName;
    private List<Category> categoryList;
    private boolean isCategoryClickScrolling = false; // 防止联动循环触发

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        shopId = getIntent().getIntExtra("shop_id", 1);
        shopName = getIntent().getStringExtra("shop_name");

        viewModel = new ViewModelProvider(this).get(ShopViewModel.class);

        initViews();
        initRecyclerViews();
        initCategoryTabs();
        observeViewModel();
        observeCart();

        viewModel.loadShopData(shopId);
    }

    private void initViews() {
        tvShopName = findViewById(R.id.tv_shop_name);
        tvShopTags = findViewById(R.id.tv_shop_tags);
        tvShopScore = findViewById(R.id.tv_shop_score);
        tvShopSales = findViewById(R.id.tv_shop_sales);
        tvShopDeliveryInfo = findViewById(R.id.tv_shop_delivery_info);

        recyclerCategory = findViewById(R.id.recycler_category);
        recyclerFood = findViewById(R.id.recycler_food);

        layoutCartBar = findViewById(R.id.layout_cart_bar);
        tvCartCount = findViewById(R.id.tv_cart_count);
        tvCartTotal = findViewById(R.id.tv_cart_total);
        btnSettle = findViewById(R.id.btn_settle);

        layoutCategoryTabs = findViewById(R.id.layout_category_tabs);

        // 返回键
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void initRecyclerViews() {
        // 左侧分类列表
        categoryAdapter = new CategoryAdapter(this);
        categoryAdapter.setOnCategoryClickListener((category, position) -> {
            isCategoryClickScrolling = true;
            categoryAdapter.setSelectedPosition(position);
            viewModel.setSelectedCategoryPosition(position);

            // 顶部标签栏同步
            updateTabSelection(position);

            // 右侧菜品列表滚动到对应分类
            if (foodListAdapter != null && categoryList != null && position < categoryList.size()) {
                scrollFoodListToCategory(position);
            }
        });
        recyclerCategory.setLayoutManager(new LinearLayoutManager(this));
        recyclerCategory.setAdapter(categoryAdapter);

        // 右侧菜品列表
        foodListAdapter = new FoodListAdapter(this);
        foodListAdapter.setShopInfo(shopId, shopName);
        foodListAdapter.setOnFoodClickListener((food, position) -> {
            // 跳转菜品详情页
            Intent intent = new Intent(ShopDetailActivity.this, FoodDetailActivity.class);
            intent.putExtra("food_id", food.getId());
            intent.putExtra("shop_id", shopId);
            startActivity(intent);
        });
        LinearLayoutManager foodLayoutManager = new LinearLayoutManager(this);
        recyclerFood.setLayoutManager(foodLayoutManager);
        recyclerFood.setAdapter(foodListAdapter);

        // 右侧列表滚动 → 左侧分类联动
        recyclerFood.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (isCategoryClickScrolling) return;
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null) {
                    int firstVisiblePos = lm.findFirstVisibleItemPosition();
                    // 找到当前可见的第一个 item 所属的分类
                    updateCategorySelectionByFoodPosition(firstVisiblePos);
                }
            }
        });

        // 去结算
        btnSettle.setOnClickListener(v -> {
            if (CartManager.getInstance().isEmpty()) {
                com.example.project_new_take_out.utils.ToastUtils.showShort(this, "购物车为空");
                return;
            }
            Intent intent = new Intent(ShopDetailActivity.this, OrderConfirmActivity.class);
            intent.putExtra("shop_id", shopId);
            intent.putExtra("shop_name", shopName);
            startActivity(intent);
        });
    }

    /**
     * 初始化顶部分类标签栏
     */
    private void initCategoryTabs() {
        // 标签栏数据将在数据加载后动态填充
    }

    private void buildCategoryTabs(List<Category> categories) {
        layoutCategoryTabs.removeAllViews();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            TextView tab = new TextView(this);
            tab.setText(category.getName());
            tab.setTextSize(13);
            tab.setPadding(dp2px(12), 0, dp2px(12), 0);
            tab.setGravity(android.view.Gravity.CENTER);

            int finalI = i;
            tab.setOnClickListener(v -> {
                updateTabSelection(finalI);
                categoryAdapter.setSelectedPosition(finalI);
                isCategoryClickScrolling = true;
                scrollFoodListToCategory(finalI);
            });
            layoutCategoryTabs.addView(tab);
        }
        updateTabSelection(0);
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < layoutCategoryTabs.getChildCount(); i++) {
            TextView tab = (TextView) layoutCategoryTabs.getChildAt(i);
            boolean selected = i == position;
            tab.setTextColor(getColor(selected ? R.color.colorPrimary : R.color.textHint));
        }
    }

    private void scrollFoodListToCategory(int categoryPos) {
        // 计算该分类在菜品列表中的起始位置
        int itemPos = 0;
        if (categoryList != null) {
            for (int i = 0; i < categoryPos && i < categoryList.size(); i++) {
                Category cat = categoryList.get(i);
                itemPos += (cat.getFoods() != null ? cat.getFoods().size() : 0);
            }
        }
        ((LinearLayoutManager) recyclerFood.getLayoutManager())
                .scrollToPositionWithOffset(itemPos, 0);
        recyclerFood.postDelayed(() -> isCategoryClickScrolling = false, 200);
    }

    private void updateCategorySelectionByFoodPosition(int foodPos) {
        if (categoryList == null) return;
        int cumulative = 0;
        for (int i = 0; i < categoryList.size(); i++) {
            Category cat = categoryList.get(i);
            cumulative += (cat.getFoods() != null ? cat.getFoods().size() : 0);
            if (foodPos < cumulative) {
                categoryAdapter.setSelectedPosition(i);
                updateTabSelection(i);
                break;
            }
        }
    }

    private void observeViewModel() {
        viewModel.getShopDetailLiveData().observe(this, shop -> {
            if (shop != null) {
                updateShopHeader(shop);
            }
        });

        viewModel.getCategoryListLiveData().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                this.categoryList = categories;
                categoryAdapter.setCategoryList(categories);
                buildCategoryTabs(categories);
                // 合并所有菜品到一个列表供右侧 RecyclerView 使用
                updateFoodList(categories);
            }
        });

        viewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            findViewById(R.id.progress_bar).setVisibility(
                    Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });
    }

    private void updateShopHeader(Shop shop) {
        tvShopName.setText(shop.getName());
        tvShopScore.setText("评分 " + shop.getScore());
        tvShopSales.setText("月售 " + shop.getMonthlySales());

        // 标签
        if (shop.getTags() != null && !shop.getTags().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String tag : shop.getTags()) {
                sb.append(tag).append(" · ");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 3);
            tvShopTags.setText(sb.toString());
        }

        tvShopDeliveryInfo.setText("配送费¥" + (int) shop.getDeliveryFee()
                + " | 起送¥" + (int) shop.getMinPrice()
                + " | 约" + shop.getDeliveryTime() + "分钟");
    }

    private void updateFoodList(List<Category> categories) {
        java.util.List<Food> allFoods = new java.util.ArrayList<>();
        for (Category cat : categories) {
            if (cat.getFoods() != null) {
                allFoods.addAll(cat.getFoods());
            }
        }
        foodListAdapter.setFoodList(allFoods);
    }

    private void observeCart() {
        CartManager.getInstance().getTotalCountLive().observe(this, count -> {
            if (count != null && count > 0) {
                tvCartCount.setVisibility(View.VISIBLE);
                tvCartCount.setText(String.valueOf(count));
                btnSettle.setEnabled(true);
                btnSettle.setAlpha(1.0f);
            } else {
                tvCartCount.setVisibility(View.GONE);
                btnSettle.setEnabled(false);
                btnSettle.setAlpha(0.5f);
            }
        });

        CartManager.getInstance().getTotalPriceLive().observe(this, price -> {
            if (price != null) {
                tvCartTotal.setText(PriceCalculator.formatPrice(price));
            }
        });
    }

    private int dp2px(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
