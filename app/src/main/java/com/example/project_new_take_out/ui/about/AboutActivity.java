package com.example.project_new_take_out.ui.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

/**
 * 关于页面（动态版本信息 + 可交互入口）
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // 返回
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 动态版本号（通过 PackageManager 获取，不依赖 BuildConfig）
        TextView tvVersion = findViewById(R.id.tv_app_version);
        if (tvVersion != null) {
            String versionName;
            try {
                PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionName = pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = "1.0.0";
            }
            tvVersion.setText("v" + versionName);
        }

        // 长按版本号显示调试信息
        if (tvVersion != null) {
            tvVersion.setOnLongClickListener(v -> {
                String debugInfo = "应用包名: " + getPackageName();
                try {
                    PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
                    debugInfo += "\nVersionCode: " + pi.versionCode
                            + "\nVersionName: " + pi.versionName;
                } catch (PackageManager.NameNotFoundException ignored) {}
                Toast.makeText(this, debugInfo, Toast.LENGTH_LONG).show();
                return true;
            });
        }

        // 联系邮箱 — 可点击
        TextView tvEmail = findViewById(R.id.tv_contact_email);
        if (tvEmail != null) {
            tvEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@foodorder.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "网上订餐 App 反馈");
                try {
                    startActivity(Intent.createChooser(intent, "发送邮件"));
                } catch (Exception e) {
                    Toast.makeText(this, "未找到邮件客户端", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 客服热线 — 可拨打
        TextView tvPhone = findViewById(R.id.tv_contact_phone);
        if (tvPhone != null) {
            tvPhone.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:4008888888"));
                startActivity(intent);
            });
        }

        // 隐私政策 & 用户协议（预留）
        TextView tvPrivacy = findViewById(R.id.tv_privacy_policy);
        if (tvPrivacy != null) {
            tvPrivacy.setOnClickListener(v ->
                    Toast.makeText(this, "隐私政策页面开发中", Toast.LENGTH_SHORT).show());
        }

        TextView tvTerms = findViewById(R.id.tv_terms_service);
        if (tvTerms != null) {
            tvTerms.setOnClickListener(v ->
                    Toast.makeText(this, "用户协议页面开发中", Toast.LENGTH_SHORT).show());
        }
    }
}
