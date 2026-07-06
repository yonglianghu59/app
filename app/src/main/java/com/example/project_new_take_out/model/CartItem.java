package com.example.project_new_take_out.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 购物车条目实体类
 * 表示购物车中的单个商品及其规格选择
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // 唯一标识（用于购物车增删改查）
    private String cartKey;

    private int foodId;
    private int shopId;
    private String foodName;
    private String imageUrl;
    private double basePrice;
    private int quantity;
    private String selectedSize;
    private double sizePriceOffset;
    private String selectedSpicy;
    private double spicyPriceOffset;
    private long addTime;

    // 构造函数
    public CartItem() {}

    public CartItem(int foodId, int shopId, String foodName, String imageUrl, double basePrice) {
        this.foodId = foodId;
        this.shopId = shopId;
        this.foodName = foodName;
        this.imageUrl = imageUrl;
        this.basePrice = basePrice;
        this.quantity = 1;
        this.selectedSize = "";
        this.sizePriceOffset = 0;
        this.selectedSpicy = "";
        this.spicyPriceOffset = 0;
        this.addTime = System.currentTimeMillis();
        generateCartKey();
    }

    /**
     * 生成购物车唯一标识：foodId + 规格 + 辣度
     */
    public void generateCartKey() {
        this.cartKey = foodId + "_" + selectedSize + "_" + selectedSpicy;
    }

    /**
     * 获取单品实际价格（基础价 + 规格加价 + 辣度加价）
     */
    public double getActualPrice() {
        return basePrice + sizePriceOffset + spicyPriceOffset;
    }

    /**
     * 获取单品小计
     */
    public double getSubtotal() {
        return getActualPrice() * quantity;
    }

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
    public void setQuantity(int quantity) { this.quantity = Math.max(1, quantity); }

    public String getSelectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
        generateCartKey();
    }

    public double getSizePriceOffset() { return sizePriceOffset; }
    public void setSizePriceOffset(double sizePriceOffset) { this.sizePriceOffset = sizePriceOffset; }

    public String getSelectedSpicy() { return selectedSpicy; }
    public void setSelectedSpicy(String selectedSpicy) {
        this.selectedSpicy = selectedSpicy;
        generateCartKey();
    }

    public double getSpicyPriceOffset() { return spicyPriceOffset; }
    public void setSpicyPriceOffset(double spicyPriceOffset) { this.spicyPriceOffset = spicyPriceOffset; }

    public long getAddTime() { return addTime; }
    public void setAddTime(long addTime) { this.addTime = addTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(cartKey, cartItem.cartKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartKey);
    }
}
