package com.example.canteen.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;

import com.google.android.material.textfield.TextInputEditText;

/**
 * 帖子详情页面
 *
 * 显示帖子完整内容、点赞按钮、评论列表、发表评论输入框。
 * 通过 PostViewModel.currentPost 获取当前帖子（由列表页通过 selectPost() 设置）。
 */
public class PostDetailFragment extends Fragment {

    //private PostViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 与 Activity 共享同一个 ViewModel（保证 currentPostId 状态一致）
        //viewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);

        // ── Views ──────────────────────────────────────────
        TextView tvTitle     = view.findViewById(R.id.tv_detail_post_title);
        TextView tvAuthor    = view.findViewById(R.id.tv_detail_post_author);
        TextView tvContent   = view.findViewById(R.id.tv_detail_post_content);
        TextView tvLikeCount = view.findViewById(R.id.tv_detail_like_count);
        Button   btnLike     = view.findViewById(R.id.btn_like);

        // ── 帖子基本信息 ───────────────────────────────────
        /*viewModel.currentPost.observe(getViewLifecycleOwner(), post -> {
            if (post == null) return;
            tvTitle.setText(post.getTitle());
            tvAuthor.setText("by " + post.getAuthorName());
            tvContent.setText(post.getContent());
            tvLikeCount.setText("👍 " + post.getLikeCount());

            btnLike.setOnClickListener(v -> {
                viewModel.likePost(post.getId());
            });
        });*/

        // ── 评论 RecyclerView ──────────────────────────────
        RecyclerView rvComments = view.findViewById(R.id.rv_comments);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        CommentAdapter commentAdapter = new CommentAdapter(comment -> {
            // 长按删除评论
            new AlertDialog.Builder(requireContext())
                .setTitle("删除评论")
                .setMessage("确定删除这条评论？")
                //.setPositiveButton("删除", (d, w) -> viewModel.deleteComment(comment))
                .setNegativeButton("取消", null)
                .show();
        });
        rvComments.setAdapter(commentAdapter);

        //viewModel.commentsOfCurrentPost.observe(getViewLifecycleOwner(), comments -> {
        //    commentAdapter.submitList(comments);
        //});

        // ── 发表评论 ──────────────────────────────────────
        TextInputEditText etCommentAuthor  = view.findViewById(R.id.et_comment_author);
        TextInputEditText etCommentContent = view.findViewById(R.id.et_comment_content);
        Button btnSubmitComment            = view.findViewById(R.id.btn_submit_comment);

        /*btnSubmitComment.setOnClickListener(v -> {
            String author  = etCommentAuthor.getText()  != null
                ? etCommentAuthor.getText().toString().trim()  : "匿名";
            String content = etCommentContent.getText() != null
                ? etCommentContent.getText().toString().trim() : "";

            if (content.isEmpty()) return;

            // 从 currentPost 中取 postId（安全起见也可以从 ViewModel 直接拿）
            if (viewModel.currentPost.getValue() != null) {
                int postId = viewModel.currentPost.getValue().getId();
                viewModel.addComment(postId, author.isEmpty() ? "匿名" : author, content);
                etCommentContent.setText("");   // 清空输入框
            }
        });*/
    }
}
