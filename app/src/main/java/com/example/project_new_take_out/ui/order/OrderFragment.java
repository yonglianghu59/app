package com.example.project_new_take_out.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.adapter.OrderListAdapter;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.ui.main.MainActivity;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的订单 Fragment
 * 包含标签切换栏（全部/待付款/待收货/待评价）+ RecyclerView + 空状态
 */
public class OrderFragment extends Fragment {

    // 当前选中的标签
    private String currentFilter = "all";
    // 外部传入的初始筛选（在View创建后应用）
    private String pendingFilter = null;

    // UI 组件
    private TextView tabAll, tabPendingPay, tabPendingDelivery, tabPendingReview;
    private View viewTabIndicator;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerOrderList;
    private LinearLayout layoutEmpty;
    private OrderListAdapter orderListAdapter;

    // 数据（按用户隔离）
    private List<Order> allOrders = new ArrayList<>();
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        currentUserId = UserManager.getInstance().getUserId();
        initViews(view);
        initRecyclerView();
        initTabListeners();
        loadOrders(); // 按用户加载订单
        // 应用外部传入的筛选
        if (pendingFilter != null) {
            applyFilter(pendingFilter);
            pendingFilter = null;
        }
        filterOrders();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 从持久化存储同步订单状态（取消/确认收货/支付等）
        syncOrderStatuses();
        filterOrders();
    }

    /**
     * 同步订单状态：从 SharedPreferences 读取并应用已保存的状态
     */
    private void syncOrderStatuses() {
        if (getContext() == null) return;
        android.content.SharedPreferences statusPrefs = getContext()
                .getSharedPreferences("order_status", android.content.Context.MODE_PRIVATE);
        for (Order order : allOrders) {
            String savedStatus = statusPrefs.getString(order.getOrderId(), null);
            if (savedStatus != null) {
                order.setStatus(savedStatus);
            }
        }
    }

    private void initViews(View view) {
        tabAll = view.findViewById(R.id.tab_all);
        tabPendingPay = view.findViewById(R.id.tab_pending_pay);
        tabPendingDelivery = view.findViewById(R.id.tab_pending_delivery);
        tabPendingReview = view.findViewById(R.id.tab_pending_review);
        viewTabIndicator = view.findViewById(R.id.view_tab_indicator);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerOrderList = view.findViewById(R.id.recycler_order_list);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(() -> {
            filterOrders();
            swipeRefresh.setRefreshing(false);
        });

        // 默认选中"全部"
        updateTabSelection(tabAll);
    }

    private void initRecyclerView() {
        recyclerOrderList.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderListAdapter = new OrderListAdapter(requireContext());
        orderListAdapter.setOnOrderActionListener(new OrderListAdapter.OnOrderActionListener() {
            @Override
            public void onReorder(Order order) {
                // 将订单商品重新加入购物车
                if (order.getItems() != null) {
                    for (CartItem item : order.getItems()) {
                        CartItem newItem = new CartItem(
                                item.getFoodId(),
                                item.getShopId(),
                                item.getFoodName(),
                                item.getImageUrl(),
                                item.getBasePrice()
                        );
                        newItem.setSelectedSize(item.getSelectedSize());
                        newItem.setSizePriceOffset(item.getSizePriceOffset());
                        newItem.setSelectedSpicy(item.getSelectedSpicy());
                        newItem.setSpicyPriceOffset(item.getSpicyPriceOffset());
                        newItem.setQuantity(1);
                        newItem.generateCartKey();
                        CartManager.getInstance().addToCart(newItem);
                    }
                }
                Toast.makeText(requireContext(), "已加入购物车，请前往购物车结算", Toast.LENGTH_SHORT).show();
                // 跳转购物车Tab
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToCartTab();
                }
            }

            @Override
            public void onReview(Order order) {
                // 已完成订单 → 删除确认
                if ("completed".equals(order.getStatus())) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("删除订单")
                            .setMessage("确定删除订单「" + order.getOrderId() + "」？\n删除后不可恢复。")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("删除", (d, w) -> {
                                allOrders.remove(order);
                                saveOrders();
                                filterOrders();
                                Toast.makeText(requireContext(), "订单已删除", Toast.LENGTH_SHORT).show();
                            })
                            .show();
                } else {
                    // 其他状态 → 跳转评价页面
                    Intent intent = new Intent(requireContext(), com.example.project_new_take_out.ui.review.ReviewActivity.class);
                    intent.putExtra("order_id", order.getOrderId());
                    intent.putExtra("shop_name", order.getShopName());
                    startActivity(intent);
                }
            }

            @Override
            public void onShopClick(Order order) {
                Intent intent = new Intent(requireContext(), com.example.project_new_take_out.ui.shop.ShopDetailActivity.class);
                intent.putExtra("shop_id", order.getShopId());
                intent.putExtra("shop_name", order.getShopName());
                startActivity(intent);
            }

            @Override
            public void onCardClick(Order order) {
                Intent intent = new Intent(requireContext(), OrderDetailActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
            }
        });
        recyclerOrderList.setAdapter(orderListAdapter);
    }

    private void initTabListeners() {
        View.OnClickListener tabClickListener = v -> {
            int id = v.getId();
            if (id == R.id.tab_all) {
                currentFilter = "all";
            } else if (id == R.id.tab_pending_pay) {
                currentFilter = "pending";
            } else if (id == R.id.tab_pending_delivery) {
                currentFilter = "delivering";
            } else if (id == R.id.tab_pending_review) {
                currentFilter = "completed";
            }
            updateTabSelection(v);
            filterOrders();
            animateTabIndicator(v);
        };

        tabAll.setOnClickListener(tabClickListener);
        tabPendingPay.setOnClickListener(tabClickListener);
        tabPendingDelivery.setOnClickListener(tabClickListener);
        tabPendingReview.setOnClickListener(tabClickListener);
    }

    private void updateTabSelection(View selectedTab) {
        int selectedColor = getResources().getColor(R.color.colorPrimary);
        int normalColor = getResources().getColor(R.color.textSecondary);

        tabAll.setTextColor(selectedTab == tabAll ? selectedColor : normalColor);
        tabPendingPay.setTextColor(selectedTab == tabPendingPay ? selectedColor : normalColor);
        tabPendingDelivery.setTextColor(selectedTab == tabPendingDelivery ? selectedColor : normalColor);
        tabPendingReview.setTextColor(selectedTab == tabPendingReview ? selectedColor : normalColor);
    }

    private void animateTabIndicator(View selectedTab) {
        // 计算指示器偏移
        selectedTab.post(() -> {
            float targetX = selectedTab.getLeft() + (selectedTab.getWidth() - viewTabIndicator.getWidth()) / 2f;
            viewTabIndicator.animate().translationX(targetX).setDuration(200).start();
        });
    }

    private void filterOrders() {
        List<Order> filtered = new ArrayList<>();
        for (Order order : allOrders) {
            if ("all".equals(currentFilter)) {
                filtered.add(order);
            } else if (currentFilter.equals(order.getStatus())) {
                filtered.add(order);
            }
        }
        orderListAdapter.setOrderList(filtered);

        if (filtered.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerOrderList.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerOrderList.setVisibility(View.VISIBLE);
        }
    }

    private void initMockData() {
        allOrders.clear();

        // 订单 1：待付款
        Order order1 = new Order();
        order1.setOrderId("DD20260701001");
        order1.setShopName("川味居·麻辣烫");
        order1.setShopId(1);
        order1.setStatus("pending");
        order1.setGoodsTotal(58.00);
        order1.setDeliveryFee(3.00);
        order1.setDiscount(0.00);
        order1.setActualAmount(61.00);
        order1.setCreateTime(System.currentTimeMillis() - 3600 * 1000); // 1 小时前
        List<CartItem> items1 = new ArrayList<>();
        CartItem item1 = new CartItem(101, 1, "宫保鸡丁", "", 28.00);
        item1.setQuantity(1);
        item1.setSelectedSize("中份");
        CartItem item2 = new CartItem(102, 1, "麻辣香锅", "", 30.00);
        item2.setQuantity(1);
        item2.setSelectedSpicy("中辣");
        items1.add(item1);
        items1.add(item2);
        order1.setItems(items1);
        allOrders.add(order1);

        // 订单 2：配送中（待收货）
        Order order2 = new Order();
        order2.setOrderId("DD20260630002");
        order2.setShopName("麦乐送");
        order2.setShopId(2);
        order2.setStatus("delivering");
        order2.setGoodsTotal(35.50);
        order2.setDeliveryFee(2.00);
        order2.setDiscount(5.00);
        order2.setActualAmount(32.50);
        order2.setCreateTime(System.currentTimeMillis() - 6 * 3600 * 1000);
        List<CartItem> items2 = new ArrayList<>();
        CartItem item3 = new CartItem(201, 2, "巨无霸套餐", "", 35.50);
        item3.setQuantity(1);
        items2.add(item3);
        order2.setItems(items2);
        allOrders.add(order2);

        // 订单 3：已完成（待评价）
        Order order3 = new Order();
        order3.setOrderId("DD20260628003");
        order3.setShopName("鱼鲜生");
        order3.setShopId(3);
        order3.setStatus("completed");
        order3.setGoodsTotal(128.00);
        order3.setDeliveryFee(0.00);
        order3.setDiscount(10.00);
        order3.setActualAmount(118.00);
        order3.setCreateTime(System.currentTimeMillis() - 2 * 24 * 3600 * 1000L);
        List<CartItem> items3 = new ArrayList<>();
        CartItem item4 = new CartItem(301, 3, "三文鱼刺身", "", 68.00);
        item4.setQuantity(1);
        CartItem item5 = new CartItem(302, 3, "寿司拼盘", "", 60.00);
        item5.setQuantity(1);
        items3.add(item4);
        items3.add(item5);
        order3.setItems(items3);
        allOrders.add(order3);

        // 订单 4：已完成
        Order order4 = new Order();
        order4.setOrderId("DD20260625004");
        order4.setShopName("川味居·麻辣烫");
        order4.setShopId(1);
        order4.setStatus("completed");
        order4.setGoodsTotal(42.00);
        order4.setDeliveryFee(3.00);
        order4.setDiscount(0.00);
        order4.setActualAmount(45.00);
        order4.setCreateTime(System.currentTimeMillis() - 7 * 24 * 3600 * 1000L);
        List<CartItem> items4 = new ArrayList<>();
        CartItem item6 = new CartItem(103, 1, "水煮鱼", "", 42.00);
        item6.setQuantity(1);
        item6.setSelectedSize("大份");
        items4.add(item6);
        order4.setItems(items4);
        allOrders.add(order4);
    }

    /**
     * 由外部（如 ProfileFragment）设置初始筛选状态
     */
    public void setInitialFilter(String filter) {
        if (tabAll == null) {
            // View 尚未创建，暂存等 onCreateView 后再应用
            pendingFilter = filter;
            return;
        }
        applyFilter(filter);
    }

    /**
     * 按当前用户加载订单（首次使用从initMockData填充）
     */
    private void loadOrders() {
        if (currentUserId == null) currentUserId = UserManager.getInstance().getUserId();
        android.content.SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_orders", android.content.Context.MODE_PRIVATE);
        String saved = prefs.getString(currentUserId, null);

        if (saved != null) {
            // 已有该用户的订单数据，反序列化
            try {
                allOrders.clear();
                com.google.gson.Gson gson = new com.google.gson.Gson();
                com.google.gson.reflect.TypeToken<java.util.List<Order>> token =
                        new com.google.gson.reflect.TypeToken<java.util.List<Order>>() {};
                java.util.List<Order> loaded = gson.fromJson(saved, token.getType());
                if (loaded != null) allOrders.addAll(loaded);
            } catch (Exception e) {
                initMockData(); // 反序列化失败回退默认数据
            }
        } else {
            // 首次使用：填充默认订单并保存
            initMockData();
            saveOrders();
        }
    }

    /**
     * 持久化当前用户订单
     */
    private void saveOrders() {
        if (currentUserId == null) return;
        try {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            String json = gson.toJson(allOrders);
            requireContext().getSharedPreferences("user_orders", android.content.Context.MODE_PRIVATE)
                    .edit().putString(currentUserId, json).apply();
        } catch (Exception ignored) {}
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        View targetTab = null;
        switch (filter) {
            case "pending": targetTab = tabPendingPay; break;
            case "delivering": targetTab = tabPendingDelivery; break;
            case "completed": targetTab = tabPendingReview; break;
            default: targetTab = tabAll; break;
        }
        if (targetTab != null) {
            updateTabSelection(targetTab);
            animateTabIndicator(targetTab);
        }
        filterOrders();
    }
}
