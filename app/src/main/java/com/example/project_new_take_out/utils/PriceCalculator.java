package com.example.project_new_take_out.utils;

import com.example.project_new_take_out.model.CartItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 金额计算工具类
 * 使用 BigDecimal 保证金额计算精度，避免浮点数精度丢失
 */
public class PriceCalculator {

    /**
     * 计算商品总价
     */
    public static double calculateGoodsTotal(List<CartItem> items) {
        if (items == null || items.isEmpty()) return 0.0;
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            BigDecimal price = BigDecimal.valueOf(item.getActualPrice());
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            total = total.add(price.multiply(quantity));
        }
        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算单品小计
     */
    public static double calculateItemSubtotal(CartItem item) {
        BigDecimal price = BigDecimal.valueOf(item.getActualPrice());
        BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
        return price.multiply(quantity).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 计算实付金额：商品总价 + 配送费 - 优惠抵扣
     */
    public static double calculateActualPay(double goodsTotal, double deliveryFee, double discount) {
        BigDecimal total = BigDecimal.valueOf(goodsTotal);
        BigDecimal fee = BigDecimal.valueOf(deliveryFee);
        BigDecimal dis = BigDecimal.valueOf(discount);
        return total.add(fee).subtract(dis).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 格式化金额为显示字符串
     */
    public static String formatPrice(double price) {
        return String.format("¥%.2f", price);
    }

    /**
     * 格式化金额（不带符号）
     */
    public static String formatPricePlain(double price) {
        return String.format("%.2f", price);
    }
}
