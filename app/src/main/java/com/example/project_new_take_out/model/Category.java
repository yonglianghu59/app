package com.example.project_new_take_out.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 菜品分类实体类（店铺内菜品分类）
 */
public class Category {

    @SerializedName("id")
    private int id;

    @SerializedName("shop_id")
    private int shopId;

    @SerializedName("name")
    private String name;

    @SerializedName("sort_order")
    private int sortOrder;

    @SerializedName("foods")
    private List<Food> foods;

    // 构造函数
    public Category() {}

    public Category(int id, int shopId, String name, int sortOrder) {
        this.id = id;
        this.shopId = shopId;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public List<Food> getFoods() { return foods; }
    public void setFoods(List<Food> foods) { this.foods = foods; }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
