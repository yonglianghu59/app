package com.example.project_new_take_out.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.adapter.BannerAdapter;
import com.example.project_new_take_out.adapter.ShopListAdapter;
import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.ui.shop.ShopDetailActivity;
import com.example.project_new_take_out.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 首页 Fragment（完整功能版）
 * 地址栏 + 搜索 + Banner轮播(含指示器) + 分类入口 + 排序栏 + 店铺列表
 */
public class HomeFragment extends Fragment {

    private MainViewModel viewModel;

    // 地址栏
    private LinearLayout layoutLocation;
    private TextView tvLocationAddress;

    // 搜索
    private EditText etSearch;

    // Banner
    private ViewPager2 viewPagerBanner;
    private BannerAdapter bannerAdapter;
    private LinearLayout layoutBannerDots;
    private ImageView[] dots;
    private Handler bannerHandler;
    private Runnable bannerRunnable;

    // 分类
    private String currentCategory = null;

    // 排序
    private TextView sortDefault, sortDistance, sortScore, sortSales;
    private int currentSort = 0; // 0=综合, 1=距离, 2=评分, 3=销量

    // 筛选标签
    private LinearLayout layoutFilterTag;
    private TextView tvFilterTag, tvClearFilter;

    // 店铺列表
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerShopList;
    private View layoutLoading, layoutEmpty;
    private ShopListAdapter shopListAdapter;

    // 数据
    private List<Shop> allShops = new ArrayList<>();
    private List<Shop> filteredShops = new ArrayList<>();

    // Banner 数据（3种不同色调的渐变背景）
    private final int[] bannerImages = {
            R.drawable.bg_banner_weekend, R.drawable.bg_banner_newshop, R.drawable.bg_banner_flashsale
    };
    private final String[] bannerTitles = {"周末特惠", "新店开业", "限时抢购"};
    private final String[] bannerSubtitles = {"周末美食狂欢", "首单立减10元", "每日特价菜品"};
    private final String[] bannerDescs = {"精选美味 5 折起", "来尝鲜吧", "手慢无"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        initRecyclerView(view);
        initViewModel();
        loadData();
        updateAddressDisplay();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAddressDisplay();
    }

    /** 从 SharedPreferences 读取默认地址并更新显示 */
    private void updateAddressDisplay() {
        if (tvLocationAddress == null || getContext() == null) return;
        String userId = com.example.project_new_take_out.utils.UserManager.getInstance().getUserId();
        String saved = getContext().getSharedPreferences("default_address", android.content.Context.MODE_PRIVATE)
                .getString(userId, null);
        if (saved != null && !saved.isEmpty()) {
            tvLocationAddress.setText(saved);
        }
    }

    // ==================== 初始化 ====================

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerShopList = view.findViewById(R.id.recycler_shop_list);
        layoutLoading = view.findViewById(R.id.layout_loading);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(() -> {
            resetFilters();
            refreshDisplayList();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void initRecyclerView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerShopList.setLayoutManager(layoutManager);

        shopListAdapter = new ShopListAdapter(requireContext());
        shopListAdapter.setOnShopClickListener((shop, position) -> {
            Intent intent = new Intent(requireContext(), ShopDetailActivity.class);
            intent.putExtra("shop_id", shop.getId());
            intent.putExtra("shop_name", shop.getName());
            intent.putExtra("shop_image", shop.getImageUrl());
            startActivity(intent);
        });

        // 添加 Header（地址栏 + 搜索 + Banner + 指示器 + 分类 + 排序）
        View headerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.header_home, recyclerShopList, false);
        initHeaderViews(headerView);
        shopListAdapter.setHeaderView(headerView);
        recyclerShopList.setAdapter(shopListAdapter);
    }

