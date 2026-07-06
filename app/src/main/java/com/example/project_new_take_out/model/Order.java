package com.example.project_new_take_out.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * 订单实体类
 */
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("shop_id")
    private int shopId;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("items")
    private List<CartItem> items;

    @SerializedName("goods_total")
    private double goodsTotal;

    @SerializedName("delivery_fee")
    private double deliveryFee;

    @SerializedName("discount")
    private double discount;

    @SerializedName("actual_amount")
    private double actualAmount;

    @SerializedName("status")
    private String status; // pending, paid, cancelled

    @SerializedName("create_time")
    private long createTime;

    @SerializedName("pay_method")
    private String payMethod;

    // 构造函数
    public Order() {
        this.createTime = System.currentTimeMillis();
        this.status = "pending";
    }

    /**
     * 生成唯一订单号
     */
    public static String generateOrderId() {
        return "DD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    // Getter & Setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getGoodsTotal() { return goodsTotal; }
    public void setGoodsTotal(double goodsTotal) { this.goodsTotal = goodsTotal; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getActualAmount() { return actualAmount; }
    public void setActualAmount(double actualAmount) { this.actualAmount = actualAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", actualAmount=" + actualAmount +
                ", status='" + status + '\'' +
                '}';
    }
}
