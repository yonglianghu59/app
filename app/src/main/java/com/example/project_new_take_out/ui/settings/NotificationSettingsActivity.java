package com.example.project_new_take_out.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

/**
 * 推送通知设置页（SharedPreferences 持久化）
 */
public class NotificationSettingsActivity extends BaseActivity {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_ORDER_NOTIFY = "notify_order";
    private static final String KEY_PROMO_NOTIFY = "notify_promo";
    private static final String KEY_SYSTEM_NOTIFY = "notify_system";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        SwitchCompat swOrder = findViewById(R.id.switch_order);
        SwitchCompat swPromo = findViewById(R.id.switch_promo);
        SwitchCompat swSystem = findViewById(R.id.switch_system);

        // 恢复已保存的开关状态（默认全部开启）
        swOrder.setChecked(prefs.getBoolean(KEY_ORDER_NOTIFY, true));
        swPromo.setChecked(prefs.getBoolean(KEY_PROMO_NOTIFY, true));
        swSystem.setChecked(prefs.getBoolean(KEY_SYSTEM_NOTIFY, true));

        // 订单通知
        swOrder.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(KEY_ORDER_NOTIFY, checked).apply();
            Toast.makeText(this, "订单通知" + (checked ? "已开启" : "已关闭"), Toast.LENGTH_SHORT).show();
        });

        // 优惠通知
        swPromo.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(KEY_PROMO_NOTIFY, checked).apply();
            Toast.makeText(this, "优惠通知" + (checked ? "已开启" : "已关闭"), Toast.LENGTH_SHORT).show();
        });

        // 系统消息
        swSystem.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(KEY_SYSTEM_NOTIFY, checked).apply();
            Toast.makeText(this, "系统消息" + (checked ? "已开启" : "已关闭"), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 获取订单通知是否开启（供其他模块查询）
     */
    public static boolean isOrderNotifyEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ORDER_NOTIFY, true);
    }

    /**
     * 获取优惠通知是否开启
     */
    public static boolean isPromoNotifyEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_PROMO_NOTIFY, true);
    }

    /**
     * 获取系统消息是否开启
     */
    public static boolean isSystemNotifyEnabled(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_SYSTEM_NOTIFY, true);
    }
}
