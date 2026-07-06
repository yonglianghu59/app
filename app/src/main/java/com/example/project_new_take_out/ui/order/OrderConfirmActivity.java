package com.example.project_new_take_out.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.adapter.OrderItemAdapter;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.model.Address;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.ui.pay.PayActivity;
import com.example.project_new_take_out.utils.PriceCalculator;
import com.example.project_new_take_out.viewmodel.OrderViewModel;

import java.util.List;

/**
 * 确认订单页
 * 展示收货地址、商品清单、费用明细
 */
public class OrderConfirmActivity extends BaseActivity {

    private OrderViewModel viewModel;

    private TextView tvAddressFull, tvContactName, tvContactPhone;
    private TextView tvShopName;
    private RecyclerView recyclerOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private TextView tvGoodsTotal, tvDeliveryFee, tvDiscount, tvActualPay;
    private TextView tvBottomActualPay, btnGoPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        int shopId = getIntent().getIntExtra("shop_id", 1);
        String shopName = getIntent().getStringExtra("shop_name");
        if (shopName == null) shopName = "店铺";

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        initViews();
        observeViewModel();

        viewModel.loadOrderData(shopId, shopName, 3.0);
    }

    private void initViews() {
        tvAddressFull = findViewById(R.id.tv_address_full);
        tvContactName = findViewById(R.id.tv_contact_name);
        tvContactPhone = findViewById(R.id.tv_contact_phone);
        tvShopName = findViewById(R.id.tv_shop_name);
        recyclerOrderItems = findViewById(R.id.recycler_order_items);
        tvGoodsTotal = findViewById(R.id.tv_goods_total);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvDiscount = findViewById(R.id.tv_discount);
        tvActualPay = findViewById(R.id.tv_actual_pay);
        tvBottomActualPay = findViewById(R.id.tv_bottom_actual_pay);
        btnGoPay = findViewById(R.id.btn_go_pay);

        // 订单商品列表
        orderItemAdapter = new OrderItemAdapter(this);
        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrderItems.setAdapter(orderItemAdapter);

        // 返回键
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 去支付
        btnGoPay.setOnClickListener(v -> {
            viewModel.submitOrder();
        });
    }

    private void observeViewModel() {
        // 收货地址
        viewModel.getAddressLiveData().observe(this, address -> {
            if (address != null) {
                updateAddressUI(address);
            }
        });

        // 店铺名
        viewModel.getShopNameLiveData().observe(this, name -> {
            tvShopName.setText(name);
        });

        // 商品清单
        viewModel.getOrderItemsLiveData().observe(this, items -> {
            if (items != null) {
                orderItemAdapter.setOrderItemList(items);
            }
        });

        // 费用明细
        viewModel.getGoodsTotalLiveData().observe(this, total -> {
            tvGoodsTotal.setText(PriceCalculator.formatPrice(total != null ? total : 0));
        });

        viewModel.getDeliveryFeeLiveData().observe(this, fee -> {
            tvDeliveryFee.setText(PriceCalculator.formatPrice(fee != null ? fee : 0));
        });

        viewModel.getDiscountLiveData().observe(this, discount -> {
            double d = discount != null ? discount : 0;
            tvDiscount.setText("-" + PriceCalculator.formatPrice(d));
        });

        viewModel.getActualPayLiveData().observe(this, actual -> {
            String text = PriceCalculator.formatPrice(actual != null ? actual : 0);
            tvActualPay.setText(text);
            tvBottomActualPay.setText(text);
        });

        // 订单提交成功 → 保存订单 + 跳转支付页
        viewModel.getOrderLiveData().observe(this, order -> {
            if (order != null) {
                // 保存订单到 SharedPreferences（支付后出现在订单列表）
                saveOrderToHistory(order);
                Intent intent = new Intent(OrderConfirmActivity.this, PayActivity.class);
                intent.putExtra("order_id", order.getOrderId());
                intent.putExtra("order_amount", order.getActualAmount());
                intent.putExtra("shop_name", order.getShopName());
                startActivity(intent);
            }
        });
    }

    private void updateAddressUI(Address address) {
        tvAddressFull.setText(address.getFullAddress());
        tvContactName.setText(address.getContactName());
        tvContactPhone.setText(address.getHiddenPhone());
    }

    /**
     * 保存新订单到用户订单历史（OrderFragment 加载时读取）
     */
    private void saveOrderToHistory(Order order) {
        String userId = com.example.project_new_take_out.utils.UserManager.getInstance().getUserId();
        android.content.SharedPreferences prefs = getSharedPreferences("user_orders", MODE_PRIVATE);
        String saved = prefs.getString(userId, null);

        java.util.List<Order> orders;
        com.google.gson.Gson gson = new com.google.gson.Gson();
        com.google.gson.reflect.TypeToken<java.util.List<Order>> token =
                new com.google.gson.reflect.TypeToken<java.util.List<Order>>() {};

        if (saved != null) {
            try {
                orders = gson.fromJson(saved, token.getType());
            } catch (Exception e) {
                orders = new java.util.ArrayList<>();
            }
        } else {
            orders = new java.util.ArrayList<>();
        }
        if (orders == null) orders = new java.util.ArrayList<>();

        // 新订单插入到列表最前面
        orders.add(0, order);
        prefs.edit().putString(userId, gson.toJson(orders)).apply();
    }
}
