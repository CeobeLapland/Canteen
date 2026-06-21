package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.canteen.R;
import com.example.canteen.data.entity.Recipe;
import com.example.canteen.data.repository.RecipeRepository;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecipeDetailFragment extends androidx.fragment.app.Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private TextView tvName, tvDesc, tvIngredients, tvSteps, tvStats;
    private MaterialButton btnLike, btnDislike;

    private RecipeRepository repository;
    private long recipeId = -1;

    public RecipeDetailFragment() {
        super(R.layout.fragment_recipe_detail);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            recipeId = args.getLong("recipeId", -1);
        }
        repository = RecipeRepository.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvName = view.findViewById(R.id.tvName);
        tvDesc = view.findViewById(R.id.tvDesc);
        tvIngredients = view.findViewById(R.id.tvIngredients);
        tvSteps = view.findViewById(R.id.tvSteps);
        tvStats = view.findViewById(R.id.tvStats);
        btnLike = view.findViewById(R.id.btnLike);
        btnDislike = view.findViewById(R.id.btnDislike);

        btnLike.setOnClickListener(v -> rate(true));
        btnDislike.setOnClickListener(v -> rate(false));

        loadDetail();
    }

    private void loadDetail() {
        if (recipeId <= 0) {
            Toast.makeText(requireContext(), "食谱参数错误", Toast.LENGTH_SHORT).show();
            return;
        }

        disposables.add(
                repository.getRecipeDetail(recipeId)
                        .subscribe(recipe -> {
                            bindRecipe(recipe);
                                },throwable -> {
                            Toast.makeText(requireContext(), "详情加载失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        //.subscribe(this::bindRecipe, throwable ->
                        //        Toast.makeText(requireContext(), "详情加载失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    private void bindRecipe(Recipe recipe) {
        tvName.setText(recipe.getName() == null ? "未命名食谱" : recipe.getName());
        tvDesc.setText(recipe.getDescription() == null ? "" : recipe.getDescription());
        tvIngredients.setText(formatIngredients(recipe.getIngredients()));
        tvSteps.setText(formatSteps(recipe.getSteps()));

        int likes = recipe.getLikes() == null ? 0 : recipe.getLikes();
        int dislikes = recipe.getDislikes() == null ? 0 : recipe.getDislikes();
        tvStats.setText("👍 " + likes + "   👎 " + dislikes);
    }

    private String formatIngredients(String ingredients) {
        if (TextUtils.isEmpty(ingredients)) return "暂无食材信息";
        String[] arr = ingredients.split(",");
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            if (!TextUtils.isEmpty(s.trim())) {
                sb.append("• ").append(s.trim()).append("\n");
            }
        }
        return sb.length() == 0 ? "暂无食材信息" : sb.toString().trim();
    }

    private String formatSteps(String steps) {
        if (TextUtils.isEmpty(steps)) return "暂无步骤信息";
        String[] arr = steps.split("\\n");
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (String s : arr) {
            if (!TextUtils.isEmpty(s.trim())) {
                sb.append(index++).append(". ").append(s.trim()).append("\n");
            }
        }
        return sb.length() == 0 ? "暂无步骤信息" : sb.toString().trim();
    }

    private void rate(boolean like) {
        if (recipeId <= 0) return;

        disposables.add(
                (like ? repository.likeRecipe(recipeId) : repository.dislikeRecipe(recipeId))
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            if (Boolean.TRUE.equals(success)) {
                                Toast.makeText(requireContext(), like ? "已点赞" : "已点踩", Toast.LENGTH_SHORT).show();
                                loadDetail();
                            } else {
                                Toast.makeText(requireContext(), "操作失败", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable ->
                                Toast.makeText(requireContext(), "操作失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}