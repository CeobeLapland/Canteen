package com.example.canteen.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.controller.adapter.RelatedPostAdapter;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.repository.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RelatedPostsFragment extends Fragment {

    private static final String ARG_POST_ID = "postId";

    public static RelatedPostsFragment newInstance(long postId) {
        RelatedPostsFragment fragment = new RelatedPostsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    private final CompositeDisposable disposables = new CompositeDisposable();

    private RecyclerView rvRelated;
    private RelatedPostAdapter adapter;
    private PostRepository postRepository;
    private long postId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_related_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //postRepository = PostRepository.getInstance();
        if (PostRepository.getInstance() == null)
            postRepository = new PostRepository(requireActivity().getApplication());
        else
            postRepository = PostRepository.getInstance();

        if(postRepository == null) {
            Toast.makeText(requireContext(), "无法加载相似推荐：数据仓库未初始化", Toast.LENGTH_SHORT).show();
            System.out.println("PostRepository is null in RelatedPostsFragment");
            //return;
        }
        postId = requireArguments().getLong(ARG_POST_ID, -1L);

        rvRelated = view.findViewById(R.id.rvRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RelatedPostAdapter();
        rvRelated.setAdapter(adapter);

        loadRelated();
    }

    private void loadRelated() {
        disposables.add(
                postRepository.getRelatedPosts(postId)
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(posts -> {
                            List<Post> topFive = new ArrayList<>();
                            if (posts != null) {
                                for (int i = 0; i < posts.size() && i < 5; i++) {
                                    topFive.add(posts.get(i));
                                }
                            }
                            adapter.submit(topFive);
                        }, throwable -> {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "加载相似推荐失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}
