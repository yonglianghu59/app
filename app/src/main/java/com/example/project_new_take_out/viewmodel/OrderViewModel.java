package com.example.project_new_take_out.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_new_take_out.model.Address;
import com.example.project_new_take_out.model.CartItem;
import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.repository.OrderRepository;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;

import java.util.List;

/**
 * 确认订单页 ViewModel
 * 管理收货地址、商品清单、费用计算
 */
public class OrderViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;

    private final MutableLiveData<Address> addressLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<CartItem>> orderItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Double> goodsTotalLiveData = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> deliveryFeeLiveData = new MutableLiveData<>(5.0);
    private final MutableLiveData<Double> discountLiveData = new MutableLiveData<>(3.0);
    private final MutableLiveData<Double> actualPayLiveData = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> shopNameLiveData = new MutableLiveData<>("");
    private final MutableLiveData<Order> orderLiveData = new MutableLiveData<>();

    private int shopId;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = OrderRepository.getInstance();
    }

    /**
     * 加载订单数据
     */
    public void loadOrderData(int shopId, String shopName, double deliveryFee) {
        this.shopId = shopId;
        this.shopNameLiveData.setValue(shopName);
        this.deliveryFeeLiveData.setValue(deliveryFee);

        // 加载默认地址
        Address address = orderRepository.getDefaultAddress();
        addressLiveData.setValue(address);

        // 加载购物车商品
        List<CartItem> items = CartManager.getInstance().getAllItems();
        orderItemsLiveData.setValue(items);

        // 计算费用
        calculateAmount(items, deliveryFee);
    }

    /**
     * 费用计算
     */
    private void calculateAmount(List<CartItem> items, double deliveryFee) {
        double goodsTotal = PriceCalculator.calculateGoodsTotal(items);
        goodsTotalLiveData.setValue(goodsTotal);

        // 优惠（满30减3元）
        double discount = goodsTotal >= 30 ? 3.0 : 0.0;
        discountLiveData.setValue(discount);

        // 实付金额 = 商品总价 + 配送费 - 优惠
        double actualPay = PriceCalculator.calculateActualPay(goodsTotal, deliveryFee, discount);
        actualPayLiveData.setValue(actualPay);
    }

    /**
     * 生成订单
     */
    public void submitOrder() {
        Order order = new Order();
        order.setShopId(shopId);
        order.setShopName(shopNameLiveData.getValue());
        order.setItems(orderItemsLiveData.getValue());
        order.setGoodsTotal(goodsTotalLiveData.getValue() != null ? goodsTotalLiveData.getValue() : 0);
        order.setDeliveryFee(deliveryFeeLiveData.getValue() != null ? deliveryFeeLiveData.getValue() : 0);
        order.setDiscount(discountLiveData.getValue() != null ? discountLiveData.getValue() : 0);
        order.setActualAmount(actualPayLiveData.getValue() != null ? actualPayLiveData.getValue() : 0);

        order = orderRepository.createOrder(order);
        orderLiveData.setValue(order);
    }

    // ========== Getter ==========
    public LiveData<Address> getAddressLiveData() { return addressLiveData; }
    public LiveData<List<CartItem>> getOrderItemsLiveData() { return orderItemsLiveData; }
    public LiveData<Double> getGoodsTotalLiveData() { return goodsTotalLiveData; }
    public LiveData<Double> getDeliveryFeeLiveData() { return deliveryFeeLiveData; }
    public LiveData<Double> getDiscountLiveData() { return discountLiveData; }
    public LiveData<Double> getActualPayLiveData() { return actualPayLiveData; }
    public LiveData<String> getShopNameLiveData() { return shopNameLiveData; }
    public LiveData<Order> getOrderLiveData() { return orderLiveData; }
}
