package com.example.project_new_take_out.ui.cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.ui.order.OrderConfirmActivity;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 购物车 Fragment
 * 商品列表 + 推荐区域 + 底部结算栏 + 空状态
 */
public class CartFragment extends Fragment {

    // UI 组件
    private RecyclerView recyclerCartItems, recyclerRecommend;
    private LinearLayout layoutEmpty, layoutRecommend, layoutDeliveryHint;
    private TextView tvClearAll, tvTotalAmount, tvGoSettle, tvGoShop;
    private ImageView ivSelectAll;
    private View layoutSelectAll;

    // Adapter
    private CartFullAdapter cartAdapter;
    private RecommendAdapter recommendAdapter;

    // 选中状态
    private final Set<String> selectedKeys = new HashSet<>();

    // 推荐商品模拟数据
    private final List<Food> recommendFoods = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        initViews(view);
        initRecyclerViews();
        initClickListeners();
        initRecommendData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCartData();
    }

    private void initViews(View view) {
        recyclerCartItems = view.findViewById(R.id.recycler_cart_items);
        recyclerRecommend = view.findViewById(R.id.recycler_recommend);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutRecommend = view.findViewById(R.id.layout_recommend);
        layoutDeliveryHint = view.findViewById(R.id.layout_delivery_hint);
        tvClearAll = view.findViewById(R.id.tv_clear_all);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        tvGoSettle = view.findViewById(R.id.tv_go_settle);
        tvGoShop = view.findViewById(R.id.tv_go_shop);
        ivSelectAll = view.findViewById(R.id.iv_select_all);
        layoutSelectAll = view.findViewById(R.id.layout_select_all);
    }

    private void initRecyclerViews() {
        // 购物车商品列表
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartAdapter = new CartFullAdapter(requireContext(), selectedKeys);
        cartAdapter.setOnCartChangeListener(new CartFullAdapter.OnCartChangeListener() {
            @Override
            public void onCartChanged() {
                refreshCartData();
            }

            @Override
            public void onSelectionChanged() {
                updateSelectionUI();
            }
        });
        recyclerCartItems.setAdapter(cartAdapter);

        // 推荐商品列表（横向滚动）
        recommendAdapter = new RecommendAdapter(requireContext());
        recommendAdapter.setOnAddClickListener(food -> {
            // 加入购物车
            CartItem item = new CartItem(food.getId(), food.getShopId(),
                    food.getName(), food.getImageUrl(), food.getPrice());
            CartManager.getInstance().addToCart(item);
            Toast.makeText(requireContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
            refreshCartData();
        });
        recyclerRecommend.setAdapter(recommendAdapter);
    }

    private void initClickListeners() {
        // 清空购物车
        tvClearAll.setOnClickListener(v -> {
            if (CartManager.getInstance().isEmpty()) {
                Toast.makeText(requireContext(), R.string.cart_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(requireContext())
                    .setMessage(R.string.cart_clear_confirm)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        CartManager.getInstance().clearCart();
                        selectedKeys.clear();
                        refreshCartData();
                    })
                    .show();
        });

        // 全选/反选
        layoutSelectAll.setOnClickListener(v -> {
            List<CartItem> items = CartManager.getInstance().getAllItems();
            if (selectedKeys.size() == items.size() && !items.isEmpty()) {
                // 全部取消
                selectedKeys.clear();
            } else {
                // 全选
                for (CartItem item : items) {
                    selectedKeys.add(item.getCartKey());
                }
            }
            updateSelectionUI();
        });

        // 去结算
        tvGoSettle.setOnClickListener(v -> {
            if (selectedKeys.isEmpty()) {
                Toast.makeText(requireContext(), "请选择要结算的商品", Toast.LENGTH_SHORT).show();
                return;
            }
            // 收集选中的商品
            List<CartItem> selectedItems = new ArrayList<>();
            List<CartItem> allItems = CartManager.getInstance().getAllItems();
            for (CartItem item : allItems) {
                if (selectedKeys.contains(item.getCartKey())) {
                    selectedItems.add(item);
                }
            }

            if (!selectedItems.isEmpty()) {
                // 跳转确认订单页
                Intent intent = new Intent(requireContext(), OrderConfirmActivity.class);
                intent.putExtra("shop_id", selectedItems.get(0).getShopId());
                intent.putExtra("shop_name", "购物车结算");
                startActivity(intent);
            }
        });

        // 空状态 - 去逛逛 → 切换到首页
        tvGoShop.setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof com.example.project_new_take_out.ui.main.MainActivity) {
                com.google.android.material.bottomnavigation.BottomNavigationView nav =
                        getActivity().findViewById(R.id.bottom_navigation);
                if (nav != null) nav.setSelectedItemId(R.id.nav_home);
            }
        });
    }

    private void refreshCartData() {
        List<CartItem> items = CartManager.getInstance().getAllItems();
        cartAdapter.setCartItemList(new ArrayList<>(items));

        // 清除无效的选中 key
        Set<String> validKeys = new HashSet<>();
        for (CartItem item : items) {
            validKeys.add(item.getCartKey());
        }
        selectedKeys.retainAll(validKeys);

        updateSelectionUI();

        // 空状态切换
        if (items.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerCartItems.setVisibility(View.GONE);
            layoutRecommend.setVisibility(View.GONE);
            layoutDeliveryHint.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerCartItems.setVisibility(View.VISIBLE);
            layoutRecommend.setVisibility(View.VISIBLE);
            layoutDeliveryHint.setVisibility(View.VISIBLE);
        }
    }

    private void updateSelectionUI() {
        List<CartItem> items = CartManager.getInstance().getAllItems();
        boolean allSelected = !items.isEmpty() && selectedKeys.size() == items.size();
        ivSelectAll.setImageResource(allSelected ? R.drawable.ic_checkbox_checked : R.drawable.ic_checkbox_unchecked);

        // 计算选中商品合计金额
        double total = 0;
        for (CartItem item : items) {
            if (selectedKeys.contains(item.getCartKey())) {
                total += item.getSubtotal();
            }
        }
        tvTotalAmount.setText(PriceCalculator.formatPrice(total));

        // 去结算按钮状态
        if (selectedKeys.isEmpty()) {
            tvGoSettle.setBackgroundResource(R.drawable.bg_button_disabled);
            tvGoSettle.setClickable(false);
        } else {
            tvGoSettle.setBackgroundResource(R.drawable.selector_orange_button);
            tvGoSettle.setClickable(true);
        }
    }

    private void initRecommendData() {
        recommendFoods.clear();

        Food food1 = new Food(401, 1, 1, "红烧牛肉面", "", 22.00, 520, "经典川味红烧牛肉面");
        Food food2 = new Food(402, 1, 2, "珍珠奶茶", "", 15.00, 890, "香浓奶茶配Q弹珍珠");
        Food food3 = new Food(403, 2, 1, "炸鸡翅(6块)", "", 18.00, 340, "外酥里嫩炸鸡翅");
        Food food4 = new Food(404, 3, 1, "鳗鱼饭", "", 48.00, 230, "日式蒲烧鳗鱼饭");

        recommendFoods.add(food1);
        recommendFoods.add(food2);
        recommendFoods.add(food3);
        recommendFoods.add(food4);

        recommendAdapter.setFoodList(recommendFoods);
    }

    // ========== 购物车完整条目 Adapter ==========

    private static class CartFullAdapter extends RecyclerView.Adapter<CartFullAdapter.ViewHolder> {

        private final Context context;
        private List<CartItem> cartItemList = new ArrayList<>();
        private final Set<String> selectedKeys;
        private OnCartChangeListener onCartChangeListener;

        interface OnCartChangeListener {
            void onCartChanged();
            void onSelectionChanged();
        }

        CartFullAdapter(Context context, Set<String> selectedKeys) {
            this.context = context;
            this.selectedKeys = selectedKeys;
        }

        void setOnCartChangeListener(OnCartChangeListener listener) {
            this.onCartChangeListener = listener;
        }

        void setCartItemList(List<CartItem> cartItemList) {
            this.cartItemList = cartItemList != null ? cartItemList : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_cart_full, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartItem item = cartItemList.get(position);
            holder.bind(item, position);
        }

        @Override
        public int getItemCount() {
            return cartItemList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivCheckbox, ivFoodImage, ivDecrease, ivIncrease, ivDelete;
            TextView tvFoodName, tvSpec, tvPrice, tvQuantity, tvSubtotal;
            int currentPosition;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivCheckbox = itemView.findViewById(R.id.iv_checkbox);
                ivFoodImage = itemView.findViewById(R.id.iv_food_image);
                tvFoodName = itemView.findViewById(R.id.tv_food_name);
                tvSpec = itemView.findViewById(R.id.tv_spec);
                tvPrice = itemView.findViewById(R.id.tv_price);
                ivDecrease = itemView.findViewById(R.id.iv_decrease);
                tvQuantity = itemView.findViewById(R.id.tv_quantity);
                ivIncrease = itemView.findViewById(R.id.iv_increase);
                tvSubtotal = itemView.findViewById(R.id.tv_subtotal);
                ivDelete = itemView.findViewById(R.id.iv_delete);
            }

            void bind(CartItem item, int position) {
                currentPosition = position;
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
                tvSubtotal.setText(PriceCalculator.formatPrice(item.getSubtotal()));

                // 加载商品图片
                Glide.with(context)
                        .load(com.example.project_new_take_out.utils.ImageUtils.getFoodDrawable(item.getFoodName()))
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(6))))
                        .placeholder(R.drawable.food_kungpao)
                        .into(ivFoodImage);

                // 复选框状态
                ivCheckbox.setImageResource(
                        selectedKeys.contains(item.getCartKey())
                                ? R.drawable.ic_checkbox_checked
                                : R.drawable.ic_checkbox_unchecked
                );
                ivCheckbox.setOnClickListener(v -> {
                    if (selectedKeys.contains(item.getCartKey())) {
                        selectedKeys.remove(item.getCartKey());
                    } else {
                        selectedKeys.add(item.getCartKey());
                    }
                    notifyItemChanged(currentPosition);
                    if (onCartChangeListener != null) {
                        onCartChangeListener.onSelectionChanged();
                    }
                });

                // 数量增减
                ivDecrease.setOnClickListener(v -> {
                    int newQty = item.getQuantity() - 1;
                    if (newQty <= 0) {
                        new AlertDialog.Builder(context)
                                .setMessage(R.string.cart_delete_confirm)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                    CartManager.getInstance().removeFromCart(item.getCartKey());
                                    selectedKeys.remove(item.getCartKey());
                                    notifyItemRemoved(currentPosition);
                                    cartItemList.remove(currentPosition);
                                    notifyCartChanged();
                                })
                                .show();
                    } else {
                        item.setQuantity(newQty);
                        CartManager.getInstance().updateQuantity(item.getCartKey(), newQty);
                        notifyItemChanged(currentPosition);
                        notifyCartChanged();
                    }
                });

                ivIncrease.setOnClickListener(v -> {
                    int newQty = item.getQuantity() + 1;
                    item.setQuantity(newQty);
                    CartManager.getInstance().updateQuantity(item.getCartKey(), newQty);
                    notifyItemChanged(currentPosition);
                    notifyCartChanged();
                });

                // 删除
                ivDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setMessage(R.string.cart_delete_confirm)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                                CartManager.getInstance().removeFromCart(item.getCartKey());
                                selectedKeys.remove(item.getCartKey());
                                cartItemList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyCartChanged();
                            })
                            .show();
                });
            }

            private void notifyCartChanged() {
                if (onCartChangeListener != null) {
                    onCartChangeListener.onCartChanged();
                }
            }

            private int dp2px(int dp) {
                float density = context.getResources().getDisplayMetrics().density;
                return (int) (dp * density + 0.5f);
            }
        }
    }

    // ========== 推荐商品 Adapter ==========

    private static class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.ViewHolder> {

        private final Context context;
        private List<Food> foodList = new ArrayList<>();
        private OnAddClickListener onAddClickListener;

        interface OnAddClickListener {
            void onAddClick(Food food);
        }

        void setOnAddClickListener(OnAddClickListener listener) {
            this.onAddClickListener = listener;
        }

        RecommendAdapter(Context context) {
            this.context = context;
        }

        void setFoodList(List<Food> foodList) {
            this.foodList = foodList != null ? foodList : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recommend_food, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Food food = foodList.get(position);
            holder.bind(food);
        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFoodImage, ivAdd;
            TextView tvFoodName, tvFoodPrice;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivFoodImage = itemView.findViewById(R.id.iv_food_image);
                tvFoodName = itemView.findViewById(R.id.tv_food_name);
                tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
                ivAdd = itemView.findViewById(R.id.iv_add);
            }

            void bind(Food food) {
                tvFoodName.setText(food.getName());
                tvFoodPrice.setText(PriceCalculator.formatPrice(food.getPrice()));

                Glide.with(context)
                        .load(com.example.project_new_take_out.utils.ImageUtils.getFoodDrawable(food.getName()))
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(dp2px(8))))
                        .placeholder(R.drawable.food_kungpao)
                        .into(ivFoodImage);

                ivAdd.setOnClickListener(v -> {
                    if (onAddClickListener != null) {
                        onAddClickListener.onAddClick(food);
                    }
                });
            }

            private int dp2px(int dp) {
                float density = context.getResources().getDisplayMetrics().density;
                return (int) (dp * density + 0.5f);
            }
        }
    }
}
