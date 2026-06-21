package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.canteen.R;
import com.example.canteen.controller.adapter.DetailPagerAdapter;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.repository.PostRepository;
import com.google.android.material.button.MaterialButton;


import java.time.format.DateTimeFormatter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PostDetailFragment extends Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private PostRepository postRepository;
    private long postId;

    private ViewPager2 vpDetail;
    private MaterialButton btnComments;
    private MaterialButton btnRelated;
    private MaterialButton btnLike;

    private android.widget.TextView tvTitle;
    private android.widget.TextView tvMeta;
    private android.widget.TextView tvContent;
    private android.widget.TextView tvLikeCount;
    private android.widget.TextView tvViewCount;

    private Post currentPost;
    private boolean liked = false;

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

        //postRepository = PostRepository.getInstance();
        if (PostRepository.getInstance() == null)
            postRepository = new PostRepository(requireActivity().getApplication());
        else
            postRepository = PostRepository.getInstance();

        postId = requireArguments().getLong("postId", -1L);

        bindViews(view);
        initPager();
        initButtons();


        loadPost();
    }

    private void bindViews(View view) {
        vpDetail = view.findViewById(R.id.vpDetail);
        btnComments = view.findViewById(R.id.btnComments);
        btnRelated = view.findViewById(R.id.btnRelated);
        btnLike = view.findViewById(R.id.btnLike);

        tvTitle = view.findViewById(R.id.tvTitle);
        tvMeta = view.findViewById(R.id.tvMeta);
        tvContent = view.findViewById(R.id.tvContent);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);
        tvViewCount = view.findViewById(R.id.tvViewCount);
    }

    private void initPager() {
        vpDetail.setAdapter(new DetailPagerAdapter(this, postId));
        vpDetail.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateSwitchButtons(position);
            }
        });
    }

    private void initButtons() {
        btnComments.setOnClickListener(v -> vpDetail.setCurrentItem(0, true));
        btnRelated.setOnClickListener(v -> vpDetail.setCurrentItem(1, true));

        btnLike.setOnClickListener(v -> {
            liked = !liked;
            updateLikeButton();
            Toast.makeText(requireContext(), liked ? "已点赞" : "已取消点赞", Toast.LENGTH_SHORT).show();

            // 如果你有后端点赞接口，可以在这里接 repository
            // postRepository.toggleLike(postId)...
        });
    }

    private void loadPost() {
        disposables.add(
                postRepository.getPostById(postId)
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(post -> {
                            currentPost = post;
                            bindPost(post);
                        }, throwable -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "加载帖子失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void bindPost(Post post) {
        tvTitle.setText(safe(post.getTitle(), "未命名帖子"));
        tvMeta.setText(buildMeta(post));
        tvContent.setText(safe(post.getContent(), "暂无内容"));
        tvLikeCount.setText("点赞 " + safeInt(post.getLikeCount()));
        tvViewCount.setText("浏览 " + safeInt(post.getViewCount()));
        updateLikeButton();
    }

    private String buildMeta(Post post) {
        String author = safe(post.getAuthorName(), "匿名");
        String time = "未知时间";
        if (post.getCreatedAt() != null) {
            try {
                time = post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception ignore) {
            }
        }
        return author + " · " + time;
    }

    private void updateLikeButton() {
        btnLike.setText(liked ? "已点赞" : "点赞");
    }

    private void updateSwitchButtons(int position) {
        boolean commentSelected = position == 0;
        btnComments.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                commentSelected ? 0xFFFF9800 : 0xFFFFF1E1
        ));
        btnComments.setTextColor(commentSelected ? 0xFFFFFFFF : 0xFFA85A00);

        btnRelated.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                commentSelected ? 0xFFFFF1E1 : 0xFFFF9800
        ));
        btnRelated.setTextColor(commentSelected ? 0xFFA85A00 : 0xFFFFFFFF);
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String safe(String value, String fallback) {
        return TextUtils.isEmpty(value) ? fallback : value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}