package com.example.canteen.controller.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.data.entity.Comment;
import com.google.android.material.button.MaterialButton;

import com.example.canteen.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    public interface OnLikeClickListener {
        void onLikeClick(Comment comment);
    }

    private final List<Comment> items = new ArrayList<>();
    private final OnLikeClickListener likeClickListener;

    public CommentAdapter(OnLikeClickListener likeClickListener) {
        this.likeClickListener = likeClickListener;
    }

    public CommentAdapter(){
        this.likeClickListener = null;
    }

    public void submitList(List<Comment> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = items.get(position);

        holder.tvName.setText(comment.getAuthorName());
        holder.tvContent.setText(comment.getContent());
        holder.btnLike.setText(String.format(Locale.getDefault(), "点赞 %d", comment.getLikeCount()));

        holder.btnLike.setOnClickListener(v -> {
            if (likeClickListener != null) {
                likeClickListener.onLikeClick(comment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvContent;
        MaterialButton btnLike;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_comment_name);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            btnLike = itemView.findViewById(R.id.btn_comment_like);
        }
    }
}