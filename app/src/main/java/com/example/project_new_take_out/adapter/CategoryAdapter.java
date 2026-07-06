package com.example.project_new_take_out.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_new_take_out.R;
import com.example.project_new_take_out.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品分类 Adapter（店铺详情页左侧分类列表）
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private List<Category> categoryList = new ArrayList<>();
    private int selectedPosition = 0;
    private OnCategoryClickListener onCategoryClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        selectedPosition = 0;
        notifyDataSetChanged();
    }

    /**
     * 设置选中的分类位置
     */
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < categoryList.size() && position != selectedPosition) {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        // 选中状态切换
        boolean isSelected = position == selectedPosition;
        holder.itemView.setSelected(isSelected);
        holder.tvCategoryName.setTextColor(
                ContextCompat.getColor(context, isSelected ? R.color.colorPrimary : R.color.textHint));
        holder.indicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        View indicator;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            indicator = itemView.findViewById(R.id.view_indicator);
        }
    }
}
