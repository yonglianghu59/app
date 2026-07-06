package com.example.project_new_take_out.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品列表 Adapter（店铺详情页右侧菜品列表）
 */
public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {

    private final Context context;
    private List<Food> foodList = new ArrayList<>();
    private int shopId;
    private String shopName;
    private OnFoodClickListener onFoodClickListener;

    public interface OnFoodClickListener {
        void onFoodClick(Food food, int position);
    }

    public FoodListAdapter(Context context) {
        this.context = context;
    }

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.onFoodClickListener = listener;
    }

    public void setShopInfo(int shopId, String shopName) {
        this.shopId = shopId;
        this.shopName = shopName;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList != null ? foodList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food);

        // 菜品条目点击 → 跳转菜品详情页
        holder.itemView.setOnClickListener(v -> {
            if (onFoodClickListener != null) {
                onFoodClickListener.onFoodClick(food, position);
            }
        });

        // "+" 按钮 → 加入购物车
        holder.ivAdd.setOnClickListener(v -> {
            addToCart(food);
            holder.ivAdd.setEnabled(false);
            holder.ivAdd.postDelayed(() -> holder.ivAdd.setEnabled(true), 300);
        });
    }

    /**
     * 将菜品添加至购物车
     */
    private void addToCart(Food food) {
        CartItem item = new CartItem(
                food.getId(),
                shopId,
                food.getName(),
                food.getImageUrl(),
                food.getPrice()
        );
        // 默认选中份和辣度（如果有规格选项）
        if (food.getSizes() != null && !food.getSizes().isEmpty()) {
            Food.Option defaultSize = food.getSizes().get(1 >= food.getSizes().size() ? 0 : 1);
            item.setSelectedSize(defaultSize.getName());
            item.setSizePriceOffset(defaultSize.getPriceOffset());
        }
        if (food.getSpicyLevels() != null && !food.getSpicyLevels().isEmpty()) {
            Food.Option defaultSpicy = food.getSpicyLevels().get(1 >= food.getSpicyLevels().size() ? 0 : 1);
            item.setSelectedSpicy(defaultSpicy.getName());
            item.setSpicyPriceOffset(defaultSpicy.getPriceOffset());
        }
        item.generateCartKey();

        CartManager.getInstance().addToCart(item);
        ToastUtils.showShort(context, food.getName() + " " + context.getString(R.string.add_success));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName;
        TextView tvMonthlySales;
        TextView tvPrice;
        ImageView ivAdd;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.iv_food_image);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvMonthlySales = itemView.findViewById(R.id.tv_monthly_sales);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivAdd = itemView.findViewById(R.id.iv_add);
        }

        void bind(Food food) {
            tvFoodName.setText(food.getName());
            tvMonthlySales.setText("月售" + food.getMonthlySales());
            tvPrice.setText("¥" + (int) food.getPrice());

            Glide.with(context)
                    .load(com.example.project_new_take_out.utils.ImageUtils.getFoodDrawable(food.getName()))
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(6))))
                    .placeholder(R.drawable.food_kungpao)
                    .error(R.drawable.food_kungpao)
                    .into(ivFoodImage);
        }

        private int dp2px(int dp) {
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5f);
        }
    }
}
