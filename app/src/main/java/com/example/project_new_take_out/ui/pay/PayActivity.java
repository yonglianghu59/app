package com.example.project_new_take_out.ui.pay;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.model.Order;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.PriceCalculator;
import com.example.project_new_take_out.utils.ToastUtils;
import com.example.project_new_take_out.viewmodel.PayViewModel;

/**
 * 支付页
 * 展示支付金额、支付方式选择、15 分钟支付倒计时
 */
public class PayActivity extends BaseActivity {

    private PayViewModel viewModel;

    private TextView tvPayAmount, tvOrderNumber, tvCountdown;
    private View layoutWechatPay, layoutAlipay;
    private TextView btnBackShop, btnConfirmPay;

    private String orderId;
    private double orderAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        orderId = getIntent().getStringExtra("order_id");
        orderAmount = getIntent().getDoubleExtra("order_amount", 0);

        // 校验必要参数
        if (orderId == null || orderAmount <= 0) {
            ToastUtils.showShort(this, "订单数据异常，请重试");
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(PayViewModel.class);

        initViews();
        observeViewModel();

        // 构建 Order 对象传入 ViewModel
        Order order = new Order();
        order.setOrderId(orderId);
        order.setActualAmount(orderAmount);
        viewModel.initPayment(order);
    }

    private void initViews() {
        tvPayAmount = findViewById(R.id.tv_pay_amount);
        tvOrderNumber = findViewById(R.id.tv_order_number);
        tvCountdown = findViewById(R.id.tv_countdown);
        layoutWechatPay = findViewById(R.id.layout_wechat_pay);
        layoutAlipay = findViewById(R.id.layout_alipay);
        btnBackShop = findViewById(R.id.btn_back_shop);
        btnConfirmPay = findViewById(R.id.btn_confirm_pay);

        // 返回键
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 返回商家
        btnBackShop.setOnClickListener(v -> finish());

        // 选择微信支付
        layoutWechatPay.setOnClickListener(v -> {
            layoutWechatPay.setSelected(true);
            layoutAlipay.setSelected(false);
            viewModel.selectPayMethod("wechat");
        });

        // 选择支付宝
        layoutAlipay.setOnClickListener(v -> {
            layoutAlipay.setSelected(true);
            layoutWechatPay.setSelected(false);
            viewModel.selectPayMethod("alipay");
        });

        // 确认支付
        btnConfirmPay.setOnClickListener(v -> {
            viewModel.confirmPay();
            btnConfirmPay.setEnabled(false);
            btnConfirmPay.setText("支付中...");
        });
    }

    private void observeViewModel() {
        // 支付金额
        viewModel.getPaymentInfoLiveData().observe(this, info -> {
            if (info != null) {
                tvPayAmount.setText(PriceCalculator.formatPrice(info.getAmount()));
                tvOrderNumber.setText("订单编号：" + info.getOrderId());
            }
        });

        // 倒计时
        viewModel.getCountdownLiveData().observe(this, time -> {
            tvCountdown.setText(time);
        });

        // 过期
        viewModel.getIsExpiredLiveData().observe(this, isExpired -> {
            if (Boolean.TRUE.equals(isExpired)) {
                showExpiredDialog();
            }
        });

        // 支付成功
        viewModel.getPaySuccessLiveData().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                showPaySuccessDialog();
            }
        });
    }

    /**
     * 支付超时弹窗
     */
    private void showExpiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("订单已超时")
                .setMessage("支付时间已超过 15 分钟，订单已自动取消。")
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * 支付成功弹窗
     */
    private void showPaySuccessDialog() {
        // 支付成功后清空购物车
        CartManager.getInstance().clearCart();

        // 更新订单状态为配送中
        if (orderId != null) {
            getSharedPreferences("order_status", MODE_PRIVATE)
                    .edit().putString(orderId, "delivering").apply();
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.pay_success)
                .setMessage("支付金额：" + PriceCalculator.formatPrice(orderAmount))
                .setPositiveButton("返回首页", (dialog, which) -> {
                    dialog.dismiss();
                    // 回到首页（关闭所有页面）
                    finishAffinity();
                })
                .setCancelable(false)
                .show();
    }
}
