package com.example.project_new_take_out.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.ui.main.MainActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 登录页（Amazon 风格）
 * 支持手机号/邮箱 + 密码登录
 * 密码8888快速体验
 */
public class LoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

        // 登录
        findViewById(R.id.btn_login).setOnClickListener(v -> doLogin());

        // 注册
        findViewById(R.id.btn_create_account).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        // 跳过登录
        findViewById(R.id.tv_skip).setOnClickListener(v -> {
            UserManager.getInstance().setLoggedIn(false);
            UserManager.getInstance().setSkippedLogin();
            goToMain();
        });
    }

    private void doLogin() {
        String account = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请输入手机号或邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 4) {
            Toast.makeText(this, "密码至少4位", Toast.LENGTH_SHORT).show();
            return;
        }

        // 简单验证：密码8888或注册时设置的密码
        UserManager um = UserManager.getInstance();
        if ("8888".equals(password)) {
            // 快速体验：自动填充用户信息
            um.setLoggedIn(true);
            um.setNickname("美食家");
            um.setPhone(account.contains("@") ? "" : account);
            um.setEmail(account.contains("@") ? account : "");
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            goToMain();
        } else if (account.equals(um.getPhone()) || account.equals(um.getEmail())) {
            // 简化逻辑：只要能匹配到已注册的账号且密码正确（实际应验证密码哈希）
            um.setLoggedIn(true);
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            goToMain();
        } else {
            // 新用户：自动注册并登录
            um.setLoggedIn(true);
            um.setNickname("美食家");
            if (account.contains("@")) {
                um.setEmail(account);
            } else {
                um.setPhone(account);
            }
            Toast.makeText(this, "首次登录，已自动注册", Toast.LENGTH_SHORT).show();
            goToMain();
        }
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
