package com.example.project_new_take_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Category;
import com.example.project_new_take_out.model.Shop;
import com.example.project_new_take_out.repository.FoodRepository;
import com.example.project_new_take_out.repository.ShopRepository;

import java.util.List;

/**
 * 店铺详情页 ViewModel
 * 管理店铺信息、菜品分类、双 RecyclerView 联动状态
 */
public class ShopViewModel extends AndroidViewModel {

    private final ShopRepository shopRepository;
    private final FoodRepository foodRepository;

    private final MutableLiveData<Shop> shopDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categoryListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> selectedCategoryPosition = new MutableLiveData<>(0);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private int shopId;

    public ShopViewModel(@NonNull Application application) {
        super(application);
        shopRepository = ShopRepository.getInstance();
        foodRepository = FoodRepository.getInstance();
    }

    /**
     * 加载店铺详情和菜品分类
     */
    public void loadShopData(int shopId) {
        this.shopId = shopId;
        isLoadingLiveData.setValue(true);

        // 加载店铺详情
        LiveData<Shop> shopResult = shopRepository.getShopDetail(shopId);
        shopResult.observeForever(shop -> {
            if (shop != null) {
                shopDetailLiveData.setValue(shop);
            }
        });

        // 加载菜品分类
        LiveData<List<Category>> categoryResult = foodRepository.getShopCategories(shopId);
        categoryResult.observeForever(categories -> {
            isLoadingLiveData.setValue(false);
            if (categories != null && !categories.isEmpty()) {
                categoryListLiveData.setValue(categories);
                selectedCategoryPosition.setValue(0);
            } else {
                errorMessageLiveData.setValue("暂无菜品数据");
            }
        });
    }

    /**
     * 更新选中的分类位置
     */
    public void setSelectedCategoryPosition(int position) {
        selectedCategoryPosition.setValue(position);
    }

    // ========== Getter ==========
    public LiveData<Shop> getShopDetailLiveData() { return shopDetailLiveData; }
    public LiveData<List<Category>> getCategoryListLiveData() { return categoryListLiveData; }
    public LiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public LiveData<Integer> getSelectedCategoryPosition() { return selectedCategoryPosition; }
    public LiveData<String> getErrorMessageLiveData() { return errorMessageLiveData; }
    public int getShopId() { return shopId; }
}
