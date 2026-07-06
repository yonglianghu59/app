package com.example.project_new_take_out.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.project_new_take_out.app.BaseActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.ui.review.ReviewActivity;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 订单详情页
 */
public class OrderDetailActivity extends BaseActivity {

    private TextView tvOrderStatus, tvOrderId, tvAddressInfo;
    private TextView tvGoodsTotal, tvDeliveryFee, tvDiscount, tvActualAmount;
    private TextView btnReorder, btnReview;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvAddressInfo = findViewById(R.id.tv_address_info);
        tvGoodsTotal = findViewById(R.id.tv_goods_total);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvDiscount = findViewById(R.id.tv_discount);
        tvActualAmount = findViewById(R.id.tv_actual_amount);
        btnReorder = findViewById(R.id.btn_reorder);
        btnReview = findViewById(R.id.btn_review);

        RecyclerView recyclerItems = findViewById(R.id.recycler_order_items);
        recyclerItems.setLayoutManager(new LinearLayoutManager(this));

        // 接收订单数据
        order = (Order) getIntent().getSerializableExtra("order");
        if (order == null) {
            // 用 bundle 重建
            order = rebuildOrder();
        }

        if (order != null) {
            updateUI();
            if (order.getItems() != null) {
                recyclerItems.setAdapter(new OrderItemAdapter(order.getItems()));
            }
        }

