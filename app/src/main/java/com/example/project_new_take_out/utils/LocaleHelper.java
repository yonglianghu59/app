package com.example.project_new_take_out.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * 语言切换工具类
 * 支持简体中文 / English 切换，持久化到 SharedPreferences
 */
public class LocaleHelper {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_LANGUAGE = "app_language";

    /**
     * 设置应用语言
     * @param languageCode "zh" = 简体中文, "en" = English
     */
    public static void setLocale(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    /**
     * 获取已保存的语言代码
     */
    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "zh"); // 默认中文
    }

    /**
     * 获取当前 Locale
     */
    public static Locale getLocale(Context context) {
        String lang = getSavedLanguage(context);
        if ("en".equals(lang)) {
            return Locale.ENGLISH;
        }
        return Locale.SIMPLIFIED_CHINESE;
    }

    /**
     * 获取当前语言显示名称
     */
    public static String getLanguageDisplayName(Context context) {
        String lang = getSavedLanguage(context);
        if ("en".equals(lang)) {
            return "English";
        }
        return "简体中文";
    }

    /**
     * 应用语言到 Context
     * 应在 Activity.attachBaseContext 中调用
     */
    public static Context wrapContext(Context context) {
        Locale locale = getLocale(context);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            return context;
        }
    }
}
