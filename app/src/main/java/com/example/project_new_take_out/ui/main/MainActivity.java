package com.example.project_new_take_out.ui.main;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.project_new_take_out.app.BaseActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.ui.cart.CartFragment;
import com.example.project_new_take_out.ui.order.OrderFragment;
import com.example.project_new_take_out.ui.profile.ProfileFragment;
import com.example.project_new_take_out.utils.CartManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主 Activity（含购物车角标）
 */
public class MainActivity extends BaseActivity {

    private static final String TAG_HOME = "frag_home";
    private static final String TAG_ORDER = "frag_order";
    private static final String TAG_CART = "frag_cart";
    private static final String TAG_PROFILE = "frag_profile";

    private HomeFragment homeFragment;
    private OrderFragment orderFragment;
    private CartFragment cartFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;

    private BottomNavigationView bottomNavigation;
    private BadgeDrawable cartBadge;
    private LinearLayout aiFloatingBall;
    private View aiGlow;
    private float ballStartX, ballStartY, touchStartX, touchStartY;
    private long ballTouchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        // 购物车角标
        cartBadge = bottomNavigation.getOrCreateBadge(R.id.nav_cart);
        cartBadge.setBackgroundColor(getColor(R.color.cartBadge));
        cartBadge.setBadgeTextColor(getColor(R.color.textWhite));
        cartBadge.setVisible(false);

        // 监听购物车数量变化
        CartManager.getInstance().getTotalCountLive().observe(this, count -> {
            if (count != null && count > 0) {
                cartBadge.setNumber(count);
                cartBadge.setVisible(true);
            } else {
                cartBadge.setVisible(false);
            }
        });

        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState != null) {
            homeFragment = (HomeFragment) fm.findFragmentByTag(TAG_HOME);
            orderFragment = (OrderFragment) fm.findFragmentByTag(TAG_ORDER);
            cartFragment = (CartFragment) fm.findFragmentByTag(TAG_CART);
            profileFragment = (ProfileFragment) fm.findFragmentByTag(TAG_PROFILE);
        }

        if (homeFragment == null) homeFragment = new HomeFragment();
        if (orderFragment == null) orderFragment = new OrderFragment();
        if (cartFragment == null) cartFragment = new CartFragment();
        if (profileFragment == null) profileFragment = new ProfileFragment();

        if (savedInstanceState == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, homeFragment, TAG_HOME)
                    .add(R.id.fragment_container, orderFragment, TAG_ORDER).hide(orderFragment)
                    .add(R.id.fragment_container, cartFragment, TAG_CART).hide(cartFragment)
                    .add(R.id.fragment_container, profileFragment, TAG_PROFILE).hide(profileFragment)
                    .commit();
        }

        // 确保 activeFragment 始终有值（旋转屏幕后 savedInstanceState != null）
        activeFragment = homeFragment;

        // AI 悬浮球
        setupFloatingBall();

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment target = null;

            if (itemId == R.id.nav_home) target = homeFragment;
            else if (itemId == R.id.nav_order) target = orderFragment;
            else if (itemId == R.id.nav_cart) target = cartFragment;
            else if (itemId == R.id.nav_profile) target = profileFragment;

            if (target != null && target != activeFragment) {
                switchFragment(target);
                return true;
            }
            return target != null;
        });
    }

    private void switchFragment(Fragment target) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(activeFragment)
                .show(target)
                .commit();
        activeFragment = target;
    }

    public void switchToOrderTab() {
        switchToOrderTab(null);
    }

    /**
     * 切换到订单Tab并设置状态筛选
     * @param statusFilter "pending" / "delivering" / "completed" / null=全部
     */
    public void switchToOrderTab(String statusFilter) {
        if (orderFragment != null && statusFilter != null) {
            orderFragment.setInitialFilter(statusFilter);
        }
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_order);
        }
    }

    public void switchToCartTab() {
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_cart);
        }
    }

    // ====== AI 悬浮球 ======

    private void setupFloatingBall() {
        aiFloatingBall = findViewById(R.id.ai_floating_ball);
        aiGlow = findViewById(R.id.ai_glow);
        if (aiFloatingBall == null) return;

        // 脉冲动画
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.anim_pulse);
        aiFloatingBall.startAnimation(pulse);
        if (aiGlow != null) aiGlow.startAnimation(pulse);

        aiFloatingBall.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ballStartX = v.getX();
                    ballStartY = v.getY();
                    touchStartX = event.getRawX();
                    touchStartY = event.getRawY();
                    ballTouchTime = System.currentTimeMillis();
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    if (aiGlow != null) aiGlow.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start();
                    return true;

                case MotionEvent.ACTION_MOVE: {
                    float dx = event.getRawX() - touchStartX;
                    float dy = event.getRawY() - touchStartY;
                    float newX = ballStartX + dx;
                    float newY = ballStartY + dy;
                    int parentW = ((View) v.getParent()).getWidth();
                    int parentH = ((View) v.getParent()).getHeight();
                    newX = Math.max(0, Math.min(newX, parentW - v.getWidth()));
                    newY = Math.max(0, Math.min(newY, parentH - v.getHeight()));
                    v.setX(newX);
                    v.setY(newY);
                    // 同步移动光晕
                    if (aiGlow != null) {
                        aiGlow.setX(newX - dp2px(6));
                        aiGlow.setY(newY - dp2px(6));
                    }
                    return true;
                }

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    if (aiGlow != null) aiGlow.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
                    if (System.currentTimeMillis() - ballTouchTime < 300
                            && Math.abs(event.getRawX() - touchStartX) < 15
                            && Math.abs(event.getRawY() - touchStartY) < 15) {
                        // 点击 → 打开 AI 聊天
                        android.content.Intent intent = new android.content.Intent(
                                MainActivity.this,
                                com.example.project_new_take_out.ui.ai.AIChatActivity.class);
                        startActivity(intent);
                    }
                    return true;
            }
            return false;
        });
    }

    private int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