    private void initHeaderViews(View headerView) {
        // ---- 地址栏 ----
        layoutLocation = headerView.findViewById(R.id.layout_location);
        tvLocationAddress = headerView.findViewById(R.id.tv_location_address);
        layoutLocation.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(),
                    com.example.project_new_take_out.ui.address.AddressManageActivity.class);
            startActivity(intent);
        });

        // ---- 搜索栏 ----
        etSearch = headerView.findViewById(R.id.et_search);
        // 点击搜索栏跳转搜索页面
        etSearch.setFocusable(false);
        etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(),
                    com.example.project_new_take_out.ui.search.SearchActivity.class);
            startActivity(intent);
        });

        // ---- Banner ----
        viewPagerBanner = headerView.findViewById(R.id.view_pager_banner);
        bannerAdapter = new BannerAdapter(requireContext());
        viewPagerBanner.setAdapter(bannerAdapter);
        viewPagerBanner.setCurrentItem(500, false);

        layoutBannerDots = headerView.findViewById(R.id.layout_banner_dots);
        setupBannerDots(headerView);

        bannerHandler = new Handler();
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (viewPagerBanner != null) {
                    viewPagerBanner.setCurrentItem(viewPagerBanner.getCurrentItem() + 1, true);
                    bannerHandler.postDelayed(this, 3000);
                }
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 3000);

        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position % bannerImages.length);
            }
        });

        // ---- 分类入口 ----
        initCategoryClick(headerView);

        // ---- 排序栏 ----
        initSortBar(headerView);

        // ---- 筛选标签 ----
        layoutFilterTag = headerView.findViewById(R.id.layout_filter_tag);
        tvFilterTag = headerView.findViewById(R.id.tv_filter_tag);
        tvClearFilter = headerView.findViewById(R.id.tv_clear_filter);
        tvClearFilter.setOnClickListener(v -> {
            currentCategory = null;
            layoutFilterTag.setVisibility(View.GONE);
            refreshDisplayList();
        });
    }

    // ==================== Banner 指示器 ====================

    private void setupBannerDots(View headerView) {
        layoutBannerDots.removeAllViews();
        dots = new ImageView[bannerImages.length];
        for (int i = 0; i < bannerImages.length; i++) {
            dots[i] = new ImageView(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp2px(8), dp2px(8));
            params.setMargins(dp2px(3), 0, dp2px(3), 0);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(i == 0
                    ? R.drawable.ic_checkbox_checked
                    : R.drawable.ic_checkbox_unchecked);
            layoutBannerDots.addView(dots[i]);
        }
    }

    private void updateDots(int current) {
        if (dots == null) return;
        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(i == current
                    ? R.drawable.ic_checkbox_checked
                    : R.drawable.ic_checkbox_unchecked);
        }
    }

    // ==================== 分类点击 ====================

    private void initCategoryClick(View headerView) {
        int[] categoryIds = {
                R.id.layout_category_food, R.id.layout_category_drink,
                R.id.layout_category_market, R.id.layout_category_flower,
                R.id.layout_category_medicine, R.id.layout_category_more
        };
        String[] categoryNames = {"美食", "饮品甜点", "超市便利", "鲜花绿植", "买药", "全部分类"};

        for (int i = 0; i < categoryIds.length; i++) {
            View categoryView = headerView.findViewById(categoryIds[i]);
            if (categoryView != null) {
                final String catName = categoryNames[i];
                categoryView.setOnClickListener(v -> {
                    if (catName.equals("全部分类")) {
                        Intent intent = new Intent(requireContext(),
                                com.example.project_new_take_out.ui.category.AllCategoriesActivity.class);
                        startActivity(intent);
                    } else {
                        currentCategory = catName;
                        String tag = getString(R.string.filter_category).replace("%1$s", catName);
                        tvFilterTag.setText(tag);
                        layoutFilterTag.setVisibility(View.VISIBLE);
                        refreshDisplayList();
                    }
                });
            }
        }
    }

    // ==================== 排序栏 ====================

    private void initSortBar(View headerView) {
        sortDefault = headerView.findViewById(R.id.sort_default);
        sortDistance = headerView.findViewById(R.id.sort_distance);
        sortScore = headerView.findViewById(R.id.sort_score);
        sortSales = headerView.findViewById(R.id.sort_sales);

        sortDefault.setOnClickListener(v -> { currentSort = 0; updateSortUI(); refreshDisplayList(); });
        sortDistance.setOnClickListener(v -> { currentSort = 1; updateSortUI(); refreshDisplayList(); });
        sortScore.setOnClickListener(v -> { currentSort = 2; updateSortUI(); refreshDisplayList(); });
        sortSales.setOnClickListener(v -> { currentSort = 3; updateSortUI(); refreshDisplayList(); });
    }

    private void updateSortUI() {
        int selectedColor = getResources().getColor(R.color.colorPrimary);
        int normalColor = getResources().getColor(R.color.textSecondary);
        sortDefault.setTextColor(currentSort == 0 ? selectedColor : normalColor);
        sortDistance.setTextColor(currentSort == 1 ? selectedColor : normalColor);
        sortScore.setTextColor(currentSort == 2 ? selectedColor : normalColor);
        sortSales.setTextColor(currentSort == 3 ? selectedColor : normalColor);
    }

    // ==================== 数据过滤与排序 ====================

    private void refreshDisplayList() {
        // 1. 复制全部数据
        filteredShops.clear();
        filteredShops.addAll(allShops);

        // 2. 排序
        sortShopList(filteredShops);

        // 3. 更新 UI
        shopListAdapter.setShopList(filteredShops);
        layoutEmpty.setVisibility(filteredShops.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void sortShopList(List<Shop> shops) {
        switch (currentSort) {
            case 1: // 距离最近
                Collections.sort(shops, Comparator.comparingDouble(Shop::getDistance));
                break;
            case 2: // 评分最高
                Collections.sort(shops, (a, b) -> Float.compare(b.getScore(), a.getScore()));
                break;
            case 3: // 销量优先
                Collections.sort(shops, (a, b) -> Integer.compare(b.getMonthlySales(), a.getMonthlySales()));
                break;
            case 0: // 综合（默认顺序，不动）
            default:
                break;
        }
    }

    private void resetFilters() {
        currentCategory = null;
        currentSort = 0;
        if (layoutFilterTag != null) layoutFilterTag.setVisibility(View.GONE);
        updateSortUI();
    }

    // ==================== ViewModel ====================

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getShopListLiveData().observe(getViewLifecycleOwner(), shops -> {
            if (shops != null) {
                allShops.clear();
                allShops.addAll(shops);
                refreshDisplayList();
            }
        });

        viewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            layoutLoading.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            if (Boolean.TRUE.equals(isLoading)) {
                layoutEmpty.setVisibility(View.GONE);
            }
        });
    }

    private void loadData() {
        viewModel.loadShopList();
    }

    // ==================== 工具 ====================

    private int dp2px(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    // ==================== 生命周期 ====================

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }
}
