package com.example.canteen.controller;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.controller.adapter.RecipeAdapter;
import com.example.canteen.data.entity.Recipe;
import com.example.canteen.data.repository.RecipeRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecipeListFragment extends Fragment {

    private static final int PAGE_SIZE = 10;

    private EditText etSearch;
    private MaterialButton btnSearch;
    private ChipGroup chipGroupTags;
    private RecyclerView rvRecipes;
    private ProgressBar progressLoading;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final ArrayList<Recipe> data = new ArrayList<>();
    private RecipeAdapter adapter;

    private RecipeRepository repository;

    private String currentKeyword = null;
    private String selectedTag = null;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMore = true;

    public RecipeListFragment() {
        super(R.layout.fragment_recipe_list);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(RecipeRepository.getInstance() == null){
            repository = new RecipeRepository(requireActivity().getApplication());
        } else {
            repository = RecipeRepository.getInstance();
        }

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        chipGroupTags = view.findViewById(R.id.chipGroupTags);
        rvRecipes = view.findViewById(R.id.rvRecipes);
        progressLoading = view.findViewById(R.id.progressLoading);

        adapter = new RecipeAdapter(data, recipe -> {
            NavController navController = NavHostFragment.findNavController(this);
            Bundle bundle = new Bundle();
            bundle.putLong("recipeId", recipe.getId());
            navController.navigate(R.id.action_cookingFrameworkFragment_to_recipeDetailFragment, bundle);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        rvRecipes.setLayoutManager(layoutManager);
        rvRecipes.setAdapter(adapter);

        rvRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) return;

                if (!recyclerView.canScrollVertically(1)) {
                    loadNextPage();
                }
            }
        });

        btnSearch.setOnClickListener(v -> {
            currentKeyword = safeText(etSearch.getText() == null ? null : etSearch.getText().toString());
            reloadFirstPage();
        });

        loadTags();
        reloadFirstPage();
    }

    private void loadTags() {
        disposables.add(
                repository.getAllTags()
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tags -> {
                            if (tags == null || tags.isEmpty()) {
                                showFallbackTags();
                            } else {
                                buildTagChips(tags);
                            }
                        }, throwable -> {
                            showFallbackTags();
                        })
        );
    }

    private void showFallbackTags() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("家常");
        tags.add("快手");
        tags.add("低脂");
        tags.add("早餐");
        tags.add("午餐");
        tags.add("晚餐");
        tags.add("甜点");
        tags.add("素食");
        buildTagChips(tags);
    }

    private void buildTagChips(List<String> tags) {
        chipGroupTags.removeAllViews();

        for (String tag : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChecked(false);
            chip.setTextColor(0xFF7A3C00);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(android.R.color.transparent);

            //chip.setChipBackgroundColor(getResources().getColorStateList(R.color_chip_selector_dummy()));
            //Cannot resolve method 'color_chip_selector_dummy' in 'R'
            chip.setChipBackgroundColor(R_color_chip_selector_dummy());

            chip.setChipStrokeWidth(0f);
            chip.setCloseIconVisible(false);
            chip.setCheckable(true);
            chip.setTextSize(14f);
            chip.setPadding(8, 8, 8, 8);

            chip.setOnClickListener(v -> {
                boolean willSelect = !tag.equals(selectedTag);

                for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                    View child = chipGroupTags.getChildAt(i);
                    if (child instanceof Chip) {
                        ((Chip) child).setChecked(false);
                    }
                }

                if (willSelect) {
                    selectedTag = tag;
                    chip.setChecked(true);
                } else {
                    selectedTag = null;
                }

                reloadFirstPage();
            });

            chipGroupTags.addView(chip);
        }
    }

    private void reloadFirstPage() {
        currentPage = 1;
        hasMore = true;
        data.clear();
        adapter.notifyDataSetChanged();
        loadPage(currentPage, true);
    }

    private void loadNextPage() {
        if (isLoading || !hasMore) return;
        currentPage++;
        loadPage(currentPage, false);
    }

    private void loadPage(int page, boolean replace) {
        if (isLoading) return;

        isLoading = true;
        progressLoading.setVisibility(View.VISIBLE);

        String keywordParam = TextUtils.isEmpty(currentKeyword) ? null : currentKeyword;
        String tagParam = TextUtils.isEmpty(selectedTag) ? null : selectedTag;

        disposables.add(
                repository.searchRecipes(keywordParam, tagParam, page, PAGE_SIZE)
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(recipes -> {
                            if (replace) {
                                data.clear();
                            }
                            if (recipes != null) {
                                data.addAll(recipes);
                            }
                            adapter.notifyDataSetChanged();

                            hasMore = recipes != null && recipes.size() >= PAGE_SIZE;
                            isLoading = false;
                            progressLoading.setVisibility(View.GONE);
                        }, throwable -> {
                            isLoading = false;
                            progressLoading.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "加载失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private String safeText(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }

    // 这里是为了不额外写颜色文件，给 Chip 用一个简单 selector。
    // 代码里引用 getColorStateList 时需要这个方法返回一个可用的颜色列表。
    private int[][] dummyStates = new int[][]{
            new int[]{android.R.attr.state_checked},
            new int[]{}
    };
    private int[] dummyColors = new int[]{
            0xFFFFD08A,
            0xFFFFF9F1
    };

    private android.content.res.ColorStateList R_color_chip_selector_dummy() {
        return new android.content.res.ColorStateList(dummyStates, dummyColors);
    }
}
