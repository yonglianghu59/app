package com.example.project_new_take_out.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.utils.PriceCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单商品清单 Adapter（确认订单页使用，只读展示）
 */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final Context context;
    private List<CartItem> orderItemList = new ArrayList<>();

    public OrderItemAdapter(Context context) {
        this.context = context;
    }

    public void setOrderItemList(List<CartItem> orderItemList) {
        this.orderItemList = orderItemList != null ? orderItemList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = orderItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName;
        TextView tvSpec;
        TextView tvPrice;
        TextView tvQuantity;
        TextView tvSubtotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvSpec = itemView.findViewById(R.id.tv_spec);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
        }

        void bind(CartItem item) {
            tvFoodName.setText(item.getFoodName());

            // 规格描述
            StringBuilder spec = new StringBuilder();
            if (item.getSelectedSize() != null && !item.getSelectedSize().isEmpty()) {
                spec.append(item.getSelectedSize());
            }
            if (item.getSelectedSpicy() != null && !item.getSelectedSpicy().isEmpty()) {
                if (spec.length() > 0) spec.append(" / ");
                spec.append(item.getSelectedSpicy());
            }
            tvSpec.setText(spec.toString());
            tvSpec.setVisibility(spec.length() > 0 ? View.VISIBLE : View.GONE);

            tvPrice.setText(PriceCalculator.formatPrice(item.getActualPrice()));
            tvQuantity.setText("x" + item.getQuantity());
            tvSubtotal.setText(PriceCalculator.formatPrice(PriceCalculator.calculateItemSubtotal(item)));
        }
    }
}
