package com.example.project_new_take_out.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

/**
 * 修改密码页
 */
public class ChangePasswordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        EditText etOld = findViewById(R.id.et_old_password);
        EditText etNew = findViewById(R.id.et_new_password);
        EditText etConfirm = findViewById(R.id.et_confirm_password);

        findViewById(R.id.btn_confirm_password).setOnClickListener(v -> {
            String oldPwd = etOld.getText().toString().trim();
            String newPwd = etNew.getText().toString().trim();
            String confirmPwd = etConfirm.getText().toString().trim();

            if (TextUtils.isEmpty(oldPwd)) { Toast.makeText(this, "请输入当前密码", Toast.LENGTH_SHORT).show(); return; }
            if (newPwd.length() < 6) { Toast.makeText(this, "新密码至少6位", Toast.LENGTH_SHORT).show(); return; }
            if (!newPwd.equals(confirmPwd)) { Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show(); return; }

            Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
