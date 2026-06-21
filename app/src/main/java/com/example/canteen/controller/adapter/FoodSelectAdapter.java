package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.controller.PostDraftStore;
import com.example.canteen.data.entity.Food;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


import com.example.canteen.R;
import java.util.ArrayList;
import java.util.List;

public class FoodSelectAdapter extends RecyclerView.Adapter<FoodSelectAdapter.VH> {

    private final List<Food> items = new ArrayList<>();
    private final PostDraftStore draftStore = PostDraftStore.get();

    public void submit(List<Food> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public List<Food> getCurrentSelectedFoods() {
        return draftStore.getSelectedFoods();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_select, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Food food = items.get(position);

        holder.tvName.setText(safe(food.getName(), "未命名食物"));
        holder.tvMeta.setText(safe(food.getCanteen(), "未知食堂") + " · " + safe(food.getWindow(), "未知窗口"));
        holder.tvPrice.setText("¥ " + safePrice(food.getPrice()) + "   评分 " + safeRating(food.getAverageRating()));

        boolean selected = draftStore.isFoodSelected(food.getId());
        holder.card.setStrokeColor(selected ? 0xFFFF9800 : 0xFFFFE2C2);
        holder.card.setCardBackgroundColor(selected ? 0xFFFFF7EF : 0xFFFFFFFF);
        holder.btnAdd.setText(selected ? "√" : "+");

        holder.btnAdd.setOnClickListener(v -> {
            draftStore.toggleFood(food);
            notifyItemChanged(position);
        });

        holder.card.setOnClickListener(v -> {
            draftStore.toggleFood(food);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String safe(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    private String safePrice(Integer price) {
        return price == null ? "-" : String.valueOf(price);
    }

    private String safeRating(Float rating) {
        return rating == null ? "-" : String.format("%.1f", rating);
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvName, tvMeta, tvPrice;
        MaterialButton btnAdd;

        VH(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardViewRoot); // 如果你不想再加 id，也可以删掉 card 的高亮逻辑
            tvName = itemView.findViewById(R.id.tvName);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}