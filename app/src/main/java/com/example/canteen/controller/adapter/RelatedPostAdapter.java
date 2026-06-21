package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.canteen.R;
import com.example.canteen.data.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class RelatedPostAdapter extends RecyclerView.Adapter<RelatedPostAdapter.VH> {

    private final List<Post> items = new ArrayList<>();

    public void submit(List<Post> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related_post, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Post post = items.get(position);
        holder.tvTitle.setText(post.getTitle() == null ? "未命名帖子" : post.getTitle());
        holder.tvMeta.setText((post.getAuthorName() == null ? "匿名" : post.getAuthorName()) +
                " · 赞 " + safeInt(post.getLikeCount()) + " · 浏览 " + safeInt(post.getViewCount()));
        holder.tvContent.setText(post.getContent() == null ? "暂无内容" : post.getContent());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMeta, tvContent;

        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvContent = itemView.findViewById(R.id.tvContent);
        }
    }
}
