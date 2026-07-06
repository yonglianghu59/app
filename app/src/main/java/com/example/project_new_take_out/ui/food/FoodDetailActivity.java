package com.example.project_new_take_out.ui.food;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;
import com.example.project_new_take_out.utils.ToastUtils;
import com.example.project_new_take_out.viewmodel.FoodViewModel;

import java.util.List;

/**
 * 菜品详情页
 * 展示菜品信息，支持规格/辣度选择，加入购物车
 */
public class FoodDetailActivity extends BaseActivity {

    private FoodViewModel viewModel;

    private ImageView ivFoodImage;
    private TextView tvFoodName, tvFoodPrice, tvFoodSales, tvFoodDesc, tvFoodIngredients;
    private LinearLayout layoutSizeOptions, layoutSpicyOptions;
    private TextView btnAddToCart;

    private int foodId;
    private int shopId;
    private Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        foodId = getIntent().getIntExtra("food_id", 101);
        shopId = getIntent().getIntExtra("shop_id", 1);

        viewModel = new ViewModelProvider(this).get(FoodViewModel.class);

        initViews();
        observeViewModel();

        viewModel.loadFoodDetail(foodId);
    }

    private void initViews() {
        ivFoodImage = findViewById(R.id.iv_food_image);
        tvFoodName = findViewById(R.id.tv_food_name);
        tvFoodPrice = findViewById(R.id.tv_food_price);
        tvFoodSales = findViewById(R.id.tv_food_sales);
        tvFoodDesc = findViewById(R.id.tv_food_desc);
        tvFoodIngredients = findViewById(R.id.tv_food_ingredients);
        layoutSizeOptions = findViewById(R.id.layout_size_options);
        layoutSpicyOptions = findViewById(R.id.layout_spicy_options);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void observeViewModel() {
        viewModel.getFoodDetailLiveData().observe(this, food -> {
            if (food != null) {
                currentFood = food;
                updateFoodUI(food);
                buildSizeOptions(food.getSizes());
                buildSpicyOptions(food.getSpicyLevels());
            }
        });

        viewModel.getCurrentPriceLiveData().observe(this, price -> {
            if (price != null) {
                tvFoodPrice.setText(PriceCalculator.formatPrice(price));
            }
        });

        viewModel.getSelectedSizeIndex().observe(this, index -> {
            if (index != null) {
                highlightOption(layoutSizeOptions, index, viewModel.getSelectedSpicyIndex().getValue());
            }
        });

        viewModel.getSelectedSpicyIndex().observe(this, index -> {
            if (index != null) {
                highlightOption(layoutSpicyOptions, index, viewModel.getSelectedSizeIndex().getValue());
            }
        });
    }

    private void updateFoodUI(Food food) {
        tvFoodName.setText(food.getName());
        tvFoodPrice.setText(PriceCalculator.formatPrice(food.getPrice()));
        tvFoodSales.setText("月售 " + food.getMonthlySales());
        tvFoodDesc.setText(food.getDescription());

        String ingredients = "主要食材：" + food.getIngredients();
        tvFoodIngredients.setText(ingredients);

        Glide.with(this)
                .load(com.example.project_new_take_out.utils.ImageUtils.getFoodDrawable(food.getName()))
                .placeholder(R.drawable.food_kungpao)
                .error(R.drawable.food_kungpao)
                .into(ivFoodImage);
    }

    /**
     * 构建规格选择按钮
     */
    private void buildSizeOptions(List<Food.Option> options) {
        layoutSizeOptions.removeAllViews();
        if (options == null || options.isEmpty()) return;

        for (int i = 0; i < options.size(); i++) {
            Food.Option option = options.get(i);
            TextView chip = createOptionChip(option.getName(), i, true);
            layoutSizeOptions.addView(chip);
        }

        Integer selectedIndex = viewModel.getSelectedSizeIndex().getValue();
        highlightOption(layoutSizeOptions, selectedIndex != null ? selectedIndex : 1,
                viewModel.getSelectedSpicyIndex().getValue());
    }

    /**
     * 构建辣度选择按钮
     */
    private void buildSpicyOptions(List<Food.Option> options) {
        layoutSpicyOptions.removeAllViews();
        if (options == null || options.isEmpty()) return;

        for (int i = 0; i < options.size(); i++) {
            Food.Option option = options.get(i);
            TextView chip = createOptionChip(option.getName(), i, false);
            layoutSpicyOptions.addView(chip);
        }

        Integer selectedIndex = viewModel.getSelectedSpicyIndex().getValue();
        highlightOption(layoutSpicyOptions, selectedIndex != null ? selectedIndex : 1,
                viewModel.getSelectedSizeIndex().getValue());
    }

    private TextView createOptionChip(String name, int index, boolean isSize) {
        TextView chip = new TextView(this);
        chip.setText(name);
        chip.setTextSize(13);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp2px(16), dp2px(6), dp2px(16), dp2px(6));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, dp2px(12), 0);
        chip.setLayoutParams(params);

        chip.setOnClickListener(v -> {
            if (isSize) {
                viewModel.selectSize(index);
            } else {
                viewModel.selectSpicy(index);
            }
        });

        return chip;
    }

    private void highlightOption(LinearLayout container, int selectedIndex, Integer otherIndex) {
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (i == selectedIndex) {
                child.setBackgroundResource(R.drawable.selector_option_chip);
                child.setSelected(true);
                ((TextView) child).setTextColor(getColor(R.color.white));
            } else {
                child.setBackgroundResource(R.drawable.selector_option_chip);
                child.setSelected(false);
                ((TextView) child).setTextColor(getColor(R.color.textSecondary));
            }
        }
    }

    /**
     * 加入购物车
     */
    private void addToCart() {
        if (currentFood == null) return;

        CartItem item = new CartItem(
                currentFood.getId(),
                shopId,
                currentFood.getName(),
                currentFood.getImageUrl(),
                currentFood.getPrice()
        );

        // 获取选中的规格
        Integer sizeIdx = viewModel.getSelectedSizeIndex().getValue();
        if (sizeIdx != null && currentFood.getSizes() != null
                && sizeIdx < currentFood.getSizes().size()) {
            Food.Option size = currentFood.getSizes().get(sizeIdx);
            item.setSelectedSize(size.getName());
            item.setSizePriceOffset(size.getPriceOffset());
        }

        // 获取选中的辣度
        Integer spicyIdx = viewModel.getSelectedSpicyIndex().getValue();
        if (spicyIdx != null && currentFood.getSpicyLevels() != null
                && spicyIdx < currentFood.getSpicyLevels().size()) {
            Food.Option spicy = currentFood.getSpicyLevels().get(spicyIdx);
            item.setSelectedSpicy(spicy.getName());
            item.setSpicyPriceOffset(spicy.getPriceOffset());
        }

        item.generateCartKey();
        CartManager.getInstance().addToCart(item);
        ToastUtils.showShort(this, currentFood.getName() + " " + getString(R.string.add_success));
        finish();
    }

    private int dp2px(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
