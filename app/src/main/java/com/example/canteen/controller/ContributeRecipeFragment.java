package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.canteen.R;
import com.example.canteen.data.entity.Recipe;
import com.example.canteen.data.repository.RecipeRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ContributeRecipeFragment extends androidx.fragment.app.Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();

    private EditText etName, etDesc, etIngredients, etSteps, etTags;
    private MaterialButton btnSubmit;

    private RecipeRepository repository;

    public ContributeRecipeFragment() {
        super(R.layout.fragment_contribute_recipe);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //repository = RecipeRepository.getInstance();
        if(RecipeRepository.getInstance() == null) {
            repository = new RecipeRepository(requireActivity().getApplication());
        } else {
            repository = RecipeRepository.getInstance();
        }

        if(repository == null)
            System.out.println("RecipeRepository is null in ContributeRecipeFragment");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etName = view.findViewById(R.id.etName);
        etDesc = view.findViewById(R.id.etDesc);
        etIngredients = view.findViewById(R.id.etIngredients);
        etSteps = view.findViewById(R.id.etSteps);
        etTags = view.findViewById(R.id.etTags);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> submitRecipe());
    }

    private void submitRecipe() {
        String name = safe(etName.getText() == null ? null : etName.getText().toString());
        String desc = safe(etDesc.getText() == null ? null : etDesc.getText().toString());
        String ingredients = safe(etIngredients.getText() == null ? null : etIngredients.getText().toString());
        String steps = safe(etSteps.getText() == null ? null : etSteps.getText().toString());
        String tagsText = safe(etTags.getText() == null ? null : etTags.getText().toString());

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "请输入食谱名称", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setDescription(desc);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        recipe.setTags(splitTags(tagsText));
        recipe.setLikes(0);
        recipe.setDislikes(0);

        disposables.add(
                repository.submitRecipe(recipe)
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(id -> {
                            Toast.makeText(requireContext(), "提交成功", Toast.LENGTH_SHORT).show();
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }, throwable ->
                                Toast.makeText(requireContext(), "提交失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
        );
    }

    private List<String> splitTags(String text) {
        ArrayList<String> result = new ArrayList<>();
        if (TextUtils.isEmpty(text)) return result;

        String[] arr = text.split(",");
        for (String s : arr) {
            String t = s.trim();
            if (!TextUtils.isEmpty(t)) {
                result.add(t);
            }
        }
        return result;
    }

    private String safe(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}
