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

public class PageNumberAdapter extends RecyclerView.Adapter<PageNumberAdapter.PageVH> {

    public interface OnPageClickListener {
        void onClick(int page);
    }

    private final List<Integer> pages = new ArrayList<>();
    private int selectedPage = 1;
    private OnPageClickListener listener;

    public void setOnPageClickListener(OnPageClickListener listener) {
        this.listener = listener;
    }

    public void submitPages(int totalPages, int currentPage) {
        pages.clear();
        selectedPage = Math.max(1, currentPage);

        int safeTotal = Math.max(1, totalPages);
        for (int i = 1; i <= safeTotal; i++) {
            pages.add(i);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_number, parent, false);
        return new PageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageVH holder, int position) {
        int page = pages.get(position);
        holder.tvPage.setText(String.valueOf(page));

        boolean selected = page == selectedPage;
        holder.cardPage.setCardBackgroundColor(selected ? 0xFFFF9800 : 0xFFFFF1E1);
        holder.cardPage.setStrokeColor(selected ? 0xFFFF9800 : 0xFFFFD2A3);
        holder.tvPage.setTextColor(selected ? 0xFFFFFFFF : 0xFFA85A00);

        holder.cardPage.setOnClickListener(v -> {
            if (listener != null) {
                selectedPage = page;
                notifyDataSetChanged();
                listener.onClick(page);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    static class PageVH extends RecyclerView.ViewHolder {
        MaterialCardView cardPage;
        TextView tvPage;

        PageVH(@NonNull View itemView) {
            super(itemView);
            cardPage = itemView.findViewById(R.id.cardPage);
            tvPage = itemView.findViewById(R.id.tvPage);
        }
    }
}