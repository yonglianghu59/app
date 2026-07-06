package com.example.project_new_take_out.model;

import com.google.gson.annotations.SerializedName;

/**
 * 收货地址实体类
 */
public class Address {

    @SerializedName("id")
    private int id;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("contact_phone")
    private String contactPhone;

    @SerializedName("province")
    private String province;

    @SerializedName("city")
    private String city;

    @SerializedName("district")
    private String district;

    @SerializedName("detail")
    private String detail;

    @SerializedName("is_default")
    private boolean isDefault;

    // 构造函数
    public Address() {}

    public Address(int id, String contactName, String contactPhone, String province,
                   String city, String district, String detail, boolean isDefault) {
        this.id = id;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.province = province;
        this.city = city;
        this.district = district;
        this.detail = detail;
        this.isDefault = isDefault;
    }

    /**
     * 获取完整地址
     */
    public String getFullAddress() {
        return province + city + district + detail;
    }

    /**
     * 获取隐藏手机号（中间四位打码）
     */
    public String getHiddenPhone() {
        if (contactPhone == null || contactPhone.length() < 7) {
            return contactPhone;
        }
        return contactPhone.substring(0, 3) + "****" + contactPhone.substring(7);
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
