package com.example.canteen.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * 帖子广场页面
 *
 * 显示全部帖子列表，右下角 FAB 发布新帖子。
 */
public class PostListFragment extends Fragment {

    //private PostViewModel viewModel;
    private PostAdapter   adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //viewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);

        // ── RecyclerView ──────────────────────────────────
        RecyclerView recyclerView = view.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PostAdapter(
            post -> {
                // 点击 → 打开帖子详情
                //viewModel.selectPost(post.getId());
                requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new PostDetailFragment())
                    .addToBackStack(null)   // 允许返回键回到列表
                    .commit();
            },
            post -> {
                // 长按 → 确认删除对话框
                new AlertDialog.Builder(requireContext())
                    .setTitle("删除帖子")
                    .setMessage("确定要删除这条帖子吗？")
                    //.setPositiveButton("删除", (d, w) -> viewModel.deletePost(post))
                    .setNegativeButton("取消", null)
                    .show();
            }
        );
        recyclerView.setAdapter(adapter);

        // ── 观察帖子列表 ──────────────────────────────────
        //viewModel.allPosts.observe(getViewLifecycleOwner(), posts -> {
        //    adapter.submitList(posts);
        //});

        // ── FAB：发布帖子 ─────────────────────────────────
        FloatingActionButton fab = view.findViewById(R.id.fab_new_post);
        fab.setOnClickListener(v -> showPublishDialog());
    }

    /**
     * 弹出发帖对话框（MVP 简化版：仅输入昵称、标题、内容）
     * 后续可替换为完整的 PostEditFragment
     */
    private void showPublishDialog() {
        View dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_new_post, null);

        TextInputEditText etAuthor  = dialogView.findViewById(R.id.et_author);
        TextInputEditText etTitle   = dialogView.findViewById(R.id.et_post_title);
        TextInputEditText etContent = dialogView.findViewById(R.id.et_post_content);

        new AlertDialog.Builder(requireContext())
            .setTitle("发布新帖子")
            .setView(dialogView)
            .setPositiveButton("发布", (d, w) -> {
                String author  = etAuthor.getText()  != null ? etAuthor.getText().toString().trim()  : "匿名";
                String title   = etTitle.getText()   != null ? etTitle.getText().toString().trim()   : "";
                String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";

                if (title.isEmpty() || content.isEmpty()) return;
                // foodId 传 null 表示非特定食品的帖子
                //viewModel.publishPost(author.isEmpty() ? "匿名" : author, title, content);
            })
            .setNegativeButton("取消", null)
            .show();
    }
}
