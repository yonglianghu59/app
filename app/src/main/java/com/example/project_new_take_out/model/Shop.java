package com.example.project_new_take_out.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 店铺实体类
 */
public class Shop {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("score")
    private float score;

    @SerializedName("monthly_sales")
    private int monthlySales;

    @SerializedName("min_price")
    private double minPrice;

    @SerializedName("delivery_fee")
    private double deliveryFee;

    @SerializedName("delivery_time")
    private int deliveryTime;

    @SerializedName("distance")
    private double distance;

    @SerializedName("tags")
    private List<String> tags;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    // 构造函数
    public Shop() {}

    public Shop(int id, String name, String imageUrl, float score, int monthlySales,
                double minPrice, double deliveryFee, int deliveryTime, double distance,
                List<String> tags) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.score = score;
        this.monthlySales = monthlySales;
        this.minPrice = minPrice;
        this.deliveryFee = deliveryFee;
        this.deliveryTime = deliveryTime;
        this.distance = distance;
        this.tags = tags;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }

    public int getMonthlySales() { return monthlySales; }
    public void setMonthlySales(int monthlySales) { this.monthlySales = monthlySales; }

    public double getMinPrice() { return minPrice; }
    public void setMinPrice(double minPrice) { this.minPrice = minPrice; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
