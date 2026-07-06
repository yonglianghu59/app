package com.example.project_new_take_out.ui.review;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;
import com.example.project_new_take_out.utils.UserManager;

/**
 * 评价页面
 * 接收订单上下文（orderId + shopName），星级评分 + 文字评价 + 持久化
 */
public class ReviewActivity extends BaseActivity {

    private int rating = 5;
    private ImageView[] stars = new ImageView[5];
    private EditText etReviewContent;
    private String orderId;
    private String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // 接收订单上下文
        orderId = getIntent().getStringExtra("order_id");
        shopName = getIntent().getStringExtra("shop_name");

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 设置评价标题
        TextView tvTitle = findViewById(R.id.tv_review_title);
        if (tvTitle != null && shopName != null) {
            tvTitle.setText("评价 " + shopName);
        }

        // 构建星级评分
        LinearLayout layoutStars = findViewById(R.id.layout_stars);
        for (int i = 0; i < 5; i++) {
            stars[i] = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp2px(36), dp2px(36));
            params.setMargins(0, 0, dp2px(8), 0);
            stars[i].setLayoutParams(params);
            final int starIndex = i;
            stars[i].setOnClickListener(v -> setRating(starIndex + 1));
            layoutStars.addView(stars[i]);
        }
        setRating(5);

        etReviewContent = findViewById(R.id.et_review_content);

        // 提交评价
        findViewById(R.id.btn_submit_review).setOnClickListener(v -> {
            String content = etReviewContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "请输入评价内容", Toast.LENGTH_SHORT).show();
                return;
            }
            // 持久化评价
            saveReview();
            Toast.makeText(this, "评价提交成功！感谢您的反馈", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveReview() {
        if (orderId == null) return;
        // 保存评价内容
        SharedPreferences prefs = getSharedPreferences("user_reviews", Context.MODE_PRIVATE);
        String userId = UserManager.getInstance().getUserId();
        String key = userId + "_" + orderId;
        String reviewData = rating + "|||" + etReviewContent.getText().toString().trim()
                + "|||" + System.currentTimeMillis();
        prefs.edit().putString(key, reviewData).apply();
        // 标记订单为"已评价"
        getSharedPreferences("order_reviewed", MODE_PRIVATE)
                .edit().putBoolean(orderId, true).apply();
    }

    /**
     * 外部查询某订单是否已评价
     */
    public static boolean isOrderReviewed(Context ctx, String orderId) {
        return ctx.getSharedPreferences("order_reviewed", Context.MODE_PRIVATE)
                .getBoolean(orderId, false);
    }

    private void setRating(int r) {
        rating = r;
        for (int i = 0; i < stars.length; i++) {
            stars[i].setImageResource(R.drawable.ic_profile_review);
            stars[i].setColorFilter(i < rating
                    ? Color.parseColor("#FFAA00")
                    : Color.parseColor("#DDDDDD"));
        }
    }

    private int dp2px(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
}
