package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.example.canteen.R;

import java.util.ArrayList;
import java.util.List;

public class ChannelTypeAdapter extends RecyclerView.Adapter<ChannelTypeAdapter.TypeVH> {

    public interface OnTypeSelectedListener {
        void onSelected(String type);
    }

    private final List<String> items = new ArrayList<>();
    private String selectedType = "全部";
    private OnTypeSelectedListener listener;

    public ChannelTypeAdapter(List<String> source, String defaultSelected) {
        if (source != null) {
            items.addAll(source);
        }
        if (defaultSelected != null) {
            selectedType = defaultSelected;
        }
    }

    public void setOnTypeSelectedListener(OnTypeSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TypeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_type, parent, false);
        return new TypeVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeVH holder, int position) {
        String item = items.get(position);
        holder.tvType.setText(item);

        boolean selected = item.equals(selectedType);
        holder.cardType.setCardBackgroundColor(selected ? 0xFFFF9800 : 0xFFFFF1E1);
        holder.cardType.setStrokeColor(selected ? 0xFFFF9800 : 0xFFFFD2A3);
        holder.tvType.setTextColor(selected ? 0xFFFFFFFF : 0xFFA85A00);

        holder.cardType.setOnClickListener(v -> {
            if (!item.equals(selectedType)) {
                selectedType = item;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onSelected(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TypeVH extends RecyclerView.ViewHolder {
        MaterialCardView cardType;
        TextView tvType;

        TypeVH(@NonNull View itemView) {
            super(itemView);
            cardType = itemView.findViewById(R.id.cardType);
            tvType = itemView.findViewById(R.id.tvType);
        }
    }
}
