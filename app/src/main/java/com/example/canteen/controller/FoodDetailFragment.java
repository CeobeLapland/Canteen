package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.controller.adapter.CommentAdapter;
import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Locale;

public class FoodDetailFragment extends Fragment implements CommentAdapter.OnLikeClickListener {

    private static final String ARG_FOOD = "arg_food";

    private Food food;
    private FoodRepository repository;

    private TextView tvName;
    private TextView tvMeta;
    private TextView tvDesc;
    private TextView tvTags;
    private TextView tvPrice;
    private TextView tvRating;
    private TextView tvSellTime;
    private LinearLayout relatedPostsContainer;

    private TextInputEditText etComment;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;

    public static FoodDetailFragment newInstance(Food food) {
        FoodDetailFragment fragment = new FoodDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FOOD, food);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_food_detail, container, false);

        repository = FoodRepository.getInstance();
        food = getArguments() == null ? null : getArguments().getParcelable(ARG_FOOD);

        MaterialButton btnBack = root.findViewById(R.id.btn_back);
        tvName = root.findViewById(R.id.tv_detail_name);
        tvMeta = root.findViewById(R.id.tv_detail_meta);
        tvDesc = root.findViewById(R.id.tv_detail_desc);
        tvTags = root.findViewById(R.id.tv_detail_tags);
        tvPrice = root.findViewById(R.id.tv_detail_price);
        tvRating = root.findViewById(R.id.tv_detail_rating);
        tvSellTime = root.findViewById(R.id.tv_detail_sell_time);
        relatedPostsContainer = root.findViewById(R.id.related_posts_container);

        etComment = root.findViewById(R.id.et_comment_input);
        rvComments = root.findViewById(R.id.rv_comments);
        MaterialButton btnPost = root.findViewById(R.id.btn_post_comment);

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        setupFoodInfo();
        setupRelatedPosts();
        setupComments();

        btnPost.setOnClickListener(v -> {
            String content = etComment.getText() == null ? "" : etComment.getText().toString().trim();
            if (TextUtils.isEmpty(content) || food == null) {
                return;
            }
            repository.addComment(food.getId(), content);
            //先生成一个弹窗提示未联网失败，后面再改成真正的网络请求结果反馈
             new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("服务器错误")
                    .setMessage("发不了喵")
                    .setPositiveButton("确定", null)
                    .show();

            etComment.setText("");
            refreshComments();
        });

        return root;
    }

    private void setupFoodInfo() {
        if (food == null) {
            tvName.setText("未找到食物");
            tvMeta.setText("");
            tvDesc.setText("");
            tvTags.setText("");
            tvPrice.setText("");
            tvRating.setText("");
            tvSellTime.setText("");
            return;
        }

        tvName.setText(food.getName());
        tvMeta.setText(food.getCampus() + " · " + food.getCanteen() + " · " + food.getFloor() + " · " + food.getWindow());
        tvDesc.setText(food.getDescription());
        tvTags.setText("标签：");// + joinTags(food.getTags()));
        //tvPrice.setText(String.format(Locale.getDefault(), "价格：¥ %.1f", food.getPrice()));
        tvPrice.setText("价格："+food.getPrice().toString());
        tvRating.setText(String.format(Locale.getDefault(), "评分：%.1f（%d）", food.getAverageRating(), food.getRatingCount()));
        tvSellTime.setText("售卖时间：" + food.getSellTime());
    }

    private void setupRelatedPosts() {
        relatedPostsContainer.removeAllViews();

        List<String> posts = food == null ? null : repository.getRelatedPosts(food.getId());
        if (posts == null || posts.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("这里先作为“与此食物相关的帖子”占位区，后面再接帖子列表。");
            empty.setTextColor(getResources().getColor(R.color.text_secondary, null));
            relatedPostsContainer.addView(empty);
            return;
        }

        for (String post : posts) {
            TextView tv = new TextView(requireContext());
            tv.setText("• " + post);
            tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
            tv.setTextSize(14f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = 8;
            relatedPostsContainer.addView(tv, lp);
        }
    }

    private void setupComments() {
        commentAdapter = new CommentAdapter(this);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setAdapter(commentAdapter);
        rvComments.setNestedScrollingEnabled(false);
        refreshComments();
    }

    private void refreshComments() {
        if (food == null) {
            commentAdapter.submitList(null);
            return;
        }
        commentAdapter.submitList(repository.getComments(food.getId()));
    }

    @Override
    public void onLikeClick(Comment comment) {
        if (food == null) return;
        repository.likeComment(food.getId(), comment.getId());
        //生成一个弹窗提示未联网失败，后面再改成真正的网络请求结果反馈
         new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("服务器错误")
                .setMessage("作者收到了你的赞，但是服务器没收到喵")
                .setPositiveButton("确定", null)
                .show();
        refreshComments();
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return "暂无";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) sb.append(" / ");
            sb.append(tags.get(i));
        }
        return sb.toString();
    }
}