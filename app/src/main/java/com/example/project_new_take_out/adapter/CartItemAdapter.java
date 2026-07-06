package com.example.project_new_take_out.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车条目 Adapter（购物车弹窗中使用）
 */
public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private final Context context;
    private List<CartItem> cartItemList = new ArrayList<>();
    private OnCartChangeListener onCartChangeListener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartItemAdapter(Context context) {
        this.context = context;
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.onCartChangeListener = listener;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList != null ? cartItemList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<CartItem> getCartItemList() {
        return new ArrayList<>(cartItemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.bind(item);

        // 增加数量
        holder.ivIncrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            CartManager.getInstance().updateQuantity(item.getCartKey(), newQty);
            notifyItemChanged(position);
            notifyCartChanged();
        });

        // 减少数量
        holder.ivDecrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            if (newQty <= 0) {
                // 数量为 0 时移除
                CartManager.getInstance().removeFromCart(item.getCartKey());
                cartItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItemList.size());
            } else {
                item.setQuantity(newQty);
                CartManager.getInstance().updateQuantity(item.getCartKey(), newQty);
                notifyItemChanged(position);
            }
            notifyCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    private void notifyCartChanged() {
        if (onCartChangeListener != null) {
            onCartChangeListener.onCartChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName;
        TextView tvSpec;
        TextView tvPrice;
        TextView tvQuantity;
        ImageView ivDecrease;
        ImageView ivIncrease;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvSpec = itemView.findViewById(R.id.tv_spec);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            ivDecrease = itemView.findViewById(R.id.iv_decrease);
            ivIncrease = itemView.findViewById(R.id.iv_increase);
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
            tvQuantity.setText(String.valueOf(item.getQuantity()));
        }
    }
}
