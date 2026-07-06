package com.example.project_new_take_out.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.ui.auth.LoginActivity;
import com.example.project_new_take_out.ui.main.MainActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 启动页（检查登录状态 + 首次使用引导）
 */
public class SplashActivity extends BaseActivity {

    private Handler splashHandler;
    private Runnable splashRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.project_new_take_out.R.layout.activity_splash);

        splashHandler = new Handler(Looper.getMainLooper());
        splashRunnable = () -> {
            if (isFinishing() || isDestroyed()) return;
            Intent intent;
            UserManager um = UserManager.getInstance();
            // 首次使用 → 引导页
            if (um.isFirstLaunch()) {
                intent = new Intent(SplashActivity.this,
                        com.example.project_new_take_out.ui.onboarding.OnboardingActivity.class);
            } else if (um.isLoggedIn() || um.hasSkippedLogin()) {
                // 已登录 或 曾跳过登录 → 主页
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        };
        splashHandler.postDelayed(splashRunnable, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (splashHandler != null && splashRunnable != null) {
            splashHandler.removeCallbacks(splashRunnable);
        }
    }
}
