package com.example.canteen.ui.post;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.data.entity.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 评论列表 Adapter
 */
public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public interface OnCommentLongClickListener {
        void onLongClick(Comment comment);
    }

    private final OnCommentLongClickListener longClickListener;

    public CommentAdapter(OnCommentLongClickListener listener) {
        super(DIFF_CALLBACK);
        this.longClickListener = listener;
    }

    private static final DiffUtil.ItemCallback<Comment> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment o, @NonNull Comment n) {
                return o.getId() == n.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull Comment o, @NonNull Comment n) {
                return o.getContent().equals(n.getContent());
            }
        };

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(getItem(position), longClickListener);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAuthor, tvContent, tvTime;
        private static final SimpleDateFormat SDF =
            new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor  = itemView.findViewById(R.id.tv_comment_author);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            tvTime    = itemView.findViewById(R.id.tv_comment_time);
        }

        void bind(Comment comment, OnCommentLongClickListener listener) {
            tvAuthor.setText(comment.getAuthorName());
            tvContent.setText(comment.getContent());
            tvTime.setText(SDF.format(new Date(comment.getCreatedAt())));
            itemView.setOnLongClickListener(v -> {
                listener.onLongClick(comment);
                return true;
            });
        }
    }
}
