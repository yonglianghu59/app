package com.example.project_new_take_out.ui.help;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 帮助中心页面
 */
public class HelpCenterActivity extends BaseActivity {

    private final Map<String, String> faqMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        initFaqData();
        setupFaqClick(R.id.faq_1, "如何下单？");
        setupFaqClick(R.id.faq_2, "配送时间是多久？");
        setupFaqClick(R.id.faq_3, "如何取消订单？");
        setupFaqClick(R.id.faq_4, "如何申请退款？");
        setupFaqClick(R.id.faq_5, "优惠券如何使用？");
        setupFaqClick(R.id.faq_6, "支付方式有哪些？");
        setupFaqClick(R.id.faq_7, "如何联系客服？");
        setupFaqClick(R.id.faq_8, "如何修改收货地址？");
    }

    private void initFaqData() {
        faqMap.put("如何下单？", "在首页选择您喜欢的餐厅 → 进入菜单选择菜品 → 加入购物车 → 点击去结算 → 确认订单并支付即可完成下单。");
        faqMap.put("配送时间是多久？", "根据商家出餐速度和配送距离，一般配送时间为20-50分钟。您可以在店铺详情页查看预计送达时间。");
        faqMap.put("如何取消订单？", "在「我的订单」中找到对应订单，点击进入详情后选择取消。注意：商家已接单后取消可能产生费用。");
        faqMap.put("如何申请退款？", "在「我的订单」→ 找到对应订单 → 点击「申请退款」→ 选择退款原因并提交。退款将在1-3个工作日内处理。");
        faqMap.put("优惠券如何使用？", "下单时在确认订单页面选择可用优惠券即可自动抵扣。注意每张优惠券有使用条件和有效期限制。");
        faqMap.put("支付方式有哪些？", "当前支持微信支付和支付宝支付。下单后在确认订单页面选择支付方式，按指引完成支付即可。");
        faqMap.put("如何联系客服？", "您可以通过「我的」→「联系客服」进入在线客服，或拨打客服热线：400-888-8888（9:00-21:00）。");
        faqMap.put("如何修改收货地址？", "在「我的」→「我的地址」中可以添加、编辑和删除收货地址。下单时也可临时修改配送地址。");
    }

    private void setupFaqClick(int id, String question) {
        View view = findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> {
                String answer = faqMap.get(question);
                if (answer != null) {
                    new AlertDialog.Builder(this)
                            .setTitle(question)
                            .setMessage(answer)
                            .setPositiveButton("知道了", null)
                            .show();
                }
            });
        }
    }
}
