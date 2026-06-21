package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.canteen.data.entity.Food;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.example.canteen.R;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PostCreateFragment extends Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private TextInputEditText etAuthor;
    private TextInputEditText etTitle;
    private TextInputEditText etContent;
    private MaterialButton btnTypes;
    private MaterialButton btnFoods;
    private MaterialButton btnPublish;
    private android.widget.TextView tvTypesSummary;
    private android.widget.TextView tvFoodsSummary;

    private PostRepository postRepository;
    private final PostDraftStore draftStore = PostDraftStore.get();

    private final List<String> allTypes = Arrays.asList(
            "学习", "求助", "娱乐", "活动", "公告", "其他"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        postRepository = PostRepository.getInstance();

        etAuthor = view.findViewById(R.id.etAuthor);
        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        btnTypes = view.findViewById(R.id.btnTypes);
        btnFoods = view.findViewById(R.id.btnFoods);
        btnPublish = view.findViewById(R.id.btnPublish);
        tvTypesSummary = view.findViewById(R.id.tvTypesSummary);
        tvFoodsSummary = view.findViewById(R.id.tvFoodsSummary);

        restoreDraft();
        registerFoodResultListener();
        bindListeners();
        updateSummaries();

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NavHostFragment.findNavController(PostCreateFragment.this).popBackStack();
                    }
                }
        );
    }

    private void restoreDraft() {
        etAuthor.setText(draftStore.getAuthorName());
        etTitle.setText(draftStore.getTitle());
        etContent.setText(draftStore.getContent());
    }

    private void bindListeners() {
        btnTypes.setOnClickListener(v -> showTypeDialog());

        btnFoods.setOnClickListener(v -> {
            saveDraftFromInput();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_postCreateFragment_to_foodSelectFragment);
        });

        btnPublish.setOnClickListener(v -> publishPost());
    }

    private void showTypeDialog() {
        boolean[] checked = new boolean[allTypes.size()];
        List<String> selected = draftStore.getSelectedTypes();

        for (int i = 0; i < allTypes.size(); i++) {
            checked[i] = selected.contains(allTypes.get(i));
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("选择类型")
                .setMultiChoiceItems(allTypes.toArray(new String[0]), checked, (dialog, which, isChecked) -> {
                    String type = allTypes.get(which);
                    draftStore.toggleType(type);
                    checked[which] = isChecked;
                })
                .setPositiveButton("确定", (dialog, which) -> updateSummaries())
                .setNegativeButton("取消", null)
                .show();
    }

    private void registerFoodResultListener() {
        getParentFragmentManager().setFragmentResultListener(
                "food_select_result",
                getViewLifecycleOwner(),
                (requestKey, bundle) -> updateSummaries()
        );
    }

    private void saveDraftFromInput() {
        draftStore.setAuthorName(textOf(etAuthor));
        draftStore.setTitle(textOf(etTitle));
        draftStore.setContent(textOf(etContent));
    }

    private void updateSummaries() {
        saveDraftFromInput();

        List<String> types = draftStore.getSelectedTypes();
        if (types.isEmpty()) {
            tvTypesSummary.setText("已选类型：无");
            btnTypes.setText("选择类型");
        } else {
            tvTypesSummary.setText("已选类型：" + TextUtils.join("、", types));
            btnTypes.setText("选择类型（" + types.size() + "）");
        }

        List<Food> foods = draftStore.getSelectedFoods();
        if (foods.isEmpty()) {
            tvFoodsSummary.setText("已选食物：无");
            btnFoods.setText("关联食物");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < foods.size(); i++) {
                if (i > 0) sb.append("、");
                sb.append(foods.get(i).getName());
            }
            tvFoodsSummary.setText("已选食物：" + sb);
            btnFoods.setText("关联食物（" + foods.size() + "）");
        }
    }

    private void publishPost() {
        saveDraftFromInput();

        String author = draftStore.getAuthorName();
        String title = draftStore.getTitle();
        String content = draftStore.getContent();

        if (TextUtils.isEmpty(author) || TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(requireContext(), "请先填写发帖人、标题和内容", Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setAuthorName(author);
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setLikeCount(0);
        post.setViewCount(0);
        post.setCommentCount(0);

        List<String> types = new ArrayList<>(draftStore.getSelectedTypes());
        List<Long> foodIds = new ArrayList<>(draftStore.getSelectedFoodIds());

        disposables.add(
                postRepository.publishPost(post, types, foodIds)
                        .subscribe(success -> {
                            if (Boolean.TRUE.equals(success)) {
                                Toast.makeText(requireContext(), "发布成功", Toast.LENGTH_SHORT).show();
                                draftStore.clear();
                                NavHostFragment.findNavController(this).popBackStack();
                            } else {
                                Toast.makeText(requireContext(), "发布失败", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> Toast.makeText(requireContext(), "发布失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummaries();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}