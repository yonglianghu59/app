package com.example.project_new_take_out.utils;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.db.entity.CartItemEntity;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 全局购物车管理器（单例模式）
 * 负责跨页面购物车数据同步，封装对 Room 数据库的增删改查操作
 * 确保整个 APP 中购物车数据的一致性
 */
public class CartManager {

    private static volatile CartManager instance;
    private CartRepository cartRepository;

    // 购物车总价 LiveData（跨页面观察）
    private final MutableLiveData<Double> totalPriceLiveData = new MutableLiveData<>(0.0);

    // 购物车总数量 LiveData（跨页面观察）
    private final MutableLiveData<Integer> totalCountLiveData = new MutableLiveData<>(0);

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) {
            synchronized (CartManager.class) {
                if (instance == null) {
                    instance = new CartManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化（Application 中调用）
     * 数据库操作在后台线程执行，避免 Room 主线程限制导致崩溃
     */
    public void init(Context context) {
        cartRepository = CartRepository.getInstance(context);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            refreshCartData();
            executor.shutdown();
        });
    }

    /**
     * 添加商品到购物车
     */
    public void addToCart(CartItem item) {
        if (cartRepository == null) return;
        cartRepository.insertOrUpdate(CartRepository.cartItemToEntity(item));
        refreshCartData();
    }

    /**
     * 更新商品数量
     */
    public void updateQuantity(String cartKey, int quantity) {
        if (cartRepository == null) return;
        cartRepository.updateQuantity(cartKey, quantity);
        refreshCartData();
    }

    /**
     * 从购物车移除商品
     */
    public void removeFromCart(String cartKey) {
        if (cartRepository == null) return;
        cartRepository.deleteByKey(cartKey);
        refreshCartData();
    }

    /**
     * 清空购物车
     */
    public void clearCart() {
        if (cartRepository == null) return;
        cartRepository.clearAll();
        refreshCartData();
    }

    /**
     * 获取所有购物车条目（同步）
     */
    public List<CartItem> getAllItems() {
        if (cartRepository == null) return new ArrayList<>();
        return CartRepository.entitiesToCartItems(cartRepository.getAllCartItemsSync());
    }

    /**
     * 获取购物车总金额 LiveData
     */
    public LiveData<Double> getTotalPriceLive() {
        return totalPriceLiveData;
    }

    /**
     * 获取购物车总数量 LiveData
     */
    public LiveData<Integer> getTotalCountLive() {
        return totalCountLiveData;
    }

    /**
     * 获取购物车总金额（同步）
     */
    public double getTotalPrice() {
        if (cartRepository == null) return 0.0;
        return cartRepository.getTotalPriceSync();
    }

    /**
     * 获取购物车总数量（同步）
     */
    public int getTotalCount() {
        if (cartRepository == null) return 0;
        List<CartItem> items = getAllItems();
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    /**
     * 检查购物车是否为空
     */
    public boolean isEmpty() {
        return getTotalCount() == 0;
    }

    /**
     * 刷新购物车数据，更新 LiveData
     */
    private void refreshCartData() {
        if (cartRepository == null) return;
        totalPriceLiveData.postValue(cartRepository.getTotalPriceSync());

        List<CartItem> items = getAllItems();
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        totalCountLiveData.postValue(count);
    }
}
