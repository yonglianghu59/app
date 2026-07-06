package com.example.project_new_take_out.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

/**
 * 修改手机号页
 */
public class ChangePhoneActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        TextView tvSendCode = findViewById(R.id.tv_send_code);
        EditText etPhone = findViewById(R.id.et_new_phone);
        EditText etCode = findViewById(R.id.et_verify_code);

        tvSendCode.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.length() != 11) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show();
            tvSendCode.setText("60s后重发");
            tvSendCode.setEnabled(false);
        });

        findViewById(R.id.btn_confirm_phone).setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "手机号修改成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
