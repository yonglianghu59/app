package com.example.project_new_take_out.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 注册页
 * 支持上传头像 + 设置昵称/手机号/密码
 */
public class RegisterActivity extends BaseActivity {

    private ImageView ivAvatar;
    private EditText etNickname, etPhone, etPassword, etConfirmPwd;
    private String avatarUri = "";
    private ActivityResultLauncher<String> imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ivAvatar = findViewById(R.id.iv_avatar_preview);
        etNickname = findViewById(R.id.et_nickname);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPwd = findViewById(R.id.et_confirm_password);

        // 图片选择器
        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        avatarUri = uri.toString();
                        Glide.with(this).load(uri).circleCrop().into(ivAvatar);
                    }
                });

        ivAvatar.setOnClickListener(v -> imagePicker.launch("image/*"));

        findViewById(R.id.btn_register).setOnClickListener(v -> doRegister());
        findViewById(R.id.tv_go_login).setOnClickListener(v -> finish());
    }

    private void doRegister() {
        String nickname = etNickname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();

        if (TextUtils.isEmpty(nickname)) { Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show(); return; }
        if (TextUtils.isEmpty(phone)) { Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show(); return; }
        if (password.length() < 4) { Toast.makeText(this, "密码至少4位", Toast.LENGTH_SHORT).show(); return; }
        if (!password.equals(confirmPwd)) { Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show(); return; }

        UserManager um = UserManager.getInstance();
        um.setLoggedIn(true);
        um.setNickname(nickname);
        um.setPhone(phone);
        if (!avatarUri.isEmpty()) um.setAvatarUri(avatarUri);

        Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, com.example.project_new_take_out.ui.main.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
