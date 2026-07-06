package com.example.project_new_take_out.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast 封装工具类
 * 统一管理提示信息，方便后续定制样式
 */
public class ToastUtils {

    private static Toast currentToast;

    /**
     * 显示短 Toast
     */
    public static void showShort(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长 Toast
     */
    public static void showLong(Context context, String message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示 Toast（避免连续点击导致排队）
     */
    private static void show(Context context, String message, int duration) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(context.getApplicationContext(), message, duration);
        currentToast.show();
    }

    /**
     * 取消当前 Toast
     */
    public static void cancel() {
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
    }
}
