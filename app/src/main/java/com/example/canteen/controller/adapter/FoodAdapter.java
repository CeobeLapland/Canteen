package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.data.entity.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(Food food);
    }

    private final List<Food> items = new ArrayList<>();
    private final OnFoodClickListener listener;

    public FoodAdapter(OnFoodClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Food> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = items.get(position);

        holder.tvName.setText(food.getName());
        holder.tvDesc.setText(food.getDescription());
        holder.tvLocation.setText(food.getCampus() + " · " + food.getCanteen() + " · " + food.getFloor() + " · " + food.getWindow());
        //holder.tvPrice.setText(String.format(Locale.getDefault(), "¥ %.1f", food.getPrice()));
        holder.tvPrice.setText("价格："+food.getPrice().toString());
        holder.tvRating.setText(String.format(Locale.getDefault(), "评分 %.1f  |  %d 人评价", food.getAverageRating(), food.getRatingCount()));

        //String tagText = buildTagText(food.getTags());
        //holder.tvTags.setText(tagText);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String buildTagText(List<String> tags) {
        if (tags == null || tags.isEmpty()) return "暂无标签";
        int max = Math.min(tags.size(), 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < max; i++) {
            if (i > 0) sb.append(" · ");
            sb.append(tags.get(i));
        }
        if (tags.size() > max) sb.append(" · ...");
        return sb.toString();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDesc;
        TextView tvLocation;
        TextView tvPrice;
        TextView tvRating;
        TextView tvTags;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_food_name);
            tvDesc = itemView.findViewById(R.id.tv_food_desc);
            tvLocation = itemView.findViewById(R.id.tv_food_location);
            tvPrice = itemView.findViewById(R.id.tv_food_price);
            tvRating = itemView.findViewById(R.id.tv_food_rating);
            tvTags = itemView.findViewById(R.id.tv_food_tags);
        }
    }
}