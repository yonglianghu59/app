package com.example.project_new_take_out.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.ui.main.MainActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 首次使用引导页（ViewPager2 轮播）
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView btnNext, btnSkip;
    private View dot1, dot2, dot3;
    private int currentPage = 0;

    private final int[] PAGE_ICONS = {
            R.drawable.ic_category_food,
            R.drawable.ic_cart,
            R.drawable.ic_profile_coupon
    };
    private final String[] PAGE_TITLES = {
            "浏览美食", "轻松下单", "优惠多多"
    };
    private final String[] PAGE_DESCS = {
            "海量餐厅任你选\n中餐、日料、西餐、饮品…\n总有一款适合你",
            "选好菜品加入购物车\n一键下单，在线支付\n美食即刻送达",
            "新用户专享优惠\n满减红包等你来领\n订餐更实惠"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.view_pager);
        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        dot1 = findViewById(R.id.dot_1);
        dot2 = findViewById(R.id.dot_2);
        dot3 = findViewById(R.id.dot_3);

        viewPager.setAdapter(new OnboardingAdapter());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateDots();
                btnNext.setText(position == 2 ? "开始使用" : "下一步");
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < 2) {
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void updateDots() {
        dot1.setBackgroundResource(currentPage == 0
                ? R.drawable.selector_orange_button : R.drawable.bg_button_disabled);
        dot2.setBackgroundResource(currentPage == 1
                ? R.drawable.selector_orange_button : R.drawable.bg_button_disabled);
        dot3.setBackgroundResource(currentPage == 2
                ? R.drawable.selector_orange_button : R.drawable.bg_button_disabled);
    }

    private void finishOnboarding() {
        UserManager.getInstance().completeOnboarding();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private class OnboardingAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        OnboardingAdapter() {
            super(OnboardingActivity.this);
        }

        @Override
        public int getItemCount() { return 3; }

        @Override
        public androidx.fragment.app.Fragment createFragment(int position) {
            return OnboardingPageFragment.newInstance(
                    PAGE_ICONS[position], PAGE_TITLES[position], PAGE_DESCS[position]);
        }
    }
}