        setupButtons();
    }

    private Order rebuildOrder() {
        Intent i = getIntent();
        Order o = new Order();
        o.setOrderId(i.getStringExtra("order_id"));
        o.setShopName(i.getStringExtra("shop_name"));
        o.setShopId(i.getIntExtra("shop_id", 0));
        o.setStatus(i.getStringExtra("status"));
        o.setGoodsTotal(i.getDoubleExtra("goods_total", 0));
        o.setDeliveryFee(i.getDoubleExtra("delivery_fee", 0));
        o.setDiscount(i.getDoubleExtra("discount", 0));
        o.setActualAmount(i.getDoubleExtra("actual_amount", 0));
        o.setCreateTime(i.getLongExtra("create_time", System.currentTimeMillis()));
        return o;
    }

    private void updateUI() {
        // 状态
        String status = order.getStatus();
        String statusText;
        int statusColor;
        switch (status != null ? status : "") {
            case "pending":
                statusText = "待付款"; statusColor = getColor(R.color.statusPending); break;
            case "delivering":
                statusText = "配送中"; statusColor = getColor(R.color.statusDelivering); break;
            case "completed":
                statusText = "已完成"; statusColor = getColor(R.color.statusCompleted); break;
            case "cancelled":
                statusText = "已取消"; statusColor = getColor(R.color.statusCancelled); break;
            default:
                statusText = status; statusColor = getColor(R.color.textSecondary); break;
        }
        tvOrderStatus.setText(statusText);
        tvOrderStatus.setTextColor(statusColor);

        tvOrderId.setText("订单编号：" + order.getOrderId());
        tvAddressInfo.setText("四川省成都市武侯区天府大道中段688号天府软件园");

        tvGoodsTotal.setText(PriceCalculator.formatPrice(order.getGoodsTotal()));
        tvDeliveryFee.setText(PriceCalculator.formatPrice(order.getDeliveryFee()));
        tvDiscount.setText("-" + PriceCalculator.formatPrice(order.getDiscount()));
        tvActualAmount.setText(PriceCalculator.formatPrice(order.getActualAmount()));
    }

    private void setupButtons() {
        String status = order.getStatus();

        if ("pending".equals(status)) {
            // 待付款 → 去支付 + 取消订单
            btnReorder.setText("去支付");
            btnReorder.setVisibility(View.VISIBLE);
            btnReorder.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.project_new_take_out.ui.pay.PayActivity.class);
                intent.putExtra("order_id", order.getOrderId());
                intent.putExtra("order_amount", order.getActualAmount());
                startActivity(intent);
            });

            btnReview.setText("取消订单");
            btnReview.setVisibility(View.VISIBLE);
            btnReview.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("取消订单")
                        .setMessage("确定取消订单「" + order.getOrderId() + "」吗？")
                        .setNegativeButton("再想想", null)
                        .setPositiveButton("确定取消", (d, w) -> {
                            order.setStatus("cancelled");
                            saveOrderStatus();
                            Toast.makeText(this, "订单已取消", Toast.LENGTH_SHORT).show();
                            updateUI();
                            setupButtons();
                        })
                        .show();
            });

        } else if ("delivering".equals(status)) {
            // 配送中 → 确认收货
            btnReorder.setText("确认收货");
            btnReorder.setVisibility(View.VISIBLE);
            btnReorder.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("确认收货")
                        .setMessage("确认已收到「" + order.getShopName() + "」的商品？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认收货", (d, w) -> {
                            order.setStatus("completed");
                            saveOrderStatus();
                            Toast.makeText(this, "已确认收货", Toast.LENGTH_SHORT).show();
                            updateUI();
                            setupButtons();
                        })
                        .show();
            });
            btnReview.setVisibility(View.GONE);

        } else if ("completed".equals(status)) {
            // 已完成 → 再来一单 + 评价 + 申请退款
            btnReorder.setText("再来一单");
            btnReorder.setVisibility(View.VISIBLE);
            btnReorder.setOnClickListener(v -> {
                if (order.getItems() != null) {
                    for (CartItem item : order.getItems()) {
                        CartItem newItem = new CartItem(item.getFoodId(), item.getShopId(),
                                item.getFoodName(), item.getImageUrl(), item.getBasePrice());
                        newItem.setSelectedSize(item.getSelectedSize());
                        newItem.setSizePriceOffset(item.getSizePriceOffset());
                        newItem.setSelectedSpicy(item.getSelectedSpicy());
                        newItem.setSpicyPriceOffset(item.getSpicyPriceOffset());
                        newItem.setQuantity(1);
                        newItem.generateCartKey();
                        CartManager.getInstance().addToCart(newItem);
                    }
                }
                Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
                finish();
            });

            boolean reviewed = com.example.project_new_take_out.ui.review.ReviewActivity
                    .isOrderReviewed(this, order.getOrderId());
            boolean within7Days = (System.currentTimeMillis() - order.getCreateTime()) < 7 * 24 * 3600 * 1000L;

            if (!reviewed) {
                // 未评价 → 显示"评价"
                btnReview.setText("评价");
                btnReview.setVisibility(View.VISIBLE);
                btnReview.setOnClickListener(v -> {
                    Intent intent = new Intent(this, com.example.project_new_take_out.ui.review.ReviewActivity.class);
                    intent.putExtra("order_id", order.getOrderId());
                    intent.putExtra("shop_name", order.getShopName());
                    startActivity(intent);
                });
            } else if (within7Days) {
                // 已评价且7天内 → 显示"申请退款"
                btnReview.setText("申请退款");
                btnReview.setVisibility(View.VISIBLE);
                btnReview.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle("申请退款")
                            .setMessage("确定对订单「" + order.getOrderId() + "」申请退款？\n退款金额：" + order.getActualAmount() + "元\n退款将在1-3个工作日到账。")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确认退款", (d, w) -> {
                                order.setStatus("refunding");
                                saveOrderStatus();
                                Toast.makeText(this, "退款申请已提交", Toast.LENGTH_SHORT).show();
                                updateUI();
                                setupButtons();
                            })
                            .show();
                });
            } else {
                btnReview.setVisibility(View.GONE);
            }

        } else if ("cancelled".equals(status)) {
            // 已取消 → 再来一单
            btnReorder.setText("再来一单");
            btnReorder.setVisibility(View.VISIBLE);
            btnReorder.setOnClickListener(v -> {
                if (order.getItems() != null) {
                    for (CartItem item : order.getItems()) {
                        CartItem newItem = new CartItem(item.getFoodId(), item.getShopId(),
                                item.getFoodName(), item.getImageUrl(), item.getBasePrice());
                        newItem.setQuantity(1);
                        newItem.generateCartKey();
                        CartManager.getInstance().addToCart(newItem);
                    }
                }
                Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show();
                finish();
            });
            btnReview.setVisibility(View.GONE);
        }
    }

    /** 订单商品列表适配器 */
    static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.VH> {
        private final List<CartItem> items;
        OrderItemAdapter(List<CartItem> items) { this.items = items != null ? items : new ArrayList<>(); }

        @Override public VH onCreateViewHolder(ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_order_item, p, false));
        }
        @Override public void onBindViewHolder(VH h, int pos) {
            CartItem item = items.get(pos);
            h.tvName.setText(item.getFoodName());
            h.tvSpec.setText((item.getSelectedSize() != null ? item.getSelectedSize() : "")
                    + (item.getSelectedSpicy() != null ? " / " + item.getSelectedSpicy() : ""));
            h.tvPrice.setText(PriceCalculator.formatPrice(item.getActualPrice()));
            h.tvQty.setText("x" + item.getQuantity());
            h.tvSubtotal.setText(PriceCalculator.formatPrice(item.getSubtotal()));
        }
        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvSpec, tvPrice, tvQty, tvSubtotal;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_food_name);
                tvSpec = v.findViewById(R.id.tv_spec);
                tvPrice = v.findViewById(R.id.tv_price);
                tvQty = v.findViewById(R.id.tv_quantity);
                tvSubtotal = v.findViewById(R.id.tv_subtotal);
            }
        }
    }

    /**
     * 持久化订单状态变更（取消/确认收货/支付后更新）
     */
    private void saveOrderStatus() {
        if (order == null || order.getOrderId() == null) return;
        android.content.SharedPreferences prefs = getSharedPreferences("order_status", MODE_PRIVATE);
        prefs.edit().putString(order.getOrderId(), order.getStatus()).apply();
    }

    /**
     * 供外部查询订单状态（OrderFragment 等使用）
     */
    public static String getSavedOrderStatus(android.content.Context ctx, String orderId) {
        return ctx.getSharedPreferences("order_status", android.content.Context.MODE_PRIVATE)
                .getString(orderId, null);
    }
}
