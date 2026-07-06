package com.example.project_new_take_out.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户会话管理器（单例）
 * 管理登录状态、昵称、头像、手机号、邮箱
 */
public class UserManager {

    private static final String PREFS_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_AVATAR_URI = "avatar_uri";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";

    private static volatile UserManager instance;
    private SharedPreferences prefs;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) instance = new UserManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    // ====== 登录状态 ======

    public boolean isLoggedIn() {
        return prefs != null && prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        if (prefs != null) prefs.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    // ====== 昵称 ======

    public String getNickname() {
        return prefs != null ? prefs.getString(KEY_NICKNAME, "美食家") : "美食家";
    }

    public void setNickname(String nickname) {
        if (prefs != null) prefs.edit().putString(KEY_NICKNAME, nickname).apply();
    }

    // ====== 头像 ======

    public String getAvatarUri() {
        return prefs != null ? prefs.getString(KEY_AVATAR_URI, "") : "";
    }

    public void setAvatarUri(String uri) {
        if (prefs != null) prefs.edit().putString(KEY_AVATAR_URI, uri).apply();
    }

    // ====== 手机号 ======

    public String getPhone() {
        return prefs != null ? prefs.getString(KEY_PHONE, "") : "";
    }

    public void setPhone(String phone) {
        if (prefs != null) prefs.edit().putString(KEY_PHONE, phone).apply();
    }

    // ====== 邮箱 ======

    public String getEmail() {
        return prefs != null ? prefs.getString(KEY_EMAIL, "") : "";
    }

    public void setEmail(String email) {
        if (prefs != null) prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    // ====== 首次使用引导 ======

    public boolean isFirstLaunch() {
        return prefs != null && prefs.getBoolean("first_launch", true);
    }

    public void completeOnboarding() {
        if (prefs != null) prefs.edit().putBoolean("first_launch", false).apply();
    }

    // ====== 跳过登录标记 ======

    /**
     * 用户选择了"跳过登录"，下次启动直接进入主页
     */
    public void setSkippedLogin() {
        if (prefs != null) prefs.edit().putBoolean("skip_login", true).apply();
    }

    /**
     * 是否之前跳过登录（无需再次弹出登录页）
     */
    public boolean hasSkippedLogin() {
        return prefs != null && prefs.getBoolean("skip_login", false);
    }

    // ====== 用户唯一标识 ======

    /**
     * 基于手机号生成唯一用户ID（用于数据隔离）
     */
    public String getUserId() {
        String phone = getPhone();
        if (phone != null && phone.length() >= 11) {
            return "user_" + phone.hashCode();
        }
        String email = getEmail();
        if (email != null && !email.isEmpty()) {
            return "user_" + email.hashCode();
        }
        return "user_guest";
    }

    // ====== 退出登录 — 清除所有用户数据 ======

    public void logout() {
        if (prefs != null) {
            prefs.edit().clear().apply();
        }
    }

    // ====== 切换账号 — 清除数据，但保留跳过登录标记 ======

    public void switchAccount() {
        logout();
        setSkippedLogin();
    }
}
