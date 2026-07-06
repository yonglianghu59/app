package com.example.project_new_take_out.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.ui.about.AboutActivity;
import com.example.project_new_take_out.ui.auth.LoginActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 设置页面
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 编辑个人资料 → 弹出编辑框
        findViewById(R.id.item_edit_profile).setOnClickListener(v -> {
            EditText et = new EditText(this);
            et.setText(UserManager.getInstance().getNickname());
            et.setPadding(20, 20, 20, 20);
            new AlertDialog.Builder(this)
                    .setTitle("编辑昵称")
                    .setView(et)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("保存", (d, w) -> {
                        String name = et.getText().toString().trim();
                        if (!name.isEmpty()) {
                            UserManager.getInstance().setNickname(name);
                            Toast.makeText(this, "昵称已修改为：" + name, Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        });

        findViewById(R.id.item_change_phone).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePhoneActivity.class)));
        findViewById(R.id.item_change_password).setOnClickListener(v ->
                startActivity(new Intent(this, ChangePasswordActivity.class)));
        findViewById(R.id.item_push_notify).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationSettingsActivity.class)));

        // 消息声音 — 开关式
        findViewById(R.id.item_msg_sound).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("消息声音")
                    .setSingleChoiceItems(new String[]{"开启", "关闭"}, 0, (d, w) -> {})
                    .setPositiveButton("确定", null)
                    .show();
        });

        findViewById(R.id.item_language).setOnClickListener(v ->
                startActivity(new Intent(this, LanguageSettingsActivity.class)));

        // 清理缓存
        findViewById(R.id.item_cache).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("清理缓存")
                    .setMessage("缓存大小：约 2.3 MB\n确定清理缓存？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (d, w) ->
                            Toast.makeText(this, "缓存已清理", Toast.LENGTH_SHORT).show())
                    .show();
        });

        findViewById(R.id.item_about).setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));

        // 退出登录 — 清除数据并跳转登录页
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("退出登录")
                    .setMessage("确定退出当前账号吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("退出", (d, w) -> {
                        // 清除用户数据
                        UserManager.getInstance().logout();
                        // 跳转登录页，清空回退栈
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finishAffinity();
                    }).show();
        });
    }
}
