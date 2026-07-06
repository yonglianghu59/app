package com.example.project_new_take_out.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_new_take_out.db.entity.CartItemEntity;

import java.util.List;

/**
 * 购物车 DAO 接口
 * Room 编译时自动生成实现类
 */
@Dao
public interface CartDao {

    /**
     * 查询所有购物车条目（返回 LiveData，支持数据驱动 UI）
     */
    @Query("SELECT * FROM cart_items ORDER BY add_time DESC")
    LiveData<List<CartItemEntity>> getAllCartItemsLive();

    /**
     * 查询所有购物车条目（同步）
     */
    @Query("SELECT * FROM cart_items ORDER BY add_time DESC")
    List<CartItemEntity> getAllCartItemsSync();

    /**
     * 根据 key 查询单个条目
     */
    @Query("SELECT * FROM cart_items WHERE cart_key = :cartKey LIMIT 1")
    CartItemEntity getByKey(String cartKey);

    /**
     * 插入或替换商品（已存在则更新数量）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(CartItemEntity item);

    /**
     * 批量插入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartItemEntity> items);

    /**
     * 更新条目
     */
    @Update
    void update(CartItemEntity item);

    /**
     * 删除条目
     */
    @Delete
    void delete(CartItemEntity item);

    /**
     * 根据 key 删除
     */
    @Query("DELETE FROM cart_items WHERE cart_key = :cartKey")
    void deleteByKey(String cartKey);

    /**
     * 清空购物车
     */
    @Query("DELETE FROM cart_items")
    void clearAll();

    /**
     * 更新商品数量
     */
    @Query("UPDATE cart_items SET quantity = :quantity WHERE cart_key = :cartKey")
    void updateQuantity(String cartKey, int quantity);

    /**
     * 获取购物车商品总数
     */
    @Query("SELECT SUM(quantity) FROM cart_items")
    LiveData<Integer> getTotalCountLive();

    /**
     * 获取购物车商品总数（同步）
     */
    @Query("SELECT SUM(quantity) FROM cart_items")
    Integer getTotalCountSync();

    /**
     * 获取购物车总金额（同步）
     */
    @Query("SELECT SUM((base_price + size_price_offset + spicy_price_offset) * quantity) FROM cart_items")
    Double getTotalPriceSync();

    /**
     * 根据店铺 ID 查询商品
     */
    @Query("SELECT * FROM cart_items WHERE shop_id = :shopId ORDER BY add_time DESC")
    List<CartItemEntity> getByShopId(int shopId);
}
