package com.example.canteen.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.canteen.R;
import com.example.canteen.controller.adapter.PostCardAdapter;
import com.example.canteen.data.entity.FeedPostItem;
import com.example.canteen.data.entity.FeedType;
import com.example.canteen.data.repository.PostRepository;
import com.example.canteen.data.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class PostFeedFragment extends Fragment {

    private static final int PAGE_SIZE = 10;

    private ProfileRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private RecyclerView recyclerView;
    private View progressLoading;
    private TextView tvEmpty, tvTitle;
    private AppCompatButton btnBack;

    private PostCardAdapter adapter;
    private final List<FeedPostItem> data = new ArrayList<>();

    private FeedType feedType = FeedType.MY_POSTS;
    private int page = 0;
    private boolean isLoading = false;
    private boolean hasMore = true;

    public PostFeedFragment() {
        super(R.layout.fragment_post_feed);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //repository = ProfileRepository.getInstance();
        if (ProfileRepository.getInstance() == null)
            repository = new ProfileRepository(requireActivity().getApplication());
        else
            repository = ProfileRepository.getInstance();

        return inflater.inflate(R.layout.fragment_post_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        progressLoading = view.findViewById(R.id.progressLoading);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvTitle = view.findViewById(R.id.tvTitle);
        btnBack = view.findViewById(R.id.btnBack);

        String typeName = getArguments() != null ? getArguments().getString("feedType") : null;
        String title = getArguments() != null ? getArguments().getString("title") : null;

        if (typeName != null) {
            try {
                feedType = FeedType.valueOf(typeName);
            } catch (Exception ignore) {
                feedType = FeedType.MY_POSTS;
            }
        }

        tvTitle.setText(title != null ? title : feedType.getTitle());

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        adapter = new PostCardAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) return;

                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm == null) return;

                int lastVisible = lm.findLastVisibleItemPosition();
                int total = lm.getItemCount();

                if (!isLoading && hasMore && total > 0 && lastVisible >= total - 3) {
                    loadNextPage();
                }
            }
        });

        loadFirstPage();
    }

    private void loadFirstPage() {
        page = 0;
        hasMore = true;
        data.clear();
        adapter.submitList(new ArrayList<>(data));
        loadNextPage();
    }

    private void loadNextPage() {
        isLoading = true;
        progressLoading.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(data.isEmpty() ? View.GONE : View.GONE);

        disposables.add(
                repository.loadFeedPosts(feedType, page, PAGE_SIZE)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(items -> {
                            progressLoading.setVisibility(View.GONE);
                            isLoading = false;

                            if (items == null || items.isEmpty()) {
                                hasMore = false;
                                if (data.isEmpty()) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
                                return;
                            }

                            data.addAll(items);
                            adapter.submitList(new ArrayList<>(data));
                            page++;

                            if (items.size() < PAGE_SIZE) {
                                hasMore = false;
                            }

                            tvEmpty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                        }, throwable -> {
                            progressLoading.setVisibility(View.GONE);
                            isLoading = false;

                            if (data.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                tvEmpty.setText("加载失败");
                            }
                        })
        );
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}