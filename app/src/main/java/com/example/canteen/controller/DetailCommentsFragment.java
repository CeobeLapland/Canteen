package com.example.canteen.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.canteen.controller.adapter.CommentAdapter;
import com.example.canteen.R;
import com.example.canteen.data.entity.Comment;

import java.util.ArrayList;
import java.util.List;

public class DetailCommentsFragment extends Fragment {

    private static final String ARG_POST_ID = "postId";

    public static DetailCommentsFragment newInstance(long postId) {
        DetailCommentsFragment fragment = new DetailCommentsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView rvComments;
    private CommentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvComments = view.findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CommentAdapter();
        rvComments.setAdapter(adapter);

        loadComments();
    }

    private void loadComments() {
        // 这里先做成示例数据；你后面换成自己的评论 repository 即可
        List<Comment> demo = new ArrayList<>();
        demo.add(new Comment(null,null,"小刻", "来自作者的一条评论"));
        demo.add(new Comment(null,null,"小刻", "来自作者的两条评论"));
        //demo.add(new Comment(null,null,"Echo", "内容写得很清楚。"));
        adapter.submitList(demo);
    }
}