package com.example.project_new_take_out.ui.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.ui.main.MainActivity;

/**
 * 全部分类页面
 * 展示所有美食/饮品/生活服务分类
 */
public class AllCategoriesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 美食外卖分类
        int[] foodIds = {
                R.id.cat_chinese, R.id.cat_hotpot, R.id.cat_bbq, R.id.cat_noodle,
                R.id.cat_japanese, R.id.cat_korean, R.id.cat_western, R.id.cat_seafood
        };
        String[] foodNames = {"中餐", "火锅", "烧烤", "面馆", "日料", "韩餐", "西餐", "海鲜"};

        for (int i = 0; i < foodIds.length; i++) {
            View catView = findViewById(foodIds[i]);
            if (catView != null) {
                final String name = foodNames[i];
                catView.setOnClickListener(v -> onCategoryClick(name));
            }
        }

        // 饮品甜点分类
        int[] drinkIds = {R.id.cat_milktea, R.id.cat_coffee, R.id.cat_dessert, R.id.cat_icecream};
        String[] drinkNames = {"奶茶", "咖啡", "甜品", "冰淇淋"};

        for (int i = 0; i < drinkIds.length; i++) {
            View catView = findViewById(drinkIds[i]);
            if (catView != null) {
                final String name = drinkNames[i];
                catView.setOnClickListener(v -> onCategoryClick(name));
            }
        }

        // 生活服务分类
        int[] lifeIds = {R.id.cat_market2, R.id.cat_flower2, R.id.cat_medicine2, R.id.cat_fruit};
        String[] lifeNames = {"超市便利", "鲜花绿植", "送药上门", "水果"};

        for (int i = 0; i < lifeIds.length; i++) {
            View catView = findViewById(lifeIds[i]);
            if (catView != null) {
                final String name = lifeNames[i];
                catView.setOnClickListener(v -> onCategoryClick(name));
            }
        }
    }

    private void onCategoryClick(String categoryName) {
        Toast.makeText(this, "正在进入「" + categoryName + "」...", Toast.LENGTH_SHORT).show();
        // 返回首页并传递分类筛选参数
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("category", categoryName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
