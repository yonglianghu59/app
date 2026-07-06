package com.example.project_new_take_out.adapter;

import android.content.Context;
import java.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.utils.PriceCalculator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 订单列表 Adapter
 * 展示订单卡片，包含店铺信息、商品摘要、时间、状态、金额、操作按钮
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private final Context context;
    private List<Order> orderList = new ArrayList<>();
    private OnOrderActionListener onOrderActionListener;

    public interface OnOrderActionListener {
        void onReorder(Order order);
        void onReview(Order order);
        void onShopClick(Order order);
        void onCardClick(Order order);
    }

    public OrderListAdapter(Context context) {
        this.context = context;
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.onOrderActionListener = listener;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList != null ? orderList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivShopIcon;
        TextView tvShopName, tvOrderStatus, tvItemsSummary, tvOrderTime, tvOrderAmount;
        TextView btnAction1, btnAction2;
        LinearLayout layoutActions;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivShopIcon = itemView.findViewById(R.id.iv_shop_icon);
            tvShopName = itemView.findViewById(R.id.tv_shop_name);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvItemsSummary = itemView.findViewById(R.id.tv_items_summary);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvOrderAmount = itemView.findViewById(R.id.tv_order_amount);
            btnAction1 = itemView.findViewById(R.id.btn_action_1);
            btnAction2 = itemView.findViewById(R.id.btn_action_2);
            layoutActions = itemView.findViewById(R.id.layout_actions);
        }

        void bind(Order order) {
            // 店铺名称
            tvShopName.setText(order.getShopName());

            // 店铺图标（使用本地图片）
            ivShopIcon.setImageResource(com.example.project_new_take_out.utils.ImageUtils.getShopDrawable(order.getShopName()));

            // 商品摘要
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                CartItem firstItem = order.getItems().get(0);
                int totalCount = 0;
                for (CartItem item : order.getItems()) {
                    totalCount += item.getQuantity();
                }
                String summary = context.getString(R.string.order_summary_format,
                        firstItem.getFoodName(), totalCount);
                tvItemsSummary.setText(summary);
            }

            // 下单时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            tvOrderTime.setText(sdf.format(new Date(order.getCreateTime())));

            // 订单金额
            tvOrderAmount.setText(PriceCalculator.formatPrice(order.getActualAmount()));

            // 订单状态及对应颜色
            String status = order.getStatus();
            int statusColor;
            String statusText;

            switch (status) {
                case "pending":
                    statusColor = context.getResources().getColor(R.color.statusPending);
                    statusText = context.getString(R.string.order_status_pending);
                    break;
                case "paid":
                case "delivering":
                    statusColor = context.getResources().getColor(R.color.statusDelivering);
                    statusText = context.getString(R.string.order_status_delivering);
                    break;
                case "completed":
                    statusColor = context.getResources().getColor(R.color.statusCompleted);
                    statusText = context.getString(R.string.order_status_completed);
                    break;
                case "cancelled":
                    statusColor = context.getResources().getColor(R.color.statusCancelled);
                    statusText = context.getString(R.string.order_status_cancelled);
                    break;
                default:
                    statusColor = context.getResources().getColor(R.color.textSecondary);
                    statusText = status;
                    break;
            }
            tvOrderStatus.setText(statusText);
            tvOrderStatus.setTextColor(statusColor);

            // 操作按钮
            btnAction1.setText(R.string.btn_reorder);
            btnAction1.setVisibility(View.VISIBLE);
            btnAction1.setOnClickListener(v -> {
                if (onOrderActionListener != null) {
                    onOrderActionListener.onReorder(order);
                }
            });

            // 已完成订单显示删除按钮
            if ("completed".equals(status)) {
                btnAction2.setText("删除");
                btnAction2.setVisibility(View.VISIBLE);
                btnAction2.setOnClickListener(v -> {
                    if (onOrderActionListener != null) {
                        onOrderActionListener.onReview(order); // 复用review回调，实际做删除
                    }
                });
            } else {
                btnAction2.setVisibility(View.GONE);
            }

            // 店铺名称点击
            tvShopName.setOnClickListener(v -> {
                if (onOrderActionListener != null) {
                    onOrderActionListener.onShopClick(order);
                }
            });

            // 整个卡片点击 → 订单详情
            itemView.setOnClickListener(v -> {
                if (onOrderActionListener != null) {
                    onOrderActionListener.onCardClick(order);
                }
            });
        }
    }
}
