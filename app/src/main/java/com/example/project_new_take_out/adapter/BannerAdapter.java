package com.example.project_new_take_out.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;

/**
 * 首页 Banner 轮播 Adapter（ViewPager2 使用）
 * 开发阶段展示静态运营活动
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private final Context context;
    private final int[] bannerImages = {
            R.drawable.banner_home, // Pexels 免费图片
            R.drawable.banner_home,
            R.drawable.banner_home
    };

    private final String[] bannerTitles = {"周末特惠", "新店开业", "限时抢购"};
    private final String[] bannerSubtitles = {"周末美食狂欢", "首单立减10元", "每日特价菜品"};
    private final String[] bannerDescs = {"精选美味 5 折起", "来尝鲜吧", "手慢无"};

    public BannerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int index = position % bannerImages.length;
        holder.ivBanner.setImageResource(bannerImages[index]);
        holder.tvTitle.setText(bannerTitles[index]);
        holder.tvSubtitle.setText(bannerSubtitles[index]);
        holder.tvDesc.setText(bannerDescs[index]);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // 无限循环
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
            tvSubtitle = itemView.findViewById(R.id.tv_banner_subtitle);
            tvDesc = itemView.findViewById(R.id.tv_banner_desc);
        }
    }
}
