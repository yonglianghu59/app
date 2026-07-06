package com.example.project_new_take_out.utils;

import com.example.project_new_take_out.R;

/**
 * 图片资源映射工具类
 * 按店铺/菜品类型映射到本地 drawable，避免所有店铺共用2张图的尴尬
 */
public class ImageUtils {

    // 每种菜系一个图标
    private static final int[] CUISINE_ICONS = {
        R.drawable.ic_food_chinese,    // 中餐 红
        R.drawable.ic_food_chinese,    // 川菜 红
        R.drawable.ic_food_japanese,   // 日料 绿
        R.drawable.ic_food_western,    // 西餐/快餐 蓝
        R.drawable.ic_food_drink,      // 饮品/甜品 粉
        R.drawable.ic_food_noodle,     // 面食 黄
    };

    /**
     * 根据店铺 ID 获取对应图标（使用不同颜色/形状区分）
     */
    public static int getShopDrawable(int shopId) {
        int idx = Math.abs(shopId - 1) % CUISINE_ICONS.length;
        return CUISINE_ICONS[idx];
    }

    /**
     * 根据店铺名称获取店铺图片
     */
    public static int getShopDrawable(String shopName) {
        if (shopName == null) return R.drawable.ic_food_chinese;
        String name = shopName;
        if (name.contains("日料") || name.contains("寿司") || name.contains("鱼")) return R.drawable.ic_food_japanese;
        if (name.contains("汉堡") || name.contains("鸡") || name.contains("披萨") || name.contains("意面") || name.contains("西")) return R.drawable.ic_food_western;
        if (name.contains("奶茶") || name.contains("烘焙") || name.contains("甜") || name.contains("饮")) return R.drawable.ic_food_drink;
        if (name.contains("面") || name.contains("拉面")) return R.drawable.ic_food_noodle;
        return R.drawable.ic_food_chinese;
    }

    /**
     * 根据菜品 ID 获取对应本地菜品图片
     */
    public static int getFoodDrawable(int foodId) {
        int idx = Math.abs(foodId) % CUISINE_ICONS.length;
        return CUISINE_ICONS[idx];
    }

    /**
     * 根据菜品名称获取对应本地菜品图片
     */
    public static int getFoodDrawable(String foodName) {
        if (foodName == null) return R.drawable.food_kungpao;
        String name = foodName;
        // 读取已有的 jpg 作为默认，不足时用矢量图
        if (name.contains("麻辣") || name.contains("烫") || name.contains("宫保") || name.contains("水煮"))
            return R.drawable.food_malatang;
        if (name.contains("饭") || name.contains("米") || name.contains("炒"))
            return R.drawable.food_rice;
        if (name.contains("汉堡") || name.contains("炸鸡") || name.contains("鸡翅") || name.contains("鸡"))
            return R.drawable.food_burger;
        if (name.contains("奶") || name.contains("茶") || name.contains("饮") || name.contains("梅"))
            return R.drawable.food_milktea;
        if (name.contains("面") || name.contains("粉"))
            return R.drawable.ic_food_noodle;
        if (name.contains("日料") || name.contains("寿司") || name.contains("刺身") || name.contains("鱼生"))
            return R.drawable.ic_food_japanese;
        if (name.contains("披萨") || name.contains("意面") || name.contains("沙拉") || name.contains("轻食"))
            return R.drawable.ic_food_western;
        // 默认
        return R.drawable.food_kungpao;
    }

    /**
     * 获取首页 Banner 图片（3种不同色调）
     */
    public static int getBannerDrawable(int index) {
        switch (Math.abs(index) % 3) {
            case 0: return R.drawable.bg_banner_weekend;
            case 1: return R.drawable.bg_banner_newshop;
            case 2: return R.drawable.bg_banner_flashsale;
        }
        return R.drawable.bg_banner_weekend;
    }

    /** 保留旧方法兼容 */
    public static int getBannerDrawable() {
        return R.drawable.bg_banner_weekend;
    }
}
