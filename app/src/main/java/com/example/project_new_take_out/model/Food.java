package com.example.project_new_take_out.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 菜品实体类
 */
public class Food {

    @SerializedName("id")
    private int id;

    @SerializedName("shop_id")
    private int shopId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("price")
    private double price;

    @SerializedName("monthly_sales")
    private int monthlySales;

    @SerializedName("description")
    private String description;

    @SerializedName("taste")
    private String taste;

    @SerializedName("ingredients")
    private String ingredients;

    @SerializedName("sizes")
    private List<Option> sizes;

    @SerializedName("spicy_levels")
    private List<Option> spicyLevels;

    /**
     * 规格/辣度选项内部类
     */
    public static class Option {
        @SerializedName("name")
        private String name;

        @SerializedName("price_offset")
        private double priceOffset;

        public Option() {}

        public Option(String name, double priceOffset) {
            this.name = name;
            this.priceOffset = priceOffset;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPriceOffset() { return priceOffset; }
        public void setPriceOffset(double priceOffset) { this.priceOffset = priceOffset; }
    }

    // 构造函数
    public Food() {}

    public Food(int id, int shopId, int categoryId, String name, String imageUrl,
                double price, int monthlySales, String description) {
        this.id = id;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.monthlySales = monthlySales;
        this.description = description;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getMonthlySales() { return monthlySales; }
    public void setMonthlySales(int monthlySales) { this.monthlySales = monthlySales; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTaste() { return taste; }
    public void setTaste(String taste) { this.taste = taste; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public List<Option> getSizes() { return sizes; }
    public void setSizes(List<Option> sizes) { this.sizes = sizes; }

    public List<Option> getSpicyLevels() { return spicyLevels; }
    public void setSpicyLevels(List<Option> spicyLevels) { this.spicyLevels = spicyLevels; }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
