package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.data.entity.Ingredient;
import com.example.canteen.data.entity.IngredientType;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class IngredientCardAdapter extends RecyclerView.Adapter<IngredientCardAdapter.VH> {

    public interface OnIngredientLongPressListener {
        void onIngredientLongPressed(Ingredient ingredient, boolean fromPotArea, View anchorView);
    }

    private final boolean fromPotArea;
    private final OnIngredientLongPressListener longPressListener;
    private final List<Ingredient> data = new ArrayList<>();

    public IngredientCardAdapter(boolean fromPotArea, OnIngredientLongPressListener longPressListener) {
        this.fromPotArea = fromPotArea;
        this.longPressListener = longPressListener;
        setHasStableIds(false);
    }

    public void setData(List<Ingredient> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    public Ingredient getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final Ingredient ingredient = data.get(position);
        holder.tvName.setText(ingredient.name);

        int bgColor = ingredient.type == IngredientType.CUSTOM ? 0xFFFFE0B2 : 0xFFFFF3E0;
        int strokeColor = ingredient.type == IngredientType.CUSTOM ? 0xFFE65100 : 0xFFFFB74D;

        holder.card.setCardBackgroundColor(bgColor);
        holder.card.setStrokeColor(strokeColor);

        holder.itemView.setContentDescription(ingredient.name);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longPressListener != null) {
                    longPressListener.onIngredientLongPressed(ingredient, fromPotArea, v);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView tvName;

        VH(@NonNull View itemView) {
            super(itemView);
            card = (MaterialCardView) itemView;
            tvName = itemView.findViewById(R.id.tvIngredientName);
        }
    }
}