package com.example.canteen.controller;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.controller.adapter.FoodInputAdapter;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;

public class FoodWheelFragment extends Fragment {

    private static final int MAX_ROWS = 12;

    private FoodRepository repository; // 由你的项目注入
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Random random = new Random();

    private FoodWheelView wheelView;
    private TextView tvPreview;
    private ChipGroup chipGroup;
    private RecyclerView rvFoods;
    private Button btnAdd;
    private Button btnClear;
    private Button btnRandom;
    private Button btnExtract;

    private FoodInputAdapter adapter;

    // 输入行：名称 + 对应 Food 对象（若是手动输入，则 Food 可能为 null）
    private final ArrayList<String> rowNames = new ArrayList<>();
    private final ArrayList<Food> rowFoods = new ArrayList<>();

    // 当前转盘可显示的数据
    private final ArrayList<WheelItem> wheelItems = new ArrayList<>();

    public FoodWheelFragment() {
        // 空构造
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_wheel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = FoodRepository.getInstance();

        wheelView = view.findViewById(R.id.wheelView);
        tvPreview = view.findViewById(R.id.tvPreview);
        chipGroup = view.findViewById(R.id.chipGroup);
        rvFoods = view.findViewById(R.id.rvFoods);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnClear = view.findViewById(R.id.btnClear);
        btnRandom = view.findViewById(R.id.btnRandom);
        btnExtract = view.findViewById(R.id.btnExtract);

        initTags();
        initRows();
        initRecycler();
        bindActions();

        refreshWheelAndPreview();
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }

