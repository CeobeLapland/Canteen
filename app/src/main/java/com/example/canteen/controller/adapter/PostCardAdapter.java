package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.canteen.R;
import com.example.canteen.data.entity.FeedPostItem;
import com.example.canteen.data.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class PostCardAdapter extends RecyclerView.Adapter<PostCardAdapter.VH> {

    private final List<FeedPostItem> items = new ArrayList<>();

    public void submitList(List<FeedPostItem> newList) {
        items.clear();
        if (newList != null) {
            items.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post_feed_card, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FeedPostItem item = items.get(position);
        Post post = item.getPost();

        holder.tvAuthor.setText("作者：" + safe(post.getAuthorName()));
        holder.tvTitle.setText(safe(post.getTitle()));
        holder.tvViewCount.setText("浏览量：" + safeInt(post.getViewCount()));
        holder.tvLikeCount.setText("点赞量：" + safeInt(post.getLikeCount()));
        holder.tvBrowseTime.setText("浏览时间：" + safe(item.getBrowseTimeText()));

        holder.itemView.setOnClickListener(v -> {
            // 先置空：以后你要进详情页，在这里接导航就行
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String safe(String s) {
        return s == null ? "-" : s;
    }

    private String safeInt(Integer value) {
        return value == null ? "0" : String.valueOf(value);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvTitle, tvViewCount, tvLikeCount, tvBrowseTime;

        VH(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvItemAuthor);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvViewCount = itemView.findViewById(R.id.tvItemViewCount);
            tvLikeCount = itemView.findViewById(R.id.tvItemLikeCount);
            tvBrowseTime = itemView.findViewById(R.id.tvItemBrowseTime);
        }
    }
}
