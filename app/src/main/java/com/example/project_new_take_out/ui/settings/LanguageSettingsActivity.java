package com.example.project_new_take_out.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.utils.LocaleHelper;

/**
 * 语言设置页（支持简体中文 / English 实时切换）
 */
public class LanguageSettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_settings);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        RadioGroup rg = findViewById(R.id.rg_language);
        RadioButton rbChinese = findViewById(R.id.rb_chinese);
        RadioButton rbEnglish = findViewById(R.id.rb_english);

        // 恢复已保存的语言设置
        String savedLang = LocaleHelper.getSavedLanguage(this);
        if ("en".equals(savedLang)) {
            rbEnglish.setChecked(true);
        } else {
            rbChinese.setChecked(true);
        }

        findViewById(R.id.btn_confirm_language).setOnClickListener(v -> {
            int id = rg.getCheckedRadioButtonId();
            String langCode;
            String langName;

            if (id == R.id.rb_english) {
                langCode = "en";
                langName = "English";
            } else {
                langCode = "zh";
                langName = "简体中文";
            }

            // 保存语言设置
            LocaleHelper.setLocale(this, langCode);

            Toast.makeText(this, "已切换为：" + langName, Toast.LENGTH_SHORT).show();

            // 重启当前 Activity 使语言生效
            recreate();
        });
    }
}