    private void initTags() {
        // 你可以替换成你的真实 tags 数据源
        List<String> tags = Arrays.asList(
                "辣", "甜", "咸", "清淡", "米饭", "面食", "粉面",
                "早餐", "午餐", "晚餐", "夜宵", "低脂", "高蛋白",
                "学生最爱", "汤类", "盖饭", "快餐", "素食", "重口味"
        );

        chipGroup.removeAllViews();
        for (String tag : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChecked(false);
            chip.setClickable(true);
            chip.setTextSize(14f);
            chip.setChipStartPadding(16f);
            chip.setChipEndPadding(16f);
            chip.setCloseIconVisible(false);

            //chip.setCheckIconVisible(false);
            //Cannot resolve method 'setCheckIconVisible' in 'Chip'
            //chip.setCheckedIconResource(R.drawable.ic_check); // 你需要提供一个 check 图标资源

            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(android.R.color.holo_orange_light);
            chip.setChipStrokeWidth(2f);
            chip.setTextColor(0xFF8A4B00);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> refreshWheelAndPreview());
            chipGroup.addView(chip);
        }
    }

    private void initRows() {
        rowNames.clear();
        rowFoods.clear();
        rowNames.add("");
        rowFoods.add(null);
    }

    private void initRecycler() {
        adapter = new FoodInputAdapter(rowNames, new FoodInputAdapter.Callback() {
            @Override
            public void onTextChanged(int position, @NonNull String text) {
                ensureRowCapacity(position);
                rowNames.set(position, text);
                rowFoods.set(position, null); // 手动输入时，Food 对象置空
                refreshWheelAndPreview();
            }

            @Override
            public void onDeleteClicked(int position) {
                deleteRow(position);
            }
        });

        rvFoods.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFoods.setNestedScrollingEnabled(false);
        rvFoods.setAdapter(adapter);
    }

    private void bindActions() {
        btnAdd.setOnClickListener(v -> addRow());
        btnClear.setOnClickListener(v -> clearAllRows());

        btnRandom.setOnClickListener(v -> loadFoodAndFill(false));
        btnExtract.setOnClickListener(v -> loadFoodAndFill(true));
    }

    private void addRow() {
        if (rowNames.size() >= MAX_ROWS) {
            toast("最多保留 12 排");
            return;
        }
        rowNames.add("");
        rowFoods.add(null);
        adapter.notifyItemInserted(rowNames.size() - 1);
        refreshWheelAndPreview();
        rvFoods.post(() -> rvFoods.smoothScrollToPosition(rowNames.size() - 1));
    }

    private void deleteRow(int position) {
        if (rowNames.size() <= 1) {
            toast("至少保留一排");
            return;
        }
        if (position < 0 || position >= rowNames.size()) return;

        rowNames.remove(position);
        rowFoods.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, rowNames.size() - position);
        refreshWheelAndPreview();
    }

    private void clearAllRows() {
        for (int i = 0; i < rowNames.size(); i++) {
            rowNames.set(i, "");
            rowFoods.set(i, null);
        }
        adapter.notifyDataSetChanged();
        refreshWheelAndPreview();
        tvPreview.setText("已清空，等待抽取结果");
    }

    private void ensureRowCapacity(int position) {
        while (rowNames.size() <= position) {
            rowNames.add("");
            rowFoods.add(null);
        }
    }

    private List<String> getSelectedTags() {
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View child = chipGroup.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    tags.add(chip.getText().toString());
                }
            }
        }
        return tags;
    }

    private void loadFoodAndFill(boolean animateAfterFill) {
        if (repository == null) {
            toast("repository 还没注入到 Fragment");
            return;
        }

        List<String> tags = getSelectedTags();

        disposables.add(
                repository.getFoodForTable(tags)
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(foods -> {
                            if (foods == null) foods = Collections.emptyList();

                            fillEmptyRowsWithFoods(foods);
                            refreshWheelAndPreview();

                            if (animateAfterFill) {
                                startExtractAnimation();
                            } else {
                                toast("已补全空位");
                            }
                        }, throwable -> toast("获取食物失败：" + throwable.getMessage()))
        );
    }

    private void fillEmptyRowsWithFoods(@NonNull List<Food> foods) {
        int foodIndex = 0;
        for (int i = 0; i < rowNames.size() && foodIndex < foods.size(); i++) {
            String current = rowNames.get(i);
            if (TextUtils.isEmpty(current)) {
                Food food = foods.get(foodIndex++);
                rowFoods.set(i, food);
                rowNames.set(i, safeFoodName(food));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void refreshWheelAndPreview() {
        rebuildWheelItems();
        wheelView.setLabels(extractWheelLabels(wheelItems));
        if (wheelItems.isEmpty()) {
            tvPreview.setText("暂无可抽取数据");
        } else if (TextUtils.isEmpty(tvPreview.getText())) {
            tvPreview.setText("点击「随机」补全空位，或点击「抽取」开始转盘");
        }
    }

    private void rebuildWheelItems() {
        wheelItems.clear();
        for (int i = 0; i < rowNames.size(); i++) {
            String name = rowNames.get(i);
            if (!TextUtils.isEmpty(name)) {
                wheelItems.add(new WheelItem(name, rowFoods.get(i), i));
                if (wheelItems.size() == MAX_ROWS) break;
            }
        }
        if (wheelItems.isEmpty()) {
            wheelItems.add(new WheelItem("", null, -1));
        }
    }

    private List<String> extractWheelLabels(@NonNull List<WheelItem> items) {
        ArrayList<String> labels = new ArrayList<>();
        for (WheelItem item : items) {
            labels.add(item.name);
        }
        return labels;
    }

    private void startExtractAnimation() {
        if (wheelItems.isEmpty()) {
            tvPreview.setText("暂无可抽取数据");
            return;
        }

        int targetIndex = random.nextInt(wheelItems.size());
        WheelItem target = wheelItems.get(targetIndex);

        wheelView.spinToIndex(targetIndex, () -> {
            tvPreview.setText(buildPreviewText(target));
            toast("抽中： " + target.name);
        });
    }

    private String buildPreviewText(@NonNull WheelItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append("抽中：").append(item.name).append('\n');

        if (item.food != null) {
            sb.append("id：").append(item.food.getId()).append('\n');
            appendIfNotEmpty(sb, "校区", item.food.getCampus());
            appendIfNotEmpty(sb, "食堂", item.food.getCanteen());
            appendIfNotEmpty(sb, "楼层", item.food.getFloor());
            appendIfNotEmpty(sb, "窗口", item.food.getWindow());
            appendIfNotEmpty(sb, "描述", item.food.getDescription());
            if (item.food.getPrice() != null) {
                sb.append("价格：").append(String.format(Locale.CHINA, "%.2f 元", item.food.getPrice() / 100.0)).append('\n');
            }
        } else {
            sb.append("说明：这是手动输入的条目，暂无 repository 详情").append('\n');
        }

        return sb.toString().trim();
    }

    private void appendIfNotEmpty(StringBuilder sb, String label, @Nullable String value) {
        if (!TextUtils.isEmpty(value)) {
            sb.append(label).append("：").append(value).append('\n');
        }
    }

    private String safeFoodName(@Nullable Food food) {
        if (food == null || TextUtils.isEmpty(food.getName())) return "";
        return food.getName();
    }

    private void toast(@NonNull String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private static class WheelItem {
        final String name;
        final Food food;
        final int rowIndex;

        WheelItem(String name, Food food, int rowIndex) {
            this.name = name;
            this.food = food;
            this.rowIndex = rowIndex;
        }
    }
}