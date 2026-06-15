package com.example.canteen.ui.food;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.data.entity.Food;

import java.util.ArrayList;
import java.util.List;
/**
 * 食品列表 RecyclerView Adapter
 * 继承 ListAdapter（基于 DiffUtil），只更新变化的条目，避免整列刷新闪烁。
 * 【已集成分页功能】
 */
public class FoodAdapter extends ListAdapter<Food, FoodAdapter.FoodViewHolder>
{
    /** 点击监听接口 */
    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    private final OnFoodClickListener clickListener;

    public FoodAdapter(OnFoodClickListener listener) {
        super(DIFF_CALLBACK);
        this.clickListener = listener;
        System.out.println("FoodAdapter initialized with listener: " + listener);
    }

    // ── DiffUtil 回调：告诉 ListAdapter 如何比较条目 ──────
    private static final DiffUtil.ItemCallback<Food> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Food>() {
            @Override
            public boolean areItemsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
                // 主键相同即为同一条目
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Food oldItem, @NonNull Food newItem) {
                // 内容比较（价格、评分等任意字段变化都触发局部刷新）
                return oldItem.getName().equals(newItem.getName());
                    //&& oldItem.getPrice() == newItem.getPrice()
                    //&& oldItem.getAverageRating() == newItem.getAverageRating()
                    //&& oldItem.getRatingCount() == newItem.getRatingCount();
            }
        };

    // ── ViewHolder 创建 & 绑定 ────────────────────────────
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {

        Food food = getItem(position);
        holder.bind(food, clickListener);
    }

    // ── ViewHolder ────────────────────────────────────────
    static class FoodViewHolder extends RecyclerView.ViewHolder {

        private final TextView   tvName;
        private final TextView   tvLocation;
        private final TextView   tvPrice;
        private final TextView   tvTags;
        private final RatingBar  ratingBar;
        private final TextView   tvRatingCount;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName       = itemView.findViewById(R.id.tv_food_name);
            tvLocation   = itemView.findViewById(R.id.tv_food_location);
            tvPrice      = itemView.findViewById(R.id.tv_food_price);
            tvTags       = itemView.findViewById(R.id.tv_food_tags);
            ratingBar    = itemView.findViewById(R.id.rating_bar);
            tvRatingCount = itemView.findViewById(R.id.tv_rating_count);
        }

        public void bind(Food food, OnFoodClickListener listener) {

            tvName.setText(food.getName());
            tvLocation.setText(food.getFullLocation());
            tvPrice.setText(String.format("¥%.1f", food.getPrice()));
            // tags已经被改成List<String>了，直接join一下就行了
            tvTags.setText(food.getTags() != null ? String.join("  ", food.getTags()) : "");
            ratingBar.setRating(food.getAverageRating());
            tvRatingCount.setText("(" + food.getRatingCount() + "人评价)");

            itemView.setOnClickListener(v -> listener.onFoodClick(food));
        }
    }

}