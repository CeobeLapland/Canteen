package com.example.canteen.controller;

import com.example.canteen.R;

import android.content.ClipData;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.canteen.data.entity.Ingredient;
import com.example.canteen.data.entity.IngredientType;
import com.example.canteen.data.repository.RecipeRepository;
import com.example.canteen.controller.adapter.IngredientCardAdapter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;

public class CookFragment extends Fragment implements IngredientCardAdapter.OnIngredientLongPressListener {

    private enum SourceArea {
        AVAILABLE,
        POT
    }

    private View rootView;
    private ChipGroup chipGroupTags;
    private RecyclerView rvIngredients;
    private RecyclerView rvPot;
    private LinearLayout layoutCustomInput;
    private EditText etCustomIngredient;
    private MaterialButton btnShowCustomInput;
    private MaterialButton btnConfirmCustom;
    private MaterialButton btnStartCooking;
    private TextView tvRecipe;

    private IngredientCardAdapter availableAdapter;
    private IngredientCardAdapter potAdapter;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final List<Ingredient> allIngredients = new ArrayList<>();
    private final List<Ingredient> potIngredients = new ArrayList<>();
    private final List<Ingredient> visibleIngredients = new ArrayList<>();

    private final EnumSet<IngredientType> selectedTypes = EnumSet.noneOf(IngredientType.class);
    private final Map<IngredientType, Chip> typeChipMap = new HashMap<>();

    private boolean suppressChipCallback = false;

    private Ingredient draggedIngredient = null;
    private SourceArea draggedFrom = null;

    private RecipeRepository repository;

    public CookFragment() {
        super(R.layout.fragment_cook);
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cook, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(RecipeRepository.getInstance() == null) {
            repository = new RecipeRepository(requireActivity().getApplication());
        } else {
            repository = RecipeRepository.getInstance();
        }

        bindViews(view);
        setupRecyclerViews();
        setupTags();
        setupActions();
        loadIngredients();
        setupDragTargets();
    }

    private void bindViews(View view) {
        chipGroupTags = view.findViewById(R.id.chipGroupTags);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        rvPot = view.findViewById(R.id.rvPot);
        layoutCustomInput = view.findViewById(R.id.layoutCustomInput);
        etCustomIngredient = view.findViewById(R.id.etCustomIngredient);
        btnShowCustomInput = view.findViewById(R.id.btnShowCustomInput);
        btnConfirmCustom = view.findViewById(R.id.btnConfirmCustom);
        btnStartCooking = view.findViewById(R.id.btnStartCooking);
        tvRecipe = view.findViewById(R.id.tvRecipe);
    }

    private void setupRecyclerViews() {
        FlexboxLayoutManager ingredientManager = new FlexboxLayoutManager(requireContext());
        ingredientManager.setFlexDirection(FlexDirection.ROW);
        ingredientManager.setFlexWrap(FlexWrap.WRAP);
        ingredientManager.setJustifyContent(JustifyContent.FLEX_START);

        FlexboxLayoutManager potManager = new FlexboxLayoutManager(requireContext());
        potManager.setFlexDirection(FlexDirection.ROW);
        potManager.setFlexWrap(FlexWrap.WRAP);
        potManager.setJustifyContent(JustifyContent.FLEX_START);

        availableAdapter = new IngredientCardAdapter(false, this);
        potAdapter = new IngredientCardAdapter(true, this);

        rvIngredients.setLayoutManager(ingredientManager);
        rvIngredients.setAdapter(availableAdapter);

        rvPot.setLayoutManager(potManager);
        rvPot.setAdapter(potAdapter);
    }

    private void setupTags() {
        chipGroupTags.removeAllViews();
        typeChipMap.clear();

        createTagChip("全部", null, true);
        createTagChip("蔬菜", IngredientType.VEGETABLE, false);
        createTagChip("肉类", IngredientType.MEAT, false);
        createTagChip("海鲜", IngredientType.SEAFOOD, false);
        createTagChip("乳制品", IngredientType.DAIRY, false);
        createTagChip("谷物", IngredientType.GRAIN, false);
        createTagChip("水果", IngredientType.FRUIT, false);
        createTagChip("其他", IngredientType.OTHER, false);
        createTagChip("自定义", IngredientType.CUSTOM, false);

        // 默认显示全部
        Chip allChip = (Chip) chipGroupTags.getChildAt(0);
        if (allChip != null) {
            allChip.setChecked(true);
        }
    }

