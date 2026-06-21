package com.example.canteen.controller;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.controller.adapter.ChannelTypeAdapter;
import com.example.canteen.controller.adapter.PageNumberAdapter;
import com.example.canteen.controller.adapter.PostAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.canteen.R;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.repository.PostRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChannelFragment extends Fragment {

    private static final int PAGE_SIZE = 20;

    private TextInputEditText etSearch;
    private MaterialButton btnSearch;
    private MaterialButton btnSortTime;
    private MaterialButton btnSortView;
    private MaterialButton btnSortLike;
    private MaterialButton btnOrder;
    private RecyclerView rvTypes;
    private RecyclerView rvPosts;
    private RecyclerView rvPages;
    private FloatingActionButton fabAddPost;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private PostRepository postRepository;
    private ChannelTypeAdapter typeAdapter;
    private PostAdapter postAdapter;
    private PageNumberAdapter pageAdapter;

    private String keyword = "";
    private String selectedType = "全部";
    private SortMode sortMode = SortMode.TIME;
    private boolean ascending = false; // 默认降序：最新在前
    private int currentPage = 1;
    private int totalPages = 1;

    private enum SortMode {
        TIME, VIEW, LIKE
    }

    private static class PageBundle {
        final List<Post> posts;
        final long totalCount;

        PageBundle(List<Post> posts, long totalCount) {
            this.posts = posts;
            this.totalCount = totalCount;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        System.out.println("ChannelFragment onCreateView");

        if (PostRepository.getInstance() == null)
            postRepository = new PostRepository(requireActivity().getApplication());
        else
            postRepository = PostRepository.getInstance();

        return inflater.inflate(R.layout.fragment_channel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //postRepository = new PostRepository(requireActivity().getApplication());

        bindViews(view);
        initRecyclerViews();
        initSortUi();
        initListeners();

        refreshPage(1);
    }

    private void bindViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSortTime = view.findViewById(R.id.btnSortTime);
        btnSortView = view.findViewById(R.id.btnSortView);
        btnSortLike = view.findViewById(R.id.btnSortLike);
        btnOrder = view.findViewById(R.id.btnOrder);
        rvTypes = view.findViewById(R.id.rvTypes);
        rvPosts = view.findViewById(R.id.rvPosts);
        rvPages = view.findViewById(R.id.rvPages);
        fabAddPost = view.findViewById(R.id.fabAddPost);
    }

    private void initRecyclerViews() {
        typeAdapter = new ChannelTypeAdapter(getDefaultTypes(), selectedType);
        rvTypes.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvTypes.setAdapter(typeAdapter);
        typeAdapter.setOnTypeSelectedListener(type -> {
            selectedType = type;
            refreshPage(1);
        });

        postAdapter = new PostAdapter();
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setAdapter(postAdapter);
        postAdapter.setOnPostClickListener(post -> navigateToPostDetail(post));

        pageAdapter = new PageNumberAdapter();
        rvPages.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        rvPages.setAdapter(pageAdapter);
        pageAdapter.setOnPageClickListener(this::refreshPage);
    }

    private void initSortUi() {
        updateSortButtons();
        updateOrderButton();
    }

    private void initListeners() {
        btnSearch.setOnClickListener(v -> applySearch());

        etSearch.setOnEditorActionListener((textView, actionId, event) -> {
            boolean isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH;
            boolean isEnterKey = event != null
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            if (isSearchAction || isEnterKey) {
                applySearch();
                return true;
            }
            return false;
        });

        btnSortTime.setOnClickListener(v -> {
            sortMode = SortMode.TIME;
            refreshPage(1);
        });

        btnSortView.setOnClickListener(v -> {
            sortMode = SortMode.VIEW;
            refreshPage(1);
        });

        btnSortLike.setOnClickListener(v -> {
            sortMode = SortMode.LIKE;
            refreshPage(1);
        });

        btnOrder.setOnClickListener(v -> {
            ascending = !ascending;
            refreshPage(1);
        });

        fabAddPost.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_channelFragment_to_postCreateFragment);
        });
    }

    private void applySearch() {
        String input = etSearch.getText() == null ? "" : etSearch.getText().toString().trim();
        keyword = input;
        refreshPage(1);
    }

    private void refreshPage(int page) {
        disposables.clear();

        Single<List<Post>> loadPageSingle = postRepository.loadChannelPage(
                page,
                PAGE_SIZE,
                keyword,
                selectedType,
                sortMode.name(),
                ascending
        );

        Single<Long> countSingle = postRepository.countChannelPosts(keyword, selectedType);

        disposables.add(
                Single.zip(
                                loadPageSingle.subscribeOn(Schedulers.io()),
                                countSingle.subscribeOn(Schedulers.io()),
                                PageBundle::new
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bundle -> {
                            currentPage = page;
                            postAdapter.submitList(bundle.posts);

                            long safeCount = Math.max(0L, bundle.totalCount);
                            totalPages = (int) Math.max(1L, (safeCount + PAGE_SIZE - 1L) / PAGE_SIZE);

                            pageAdapter.submitPages(totalPages, currentPage);
                            updateSortButtons();
                            updateOrderButton();

                            if (rvPosts.getLayoutManager() != null) {
                                rvPosts.getLayoutManager().scrollToPosition(0);
                            }
                        }, throwable -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "加载失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void updateSortButtons() {
        setSortButtonState(btnSortTime, sortMode == SortMode.TIME);
        setSortButtonState(btnSortView, sortMode == SortMode.VIEW);
        setSortButtonState(btnSortLike, sortMode == SortMode.LIKE);
    }

    private void updateOrderButton() {
        btnOrder.setText(ascending ? "升序" : "降序");
        btnOrder.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.orange_deep_fallback)
        ));
        btnOrder.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
    }

    private void setSortButtonState(MaterialButton button, boolean selected) {
        if (selected) {
            button.setBackgroundTintList(ColorStateList.valueOf(0xFFFF9800));
            button.setTextColor(0xFFFFFFFF);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(0xFFFFF1E1));
            button.setTextColor(0xFFA85A00);
        }
    }

    private List<String> getDefaultTypes() {
        return new ArrayList<>(Arrays.asList(
                "全部",
                "分享好吃的",
                "求助",
                "新品上市",
                "活动",
                "公告",
                "食品安全",
                "其他"
        ));
    }

    private void navigateToPostDetail(Post post) {
        Bundle args = new Bundle();
        args.putLong("postId", post.getId());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_channelFragment_to_postDetailFragment, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}