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
import com.example.canteen.data.entity.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 帖子列表 Adapter
 */
public class PostAdapter extends ListAdapter<Post, PostAdapter.PostViewHolder> {

    public interface OnPostClickListener    { void onClick(Post post); }
    public interface OnPostLongClickListener { void onLongClick(Post post); }

    private final OnPostClickListener     clickListener;
    private final OnPostLongClickListener longClickListener;

    public PostAdapter(OnPostClickListener click, OnPostLongClickListener longClick) {
        super(DIFF_CALLBACK);
        this.clickListener     = click;
        this.longClickListener = longClick;
    }

    private static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<Post>() {
            @Override
            public boolean areItemsTheSame(@NonNull Post o, @NonNull Post n) {
                return o.getId() == n.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull Post o, @NonNull Post n) {
                return o.getLikeCount()    == n.getLikeCount()
                    && o.getCommentCount() == n.getCommentCount()
                    && o.getTitle().equals(n.getTitle());
            }
        };

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(getItem(position), clickListener, longClickListener);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle, tvAuthor, tvTime,
                               tvLikeCount, tvCommentCount, tvPreview;

        private static final SimpleDateFormat SDF =
            new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle        = itemView.findViewById(R.id.tv_post_title);
            tvAuthor       = itemView.findViewById(R.id.tv_post_author);
            tvTime         = itemView.findViewById(R.id.tv_post_time);
            tvLikeCount    = itemView.findViewById(R.id.tv_post_like);
            tvCommentCount = itemView.findViewById(R.id.tv_post_comment);
            tvPreview      = itemView.findViewById(R.id.tv_post_preview);
        }

        void bind(Post post,
                  OnPostClickListener click,
                  OnPostLongClickListener longClick) {
            tvTitle.setText(post.getTitle());
            tvAuthor.setText(post.getAuthorName());
            tvTime.setText(SDF.format(new Date(post.getCreatedAt())));
            tvLikeCount.setText("👍 " + post.getLikeCount());
            tvCommentCount.setText("💬 " + post.getCommentCount());

            // 正文预览：最多显示 60 字
            String content = post.getContent();
            tvPreview.setText(content.length() > 60 ? content.substring(0, 60) + "…" : content);

            itemView.setOnClickListener(v -> click.onClick(post));
            itemView.setOnLongClickListener(v -> {
                longClick.onLongClick(post);
                return true;
            });
        }
    }
}
