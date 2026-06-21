package com.example.canteen.controller;

import com.example.canteen.R;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.canteen.controller.adapter.FoodAdapter;
import com.example.canteen.data.database.Converters;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;
import com.example.canteen.data.repository.RepositoryCallback;
import com.example.canteen.data.repository.SyncRepository;
import com.example.canteen.net.dto.Dtos;
import com.example.canteen.net.response.ApiResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//@NoArgsConstructor
public class FoodFragment extends Fragment implements FoodAdapter.OnFoodClickListener {

    private static final String ARG_FOOD = "arg_food";

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextInputEditText etKeyword;
    private MaterialButton btnSearch;
    private MaterialButton btnClearTags;

    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private LinearLayout pageContainer;

    //private TextView tvCampusValue;
    //private TextView tvCanteenValue;
    //private TextView tvFloorValue;
    //private TextView tvWindowValue;

    private final List<String> selectedTags = new ArrayList<>();
    private final List<Chip> allTagChips = new ArrayList<>();


    //private int currentPage = 1;
    private final int pageSize = 20;

    private FoodAdapter adapter;
    //private final FoodRepository repository = FoodRepository.getInstance();
    private FoodRepository repository;

    private SyncRepository syncRepository;

    private final List<String> tagOptions = Arrays.asList(
            "米饭", "面食", "轻食", "甜品", "饮品", "早餐",
            "夜宵", "辣", "热", "便宜"
    );
    // 筛选控件
    private Spinner spCampus, spCanteen, spFloor, spWindow;
    // 选中结果（null 代表选中“全部”）
    private String selectedCampus, selectedCanteen, selectedFloor, selectedWindow;
    // 原始全量数据
    private List<WindowDto> allWindows = new ArrayList<>();

    // 层级映射缓存（提升查询性能，仅初始化时构建一次）
    private Map<String, List<String>> campusCanteenMap;   // 校园 → 对应食堂列表
    private Map<String, List<String>> canteenFloorMap;    // 校园_食堂 → 对应楼层列表
    private Map<String, List<String>> floorWindowMap;     // 校园_食堂_楼层 → 对应窗口列表

    // 常量定义
    private static final String SPLIT = "_";
    private static final String OPTION_ALL = "全部";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        System.out.println("FoodFragment onCreateView: " + this);
        if (FoodRepository.getInstance() == null)
            repository = new FoodRepository(getActivity().getApplication());
        else
                repository = FoodRepository.getInstance();

        syncRepository = new SyncRepository(getActivity().getApplication());
        //测试同步功能
        syncRepository.testSync(new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                System.out.println("Sync test success: " + data);
            }

