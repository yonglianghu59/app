package com.example.project_new_take_out.ui.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_new_take_out.ui.main.MainActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券列表页面
 */
public class CouponListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        RecyclerView recyclerCoupon = findViewById(R.id.recycler_coupon);
        recyclerCoupon.setLayoutManager(new LinearLayoutManager(this));

        // 模拟优惠券数据
        List<Coupon> coupons = new ArrayList<>();
        coupons.add(new Coupon("新人专享券", "满50减15", "2025.07.31 到期"));
        coupons.add(new Coupon("周末特惠券", "满30减8", "2025.07.15 到期"));
        coupons.add(new Coupon("品质专享券", "满100减25", "2025.08.01 到期"));
        coupons.add(new Coupon("晚餐券", "满60减12", "2025.07.20 到期"));

        recyclerCoupon.setAdapter(new CouponAdapter(coupons));
    }

    static class Coupon {
        String title, desc, expire;
        Coupon(String t, String d, String e) { title = t; desc = d; expire = e; }
    }

    private class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.VH> {
        private final List<Coupon> list;
        CouponAdapter(List<Coupon> l) { this.list = l; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_coupon, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Coupon c = list.get(pos);
            h.tvTitle.setText(c.title);
            h.tvDesc.setText(c.desc);
            h.tvExpire.setText(c.expire);
            h.tvUse.setOnClickListener(v -> {
                Intent intent = new Intent(CouponListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDesc, tvExpire, tvUse;
            VH(@NonNull View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_coupon_title);
                tvDesc = v.findViewById(R.id.tv_coupon_desc);
                tvExpire = v.findViewById(R.id.tv_coupon_expire);
                tvUse = v.findViewById(R.id.tv_coupon_use);
            }
        }
    }
}
