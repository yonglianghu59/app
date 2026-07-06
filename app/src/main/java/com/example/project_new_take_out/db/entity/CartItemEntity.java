package com.example.project_new_take_out.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room 购物车条目实体
 * 对应本地 SQLite 表 cart_items
 */
@Entity(tableName = "cart_items")
public class CartItemEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "cart_key")
    private String cartKey = "";

    @ColumnInfo(name = "food_id")
    private int foodId;

    @ColumnInfo(name = "shop_id")
    private int shopId;

    @ColumnInfo(name = "food_name")
    private String foodName;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "base_price")
    private double basePrice;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "selected_size")
    private String selectedSize;

    @ColumnInfo(name = "size_price_offset")
    private double sizePriceOffset;

    @ColumnInfo(name = "selected_spicy")
    private String selectedSpicy;

    @ColumnInfo(name = "spicy_price_offset")
    private double spicyPriceOffset;

    @ColumnInfo(name = "add_time")
    private long addTime;

    // 构造函数
    public CartItemEntity() {}

    // Getter & Setter
    public String getCartKey() { return cartKey; }
    public void setCartKey(String cartKey) { this.cartKey = cartKey; }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public double getSizePriceOffset() { return sizePriceOffset; }
    public void setSizePriceOffset(double sizePriceOffset) { this.sizePriceOffset = sizePriceOffset; }

    public String getSelectedSpicy() { return selectedSpicy; }
    public void setSelectedSpicy(String selectedSpicy) { this.selectedSpicy = selectedSpicy; }

    public double getSpicyPriceOffset() { return spicyPriceOffset; }
    public void setSpicyPriceOffset(double spicyPriceOffset) { this.spicyPriceOffset = spicyPriceOffset; }

    public long getAddTime() { return addTime; }
    public void setAddTime(long addTime) { this.addTime = addTime; }
}