    private void createTagChip(String text, final IngredientType type, final boolean isAllChip) {
        final Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setChipBackgroundColorResource(android.R.color.transparent);
        chip.setChipStrokeColorResource(android.R.color.transparent);
        chip.setTextColor(0xFF6D4C41);
        chip.setCheckedIconVisible(false);
        chip.setCloseIconVisible(false);
        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFFFFE0B2));
        chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(0xFFFFB74D));
        chip.setChipStrokeWidth(1f);

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (suppressChipCallback) return;

            if (isAllChip) {
                if (isChecked) {
                    suppressChipCallback = true;
                    for (int i = 0; i < chipGroupTags.getChildCount(); i++) {
                        View child = chipGroupTags.getChildAt(i);
                        if (child instanceof Chip && child != chip) {
                            ((Chip) child).setChecked(false);
                        }
                    }
                    suppressChipCallback = false;
                    selectedTypes.clear();
                    applyFilter();
                }
            } else {
                if (isChecked) {
                    Chip allChip = getAllChip();
                    if (allChip != null && allChip.isChecked()) {
                        suppressChipCallback = true;
                        allChip.setChecked(false);
                        suppressChipCallback = false;
                    }
                    selectedTypes.add(type);
                } else {
                    selectedTypes.remove(type);
                }
                applyFilter();
            }
        });

        chipGroupTags.addView(chip);

        if (!isAllChip && type != null) {
            typeChipMap.put(type, chip);
        }
    }

    private Chip getAllChip() {
        if (chipGroupTags.getChildCount() == 0) return null;
        View child = chipGroupTags.getChildAt(0);
        return child instanceof Chip ? (Chip) child : null;
    }

    private void setupActions() {
        btnShowCustomInput.setOnClickListener(v -> {
            layoutCustomInput.setVisibility(View.VISIBLE);
            etCustomIngredient.requestFocus();
        });

        btnConfirmCustom.setOnClickListener(v -> {
            final String name = etCustomIngredient.getText() == null ? "" : etCustomIngredient.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "请输入食材名称", Toast.LENGTH_SHORT).show();
                return;
            }

            disposables.add(
                    repository.addCustomIngredient(name)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(customIngredient -> {
                                allIngredients.add(customIngredient);
                                layoutCustomInput.setVisibility(View.GONE);
                                etCustomIngredient.setText("");

                                Chip customChip = typeChipMap.get(IngredientType.CUSTOM);
                                if (customChip != null) {
                                    suppressChipCallback = true;
                                    Chip allChip = getAllChip();
                                    if (allChip != null) allChip.setChecked(false);
                                    customChip.setChecked(true);
                                    suppressChipCallback = false;
                                    selectedTypes.add(IngredientType.CUSTOM);
                                }

                                applyFilter();
                                Toast.makeText(requireContext(), "已添加自定义食材", Toast.LENGTH_SHORT).show();
                            }, throwable -> Toast.makeText(requireContext(), "添加失败：" + throwable.getMessage(), Toast.LENGTH_SHORT).show())
            );
        });

        btnStartCooking.setOnClickListener(v -> {
            if (potIngredients.isEmpty()) {
                tvRecipe.setText("锅里还没有食材，先拖一些食材进去吧。");
                return;
            }

            tvRecipe.setText("正在生成食谱...");
            disposables.add(
                    repository.generateRecipe(new ArrayList<>(potIngredients))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(recipe -> tvRecipe.setText(recipe),
                                    throwable -> tvRecipe.setText("生成失败，先显示一个本地占位：\n\n" + buildLocalRecipeFallback()))
            );
        });
    }

    private void loadIngredients() {
        disposables.add(
                repository.loadAllIngredients()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            allIngredients.clear();
                            allIngredients.addAll(list);
                            applyFilter();
                        }, throwable -> {
                            tvRecipe.setText("加载食材失败，已使用本地示例数据。\n\n" + throwable.getMessage());
                            loadFallbackData();
                            applyFilter();
                        })
        );
    }

    private void loadFallbackData() {
        allIngredients.clear();
        allIngredients.add(new Ingredient(IngredientType.VEGETABLE, "土豆"));
        allIngredients.add(new Ingredient(IngredientType.VEGETABLE, "胡萝卜"));
        allIngredients.add(new Ingredient(IngredientType.VEGETABLE, "洋葱"));
        allIngredients.add(new Ingredient(IngredientType.MEAT, "鸡胸肉"));
        allIngredients.add(new Ingredient(IngredientType.MEAT, "牛肉片"));
        allIngredients.add(new Ingredient(IngredientType.SEAFOOD, "虾仁"));
        allIngredients.add(new Ingredient(IngredientType.DAIRY, "牛奶"));
        allIngredients.add(new Ingredient(IngredientType.GRAIN, "米饭"));
        allIngredients.add(new Ingredient(IngredientType.FRUIT, "番茄"));
        allIngredients.add(new Ingredient(IngredientType.OTHER, "鸡蛋"));
    }

    private void applyFilter() {
        visibleIngredients.clear();

        Chip allChip = getAllChip();
        boolean showAll = allChip != null && allChip.isChecked();

        if (showAll || selectedTypes.isEmpty()) {
            visibleIngredients.addAll(allIngredients);
        } else {
            for (Ingredient ingredient : allIngredients) {
                if (selectedTypes.contains(ingredient.type)) {
                    visibleIngredients.add(ingredient);
                }
            }
        }

        availableAdapter.setData(new ArrayList<>(visibleIngredients));
        potAdapter.setData(new ArrayList<>(potIngredients));
    }

    private void setupDragTargets() {
        rvIngredients.setOnDragListener((v, event) -> handleDragEvent(event, SourceArea.AVAILABLE));
        rvPot.setOnDragListener((v, event) -> handleDragEvent(event, SourceArea.POT));

        View potContainer = rootView.findViewById(R.id.cardPotContainer);
        if (potContainer != null) {
            potContainer.setOnDragListener((v, event) -> handleDragEvent(event, SourceArea.POT));
        }
    }

    private boolean handleDragEvent(DragEvent event, SourceArea targetArea) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return draggedIngredient != null;

            case DragEvent.ACTION_DRAG_ENTERED:
            case DragEvent.ACTION_DRAG_LOCATION:
            case DragEvent.ACTION_DRAG_EXITED:
                return true;

            case DragEvent.ACTION_DROP:
                if (draggedIngredient != null && draggedFrom != null) {
                    moveIngredient(draggedIngredient, draggedFrom, targetArea);
                    applyFilter();
                }
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                clearDragState();
                return true;

            default:
                return true;
        }
    }

    private void moveIngredient(Ingredient ingredient, SourceArea from, SourceArea to) {
        if (ingredient == null || from == to) return;

        if (from == SourceArea.AVAILABLE && to == SourceArea.POT) {
            if (allIngredients.remove(ingredient)) {
                potIngredients.add(ingredient);
            }
        } else if (from == SourceArea.POT && to == SourceArea.AVAILABLE) {
            if (potIngredients.remove(ingredient)) {
                allIngredients.add(ingredient);
            }
        }
    }

    private void clearDragState() {
        draggedIngredient = null;
        draggedFrom = null;
    }

    @Override
    public void onIngredientLongPressed(Ingredient ingredient, boolean fromPotArea, View anchorView) {
        draggedIngredient = ingredient;
        draggedFrom = fromPotArea ? SourceArea.POT : SourceArea.AVAILABLE;

        ClipData data = ClipData.newPlainText("ingredient", ingredient.name);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(anchorView);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ViewCompat.startDragAndDrop(anchorView, data, shadowBuilder, null, 0);
        } else {
            anchorView.startDrag(data, shadowBuilder, null, 0);
        }
    }

    private String buildLocalRecipeFallback() {
        StringBuilder sb = new StringBuilder();
        sb.append("【本地占位食谱】\n");
        for (Ingredient ingredient : potIngredients) {
            sb.append("· ").append(ingredient.name).append("\n");
        }
        sb.append("\n建议：先热锅，后下主料，再放配菜，最后调味。");
        return sb.toString();
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}