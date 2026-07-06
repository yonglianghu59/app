package com.example.project_new_take_out.model;

/**
 * 支付信息实体类
 */
public class PaymentInfo {

    private String orderId;
    private double amount;
    private String payMethod; // wechat, alipay
    private long createTime;
    private long expireTime; // 15分钟后过期

    public PaymentInfo() {
        this.createTime = System.currentTimeMillis();
        this.expireTime = this.createTime + 15 * 60 * 1000; // 15分钟
        this.payMethod = "wechat"; // 默认微信支付
    }

    public PaymentInfo(String orderId, double amount) {
        this();
        this.orderId = orderId;
        this.amount = amount;
    }

    /**
     * 获取剩余秒数
     */
    public long getRemainingSeconds() {
        long remaining = (expireTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= expireTime;
    }

    /**
     * 获取过期时间格式化字符串
     */
    public String getExpireTimeFormatted() {
        long totalSeconds = getRemainingSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Getter & Setter
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPayMethod() { return payMethod; }
    public void setPayMethod(String payMethod) { this.payMethod = payMethod; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
}
