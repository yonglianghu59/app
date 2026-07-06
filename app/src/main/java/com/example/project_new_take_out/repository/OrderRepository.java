package com.example.project_new_take_out.repository;

import com.example.project_new_take_out.model.Address;
import com.example.project_new_take_out.model.Order;

/**
 * 订单数据仓库
 * 管理订单创建、地址获取等业务
 * 开发阶段使用模拟数据
 */
public class OrderRepository {

    private static volatile OrderRepository instance;

    private OrderRepository() {}

    public static OrderRepository getInstance() {
        if (instance == null) {
            synchronized (OrderRepository.class) {
                if (instance == null) {
                    instance = new OrderRepository();
                }
            }
        }
        return instance;
    }

    /**
     * 获取默认收货地址（模拟数据）
     */
    public Address getDefaultAddress() {
        return new Address(
                1,
                "张三",
                "13812345678",
                "四川省",
                "成都市",
                "武侯区",
                "天府大道中段 688 号天府软件园",
                true
        );
    }

    /**
     * 创建订单
     */
    public Order createOrder(Order order) {
        order.setOrderId(Order.generateOrderId());
        order.setStatus("pending");
        order.setCreateTime(System.currentTimeMillis());
        return order;
    }
}
