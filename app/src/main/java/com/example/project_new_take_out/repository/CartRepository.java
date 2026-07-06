package com.example.project_new_take_out.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.project_new_take_out.db.AppDatabase;
import com.example.project_new_take_out.db.dao.CartDao;
import com.example.project_new_take_out.db.entity.CartItemEntity;
import com.example.project_new_take_out.model.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 购物车数据仓库
 * 封装 Room 数据库操作，提供同步/异步接口
 */
public class CartRepository {

    private final CartDao cartDao;
    private final ExecutorService executor;
    private static volatile CartRepository instance;

    private CartRepository(Context context) {
        cartDao = AppDatabase.getInstance(context).cartDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public static CartRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (CartRepository.class) {
                if (instance == null) {
                    instance = new CartRepository(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取所有购物车条目（LiveData）
     */
    public LiveData<List<CartItemEntity>> getAllCartItemsLive() {
        return cartDao.getAllCartItemsLive();
    }

    /**
     * 获取所有购物车条目（同步）
     */
    public List<CartItemEntity> getAllCartItemsSync() {
        return cartDao.getAllCartItemsSync();
    }

    /**
     * 获取商品总数（LiveData）
     */
    public LiveData<Integer> getTotalCountLive() {
        return cartDao.getTotalCountLive();
    }

    /**
     * 获取购物车总金额（同步）
     */
    public Double getTotalPriceSync() {
        Double price = cartDao.getTotalPriceSync();
        return price != null ? price : 0.0;
    }

    /**
     * 添加或更新商品（同步，因 allowMainThreadQueries 已开启）
     */
    public void insertOrUpdate(CartItemEntity item) {
        CartItemEntity existing = cartDao.getByKey(item.getCartKey());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
            cartDao.update(existing);
        } else {
            cartDao.insertOrUpdate(item);
        }
    }

    /**
     * 更新商品数量（同步）
     */
    public void updateQuantity(String cartKey, int quantity) {
        cartDao.updateQuantity(cartKey, quantity);
    }

    /**
     * 删除商品（同步）
     */
    public void deleteByKey(String cartKey) {
        cartDao.deleteByKey(cartKey);
    }

    /**
     * 清空购物车（异步）
     */
    public void clearAll() {
        executor.execute(cartDao::clearAll);
    }

    /**
     * CartItem 转 CartItemEntity
     */
    public static CartItemEntity cartItemToEntity(CartItem item) {
        CartItemEntity entity = new CartItemEntity();
        entity.setCartKey(item.getCartKey());
        entity.setFoodId(item.getFoodId());
        entity.setShopId(item.getShopId());
        entity.setFoodName(item.getFoodName());
        entity.setImageUrl(item.getImageUrl());
        entity.setBasePrice(item.getBasePrice());
        entity.setQuantity(item.getQuantity());
        entity.setSelectedSize(item.getSelectedSize());
        entity.setSizePriceOffset(item.getSizePriceOffset());
        entity.setSelectedSpicy(item.getSelectedSpicy());
        entity.setSpicyPriceOffset(item.getSpicyPriceOffset());
        entity.setAddTime(item.getAddTime());
        return entity;
    }

    /**
     * CartItemEntity 转 CartItem
     */
    public static CartItem entityToCartItem(CartItemEntity entity) {
        CartItem item = new CartItem();
        item.setCartKey(entity.getCartKey());
        item.setFoodId(entity.getFoodId());
        item.setShopId(entity.getShopId());
        item.setFoodName(entity.getFoodName());
        item.setImageUrl(entity.getImageUrl());
        item.setBasePrice(entity.getBasePrice());
        item.setQuantity(entity.getQuantity());
        item.setSelectedSize(entity.getSelectedSize());
        item.setSizePriceOffset(entity.getSizePriceOffset());
        item.setSelectedSpicy(entity.getSelectedSpicy());
        item.setSpicyPriceOffset(entity.getSpicyPriceOffset());
        item.setAddTime(entity.getAddTime());
        return item;
    }

    /**
     * 批量转换
     */
    public static List<CartItem> entitiesToCartItems(List<CartItemEntity> entities) {
        List<CartItem> items = new ArrayList<>();
        if (entities != null) {
            for (CartItemEntity entity : entities) {
                items.add(entityToCartItem(entity));
            }
        }
        return items;
    }
}
