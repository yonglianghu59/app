package com.example.project_new_take_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Food;
import com.example.project_new_take_out.repository.FoodRepository;

/**
 * 菜品详情页 ViewModel
 * 管理菜品信息、规格/辣度选择状态、实时价格计算
 */
public class FoodViewModel extends AndroidViewModel {

    private final FoodRepository foodRepository;

    private final MutableLiveData<Food> foodDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    // 选中的规格和辣度
    private final MutableLiveData<Integer> selectedSizeIndex = new MutableLiveData<>(1); // 默认中份
    private final MutableLiveData<Integer> selectedSpicyIndex = new MutableLiveData<>(1); // 默认微辣

    // 当前实时价格
    private final MutableLiveData<Double> currentPriceLiveData = new MutableLiveData<>(0.0);

    public FoodViewModel(@NonNull Application application) {
        super(application);
        foodRepository = FoodRepository.getInstance();
    }

    /**
     * 加载菜品详情
     */
    public void loadFoodDetail(int foodId) {
        isLoadingLiveData.setValue(true);
        LiveData<Food> result = foodRepository.getFoodDetail(foodId);
        result.observeForever(food -> {
            isLoadingLiveData.setValue(false);
            if (food != null) {
                foodDetailLiveData.setValue(food);
                // 设置默认价格
                updateCurrentPrice();
            } else {
                errorMessageLiveData.setValue("菜品不存在");
            }
        });
    }

    /**
     * 选择规格
     */
    public void selectSize(int index) {
        selectedSizeIndex.setValue(index);
        updateCurrentPrice();
    }

    /**
     * 选择辣度
     */
    public void selectSpicy(int index) {
        selectedSpicyIndex.setValue(index);
        updateCurrentPrice();
    }

    /**
     * 计算当前实时价格 = 基础价 + 规格加价 + 辣度加价
     */
    private void updateCurrentPrice() {
        Food food = foodDetailLiveData.getValue();
        if (food == null) return;

        double price = food.getPrice();
        Integer sizeIdx = selectedSizeIndex.getValue();
        Integer spicyIdx = selectedSpicyIndex.getValue();

        if (sizeIdx != null && food.getSizes() != null && sizeIdx < food.getSizes().size()) {
            price += food.getSizes().get(sizeIdx).getPriceOffset();
        }
        if (spicyIdx != null && food.getSpicyLevels() != null && spicyIdx < food.getSpicyLevels().size()) {
            price += food.getSpicyLevels().get(spicyIdx).getPriceOffset();
        }
        currentPriceLiveData.setValue(price);
    }

    // ========== Getter ==========
    public LiveData<Food> getFoodDetailLiveData() { return foodDetailLiveData; }
    public LiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public LiveData<String> getErrorMessageLiveData() { return errorMessageLiveData; }
    public LiveData<Integer> getSelectedSizeIndex() { return selectedSizeIndex; }
    public LiveData<Integer> getSelectedSpicyIndex() { return selectedSpicyIndex; }
    public LiveData<Double> getCurrentPriceLiveData() { return currentPriceLiveData; }
}
