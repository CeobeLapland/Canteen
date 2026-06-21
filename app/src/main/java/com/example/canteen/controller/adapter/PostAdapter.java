package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.example.canteen.R;
import com.example.canteen.data.entity.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostVH> {

    public interface OnPostClickListener {
        void onClick(Post post);
    }

    private final List<Post> items = new ArrayList<>();
    private OnPostClickListener listener;

    public void setOnPostClickListener(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Post> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_card, parent, false);
        return new PostVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostVH holder, int position) {
        Post post = items.get(position);

        holder.tvTitle.setText(safe(post.getTitle(), "未命名帖子"));
        holder.tvMeta.setText(buildMeta(post));
        holder.tvContent.setText(buildPreview(post.getContent()));
        holder.tvLike.setText("点赞 " + safeInt(post.getLikeCount()));
        holder.tvView.setText("浏览 " + safeInt(post.getViewCount()));
        holder.tvComment.setText("评论 " + safeInt(post.getCommentCount()));

        holder.cardPost.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String buildMeta(Post post) {
        String author = safe(post.getAuthorName(), "匿名");
        String time = "未知时间";
        LocalDateTime createdAt = post.getCreatedAt();
        if (createdAt != null) {
            try {
                time = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception ignore) {
            }
        }
        return author + " · " + time;
    }

    private String buildPreview(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "暂无内容";
        }
        String text = content.trim().replace("\n", " ");
        if (text.length() <= 120) {
            return text;
        }
        return text.substring(0, 120) + "...";
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String safe(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    static class PostVH extends RecyclerView.ViewHolder {
        MaterialCardView cardPost;
        TextView tvTitle;
        TextView tvMeta;
        TextView tvContent;
        TextView tvLike;
        TextView tvView;
        TextView tvComment;

        PostVH(@NonNull View itemView) {
            super(itemView);
            cardPost = itemView.findViewById(R.id.cardPost);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvView = itemView.findViewById(R.id.tvView);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}