            @Override
            public void onError(int code, String message) {
                System.out.println("Sync test error: " + code + ", " + message);
                //弹出一个窗口显示错误信息
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("同步测试错误，只能给你看本地数据了喵（不过如果本地也没有的话就放俩示例占位了（笨蛋作者）（笨蛋作者）")
                        .setMessage("发生错误：" + message + " (错误码: " + code + ")")
                        .setPositiveButton("确定", null)
                        .show();
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Sync test failure: " + t.getMessage());
                //弹出一个窗口显示错误信息
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("同步测试失败，服务器未响应，只能给你看本地数据了喵（不过如果本地也没有的话就放俩示例占位了（笨蛋作者）")
                        .setMessage("发生错误：" + t.getMessage())
                        .setPositiveButton("确定", null)
                        .show();
            }
        });


        View root = inflater.inflate(R.layout.fragment_food, container, false);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh);
        etKeyword = root.findViewById(R.id.et_keyword);
        btnSearch = root.findViewById(R.id.btn_search);
        btnClearTags = root.findViewById(R.id.btn_clear_tags);
        recyclerView = root.findViewById(R.id.rv_foods);
        pageContainer = root.findViewById(R.id.page_container);



        LinearLayout tagRow1 = root.findViewById(R.id.tag_row_1);
        LinearLayout tagRow2 = root.findViewById(R.id.tag_row_2);


        // 1. 初始化控件
        spCampus = root.findViewById(R.id.sp_campus);
        spCanteen = root.findViewById(R.id.sp_canteen);
        spFloor = root.findViewById(R.id.sp_floor);
        spWindow = root.findViewById(R.id.sp_window);
        // 2. 填充原始数据（实际项目替换为你的数据源）
        initSourceData();
        // 3. 预处理数据：一次遍历构建层级映射
        preprocessData();
        // 4. 初始化第一级（校园）筛选器
        initCampusSpinner();
        // 5. 设置四级联动监听
        setupFilterListeners();



        setupRecyclerView();

        setupTags(tagRow1, tagRow2);

        btnSearch.setOnClickListener(v -> loadFoods(1, true));
        btnClearTags.setOnClickListener(v -> clearTags());

        swipeRefreshLayout.setOnRefreshListener(() -> loadFoods(1, true));

        //loadFoods(1, false);

        repository.fetchAllFoodsNoPaginationAsync(new RepositoryCallback<List<Dtos.FoodDetailDto>>() {
            @Override
            public void onSuccess(List<Dtos.FoodDetailDto> data) {
                System.out.println("Fetched " + data.size() + " foods from repository.");
                adapter.submitList(Converters.convertFoodDetailList(data));

                //提示弹窗
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("数据加载完成")
                        .setMessage("成功从服务器获取了 " + data.size() + " 条食品数据！")
                        .setPositiveButton("太棒了喵", null)
                        .show();
                //adapter.submitList(data);
            }

            @Override
            public void onError(int code, String message) {
                System.out.println("Error fetching foods: " + code + ", " + message);

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("数据加载错误")
                        .setMessage("发生错误：" + message + " (错误码: " + code + ")")
                        .setPositiveButton("确定", null)
                        .show();
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("Failure fetching foods: " + t.getMessage());

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("数据加载失败")
                        .setMessage("发生错误：" + t.getMessage())
                        .setPositiveButton("确定", null)
                        .show();
            }
        });


        return root;
    }

    private void setupRecyclerView() {
        adapter = new FoodAdapter(this);
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }



    // region 四级联动筛选相关方法
    /**
     * 模拟原始数据，实际项目中替换为你的 allWindows 赋值逻辑
     */
    private void initSourceData() {
        allWindows.add(new WindowDto("米面窗口", "南食堂1楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("菜窗口", "南食堂1楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("轻食窗口", "南食堂1楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("自选窗口", "南食堂1楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("小超市", "南食堂1楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("米线饺子", "南食堂2楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("小火锅", "南食堂2楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("西侧区域", "南食堂3楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("中间区域", "南食堂3楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("东侧区域", "南食堂3楼", "南食堂", "良乡校区"));
        allWindows.add(new WindowDto("奶茶汉堡", "南食堂3楼", "南食堂", "良乡校区"));

        allWindows.add(new WindowDto("米面窗口", "北食堂1楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("菜窗口", "北食堂1楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("小超市", "北食堂1楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("西侧", "北食堂2楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("东侧", "北食堂2楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("面馆", "北食堂3楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("米线", "北食堂3楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("饺子", "北食堂3楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("不知道什么锅", "北食堂3楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("大碗", "北食堂3楼", "北食堂", "良乡校区"));
        allWindows.add(new WindowDto("奶茶", "北食堂3楼", "北食堂", "良乡校区"));

        allWindows.add(new WindowDto("米面窗口", "东食堂1楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("菜窗口", "东食堂1楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("小超市", "东食堂1楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("教职工窗口", "东食堂2楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("自选", "东食堂2楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("一大堆", "东食堂2楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("蜜雪冰城", "东食堂2楼", "东食堂", "良乡校区"));
        allWindows.add(new WindowDto("作者没怎么去过", "东食堂3楼", "东食堂", "良乡校区"));
        
        
        allWindows.add(new WindowDto("麦叔铺子", "理教一楼", "中食堂", "良乡校区"));
        allWindows.add(new WindowDto("奶茶店", "综教一楼", "中食堂", "良乡校区"));
        allWindows.add(new WindowDto("一大堆", "甘棠楼下", "中食堂", "良乡校区"));
        allWindows.add(new WindowDto("一大堆", "学服餐厅", "中食堂", "良乡校区"));
        allWindows.add(new WindowDto("好久没去", "学服超市", "中食堂", "良乡校区"));
        allWindows.add(new WindowDto("咖啡店", "图书馆", "中食堂", "良乡校区"));
        allWindows.add(new WindowDto("小超市", "疏桐南", "中食堂", "良乡校区"));


        allWindows.add(new WindowDto("人很多", "一楼", "清真食堂", "良乡校区"));
    }

    /**
     * 性能核心：仅遍历一次全量数据，构建所有层级的去重映射
     * 后续筛选直接从 Map 取数，时间复杂度 O(1)
     */
    private void preprocessData() {
        // 用 HashSet 自动去重
        Map<String, Set<String>> campusCanteenSet = new HashMap<>();
        Map<String, Set<String>> canteenFloorSet = new HashMap<>();
        Map<String, Set<String>> floorWindowSet = new HashMap<>();

        for (WindowDto window : allWindows) {
            String campus = window.getCampus();
            String canteen = window.getCanteen();
            String floor = window.getFloor();
            String windowName = window.getName();

            // 1. 校园 → 食堂 映射
            if (!campusCanteenSet.containsKey(campus)) {
                campusCanteenSet.put(campus, new HashSet<>());
            }
            campusCanteenSet.get(campus).add(canteen);

            // 2. 校园_食堂 → 楼层 映射
            String canteenKey = campus + SPLIT + canteen;
            if (!canteenFloorSet.containsKey(canteenKey)) {
                canteenFloorSet.put(canteenKey, new HashSet<>());
            }
            canteenFloorSet.get(canteenKey).add(floor);

            // 3. 校园_食堂_楼层 → 窗口 映射
            String floorKey = campus + SPLIT + canteen + SPLIT + floor;
            if (!floorWindowSet.containsKey(floorKey)) {
                floorWindowSet.put(floorKey, new HashSet<>());
            }
            floorWindowSet.get(floorKey).add(windowName);
        }

        // Set 转 List，适配 ArrayAdapter
        campusCanteenMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : campusCanteenSet.entrySet()) {
            campusCanteenMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        canteenFloorMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : canteenFloorSet.entrySet()) {
            canteenFloorMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        floorWindowMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : floorWindowSet.entrySet()) {
            floorWindowMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * 初始化第一级：校园筛选框
     */
    private void initCampusSpinner() {
        List<String> campusList = new ArrayList<>(campusCanteenMap.keySet());
        campusList.add(0, OPTION_ALL);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, campusList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCampus.setAdapter(adapter);
    }

    /**
     * 设置四级联动选择监听
     */
    private void setupFilterListeners() {
        // —— 校园筛选监听 ——
        spCampus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (OPTION_ALL.equals(selected)) {
                    selectedCampus = null;
                    // 隐藏所有后续筛选框，重置所有选中值
                    spCanteen.setVisibility(View.GONE);
                    spFloor.setVisibility(View.GONE);
                    spWindow.setVisibility(View.GONE);
                    selectedCanteen = null;
                    selectedFloor = null;
                    selectedWindow = null;
                } else {
                    selectedCampus = selected;
                    // 刷新下一级（食堂）数据并显示
                    updateCanteenSpinner(selectedCampus);
                    spCanteen.setVisibility(View.VISIBLE);
                    // 隐藏后续层级并重置
                    spFloor.setVisibility(View.GONE);
                    spWindow.setVisibility(View.GONE);
                    selectedCanteen = null;
                    selectedFloor = null;
                    selectedWindow = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // —— 食堂筛选监听 ——
        spCanteen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (OPTION_ALL.equals(selected)) {
                    selectedCanteen = null;
                    spFloor.setVisibility(View.GONE);
                    spWindow.setVisibility(View.GONE);
                    selectedFloor = null;
                    selectedWindow = null;
                } else {
                    selectedCanteen = selected;
                    updateFloorSpinner(selectedCampus, selectedCanteen);
                    spFloor.setVisibility(View.VISIBLE);
                    spWindow.setVisibility(View.GONE);
                    selectedFloor = null;
                    selectedWindow = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // —— 楼层筛选监听 ——
        spFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                if (OPTION_ALL.equals(selected)) {
                    selectedFloor = null;
                    spWindow.setVisibility(View.GONE);
                    selectedWindow = null;
                } else {
                    selectedFloor = selected;
                    updateWindowSpinner(selectedCampus, selectedCanteen, selectedFloor);
                    spWindow.setVisibility(View.VISIBLE);
                    selectedWindow = null;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // —— 窗口筛选监听 ——
        spWindow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                selectedWindow = OPTION_ALL.equals(selected) ? null : selected;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // —— 刷新下级筛选框数据的工具方法 ——
    private void updateCanteenSpinner(String campus) {
        List<String> canteenList = new ArrayList<>(campusCanteenMap.get(campus));
        canteenList.add(0, OPTION_ALL);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, canteenList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCanteen.setAdapter(adapter);
    }

    private void updateFloorSpinner(String campus, String canteen) {
        String key = campus + SPLIT + canteen;
        List<String> floorList = new ArrayList<>(canteenFloorMap.get(key));
        floorList.add(0, OPTION_ALL);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, floorList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFloor.setAdapter(adapter);
    }

    private void updateWindowSpinner(String campus, String canteen, String floor) {
        String key = campus + SPLIT + canteen + SPLIT + floor;
        List<String> windowList = new ArrayList<>(floorWindowMap.get(key));
        windowList.add(0, OPTION_ALL);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, windowList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWindow.setAdapter(adapter);
    }

    // WindowDto 实体类（你已有则可删除，此处仅为编译通过）
    public static class WindowDto {
        private String name;
        private String floor;
        private String canteen;
        private String campus;

        public WindowDto(String name, String floor, String canteen, String campus) {
            this.name = name;
            this.floor = floor;
            this.canteen = canteen;
            this.campus = campus;
        }
        public String getName() { return name; }
        public String getFloor() { return floor; }
        public String getCanteen() { return canteen; }
        public String getCampus() { return campus; }
    }

    // endregion




    // region 标签筛选相关方法（Chip 样式、状态管理等）

    private void setupTags(LinearLayout row1, LinearLayout row2) {
        row1.removeAllViews();
        row2.removeAllViews();
        allTagChips.clear();

        int half = (int) Math.ceil(tagOptions.size() / 2.0);
        List<String> firstHalf = tagOptions.subList(0, half);
        List<String> secondHalf = tagOptions.subList(half, tagOptions.size());

        addTagChips(row1, firstHalf);
        addTagChips(row2, secondHalf);
    }

    private void addTagChips(LinearLayout row, List<String> tags) {
        for (String tag : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setClickable(true);
            chip.setTextSize(14f);
            chip.setChipStrokeWidth(1f);
            chip.setChipCornerRadius(999f);
            chip.setPadding(18, 6, 18, 6);

            updateChipStyle(chip, false);

            chip.setOnClickListener(v -> {
                boolean checked = chip.isChecked();
                if (checked) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
                updateChipStyle(chip, checked);
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 20, 0);
            row.addView(chip, lp);
            allTagChips.add(chip);
        }
    }

    private void updateChipStyle(Chip chip, boolean checked) {
        int bg = checked ? R.color.orange_primary : android.R.color.white;
        int text = checked ? android.R.color.white : R.color.text_primary;
        int stroke = checked ? R.color.orange_primary : R.color.orange_primary;

        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), bg)));
        chip.setTextColor(ContextCompat.getColor(requireContext(), text));
        chip.setChipStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), stroke)));
    }

    private void clearTags() {
        selectedTags.clear();
        for (Chip chip : allTagChips) {
            chip.setChecked(false);
            updateChipStyle(chip, false);
        }
        //loadFoods(1, true);
    }

    // endregion


    private void loadFoods(int page, boolean showRefreshing) {
        if (showRefreshing) {
            swipeRefreshLayout.setRefreshing(true);
        }

        String keyword = etKeyword.getText() == null ? "" : etKeyword.getText().toString().trim();

        repository.getFoodCount().subscribe(count -> {
            int totalPages = (int) Math.ceil(count / (double) pageSize);

            renderPageButtons(totalPages, page);
        }, error -> {
            if (showRefreshing) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        repository.getFoodsByCustomQuery(
                selectedCampus, selectedCanteen, selectedFloor, selectedWindow, keyword,
                null, null, selectedTags, pageSize, page
        ).subscribe(foods -> {
            adapter.submitList(foods);
        }, error -> {
            if (showRefreshing) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        if (showRefreshing) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void renderPageButtons(int totalPages, int selectedPage) {
        pageContainer.removeAllViews();

        for (int i = 1; i <= totalPages; i++) {
            final int page = i;
            MaterialButton btn = new MaterialButton(requireContext());
            btn.setText(String.valueOf(i));
            btn.setCornerRadius(18);
            btn.setStrokeWidth(2);
            btn.setTextSize(14f);
            btn.setAllCaps(false);
            btn.setMinWidth(0);
            btn.setInsetTop(0);
            btn.setInsetBottom(0);

            boolean selected = page == selectedPage;
            applyPageButtonStyle(btn, selected);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 16, 0);
            pageContainer.addView(btn, lp);

            btn.setOnClickListener(v -> loadFoods(page, false));
        }
    }

    private void applyPageButtonStyle(MaterialButton btn, boolean selected) {
        if (selected) {
            btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.orange_primary)));
            btn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
            btn.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.orange_primary)));
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.white)));
            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
            btn.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.orange_primary)));
        }
    }

    @Override
    public void onFoodClick(Food food) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_FOOD, food);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_foodFragment_to_foodDetailFragment, bundle);
    }

    private View requireViewSafe() {
        return getView();
    }

    private interface ValueSetter {
        void set(String value);
    }
}