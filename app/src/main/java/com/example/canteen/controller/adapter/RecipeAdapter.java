package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.data.entity.Recipe;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final ArrayList<Recipe> data;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(ArrayList<Recipe> data, OnRecipeClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Recipe recipe = data.get(position);

        holder.tvName.setText(recipe.getName() == null ? "未命名食谱" : recipe.getName());
        holder.tvDesc.setText(recipe.getDescription() == null ? "" : recipe.getDescription());

        holder.tvTags.setText(buildTagText(recipe.getTags()));
        int likes = recipe.getLikes() == null ? 0 : recipe.getLikes();
        int dislikes = recipe.getDislikes() == null ? 0 : recipe.getDislikes();
        holder.tvLikeInfo.setText("👍 " + likes + "   👎 " + dislikes);

        holder.card.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    private String buildTagText(List<String> tags) {
        if (tags == null || tags.isEmpty()) return "无标签";
        StringBuilder sb = new StringBuilder();
        int count = Math.min(tags.size(), 3);
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(" · ");
            sb.append(tags.get(i));
        }
        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvName, tvDesc, tvTags, tvLikeInfo;

        VH(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardRecipe);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTags = itemView.findViewById(R.id.tvTags);
            tvLikeInfo = itemView.findViewById(R.id.tvLikeInfo);
        }
    }
}