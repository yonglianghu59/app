package com.example.project_new_take_out.app;

import android.app.Application;

import com.example.project_new_take_out.db.AppDatabase;
import com.example.project_new_take_out.net.RetrofitClient;
import com.example.project_new_take_out.utils.CartManager;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 全局 Application
 * APP 启动时初始化核心组件
 */
public class FoodOrderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化网络客户端（预热 Retrofit 单例）
        RetrofitClient.getInstance(this);

        // 初始化数据库
        AppDatabase.getInstance(this);

        // 初始化购物车管理器
        CartManager.getInstance().init(this);

        // 初始化用户会话管理器
        UserManager.getInstance().init(this);
    }
}
