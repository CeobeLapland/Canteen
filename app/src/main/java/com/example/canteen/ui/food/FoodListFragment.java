package com.example.canteen.ui.food;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.canteen.R;
import com.example.canteen.data.entity.Campus;
import com.example.canteen.data.entity.Canteen;
import com.example.canteen.data.entity.Floor;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 食品列表页面
 * 职责：
 *  1. 显示搜索栏
 *  2. 用 RecyclerView 展示食品卡片列表
 *  3. 点击卡片跳转食品详情页
 *  4. 监听 ViewModel.foodList 自动刷新列表
 */
public class FoodListFragment extends Fragment {

    private FoodAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        System.out.println("FoodListFragment onCreateView");

        if(savedInstanceState!=null) {
            System.out.println("FoodListFragment received savedInstanceState: " + savedInstanceState);
        }
        else {
            if(FoodRepository.instance == null)
                repository = new FoodRepository(requireActivity().getApplication());
        }

        return inflater.inflate(R.layout.fragment_food_list, container, false);
    }

    // 控件声明
    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    // 筛选数据
    public Searcher searcher = new Searcher();
    private FoodRepository repository;

    // 记录选中的值
    //private String selectedCampus = "全部校区";
    //private String selectedCanteen = "全部食堂";
    //private String selectedFloor = "全部楼层";

    private HorizontalScrollView scrollViewCampus, scrollViewCanteen, scrollViewFloor;
    private LinearLayout layoutTagsCampus, layoutTagsCanteen, layoutTagsFloor;
    private Button btnToggleCampus, btnToggleCanteen, btnToggleFloor;
    //private boolean isExpandedCampus = false, isExpandedCanteen = false, isExpandedFloor = false;
    //先把三个合在一起开闭吧，之后再改
    private boolean isExpanded = false;

    private Button[] allTagButtonsCampus, allTagButtonsCanteen, allTagButtonsFloor; // 存储所有标签按钮，方便后续操作
    //先另外写一个MAP用来查询吧，回来再改成更高效的
    private Map<String, Button> campusTagButtonsMap = new HashMap<>(),
            canteenTagButtonsMap = new HashMap<>(),
            floorTagButtonsMap = new HashMap<>();


    /**
     * 动态生成标签按钮
     */
    private Button[] generateTags(List<String> tagList, LinearLayout layoutTags,
                                  Map<String, Button> tagButtonsMap) {
        int length = tagList.size();
        Button[] allTagButtons = new Button[length];
        int index = 0;
        for (String text : tagList) {
            // 创建按钮
            allTagButtons[index] = new Button(this.getContext());
            Button tagBtn = allTagButtons[index];
            index++;
            tagBtn.setText(text);

            tagButtonsMap.put(text, tagBtn);

            // 设置按钮大小、样式、圆角
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 5); // 间距
            tagBtn.setLayoutParams(params);

            // 添加到布局
            layoutTags.addView(tagBtn);
        }
        return allTagButtons;
    }




    // 分页参数
    private int currentPage = 1;    // 当前页码

    // 分页新增：是否正在加载（防止重复请求）
    public boolean isLoading = false;
    // 分页新增：是否还有更多数据
    public boolean hasMore = true;

    // ====================== 分页新增：追加下一页数据 ======================
    private void addNextPage(int page)
    {
        repository.loadPage(page)
                .subscribe(foods -> {
                    System.out.println("加载到第 " + page + " 页，" + foods.size() + " 条数据");
                    if (foods.isEmpty()) {
                        hasMore = false; // 没有更多数据了
                    } else {
                        List<Food> currentList = adapter.getCurrentList();
                        List<Food> newList = new ArrayList<>(currentList);
                        newList.addAll(foods);
                        adapter.submitList(newList); // 提交新列表，触发 DiffUtil 计算差异并刷新 RecyclerView
                    }
                    isLoading = false; // 加载完成
                }, throwable -> {
                    System.err.println("加载第 " + page + " 页失败: " + throwable.getMessage());
                    isLoading = false; // 加载完成（即使失败也要重置状态）
                });
    }

    // 仅加载本页数据（不追加），适用于刷新或搜索等场景
    private void loadPageOnly(int page)
    {
        repository.loadPage(page)
                .subscribe(foods -> {
                    System.out.println("加载了第 " + page + " 页，" + foods.size() + " 条数据");
                    adapter.submitList(foods); // 直接提交本页数据，替换当前列表
                    isLoading = false; // 加载完成
                }, throwable -> {
                    System.err.println("加载第 " + page + " 页失败: " + throwable.getMessage());
                    isLoading = false; // 加载完成（即使失败也要重置状态）
                });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── RecyclerView 设置 ──────────────────────────────
        recyclerView = view.findViewById(R.id.recycler_food);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FoodAdapter(food -> {
            System.out.println("点击了食品: " + food.getName());
            // 点击食品卡片 → 跳转详情页，携带 foodId
            Intent intent = new Intent(requireContext(), FoodDetailActivity.class);
            intent.putExtra(FoodDetailActivity.EXTRA_FOOD_ID, food.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // 核心：滑动监听 → 自动加载下一页
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 只监听向下滑动
                if (dy <= 0) return;

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisible = manager.findLastCompletelyVisibleItemPosition();
                int totalCount = manager.getItemCount();

                // 判断：未加载 + 有更多数据 + 滑到底部附近
                if (!isLoading && hasMore && lastVisible >= totalCount - 2) {
                    isLoading = true; // 标记加载中
                    currentPage++; // 页码+1
                    addNextPage(currentPage); // 请求下一页
                }
            }
        });


        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        //tvPreview = view.findViewById(R.id.tv_preview);


        // 搜索按钮点击事件
        btnSearch.setOnClickListener(v -> {
            String searchContent = etSearch.getText().toString().trim();

            System.out.println("点击了搜索按钮，搜索内容: " + searchContent);
            //viewModel.setSearchQuery(searchContent); // 更新搜索关键词，触发数据刷新
        });

        initSearcherData(); // 初始化树形查询数据

        //region 筛选标签相关
        // 绑定控件
        scrollViewCampus = view.findViewById(R.id.scroll_view_campus);
        scrollViewCanteen = view.findViewById(R.id.scroll_view_canteen);
        scrollViewFloor = view.findViewById(R.id.scroll_view_floor);
        layoutTagsCampus = view.findViewById(R.id.layout_tags_campus);
        layoutTagsCanteen = view.findViewById(R.id.layout_tags_canteen);
        layoutTagsFloor = view.findViewById(R.id.layout_tags_floor);
        btnToggleCampus = view.findViewById(R.id.btn_toggle_campus);
        btnToggleCanteen = view.findViewById(R.id.btn_toggle_canteen);
        btnToggleFloor = view.findViewById(R.id.btn_toggle_floor);

        // 初始隐藏标签
        //scrollView.setVisibility(View.GONE);

        // 展开/收起 点击事件
        btnToggleCampus.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            if (isExpanded) {
                scrollViewCampus.setVisibility(View.VISIBLE);
                scrollViewCanteen.setVisibility(View.VISIBLE);
                scrollViewFloor.setVisibility(View.VISIBLE);
            } else {
                scrollViewCampus.setVisibility(View.GONE);
                scrollViewCanteen.setVisibility(View.GONE);
                scrollViewFloor.setVisibility(View.GONE);
            }
        });
        btnToggleCanteen.setOnClickListener(v -> btnToggleCampus.performClick());
        btnToggleFloor.setOnClickListener(v -> btnToggleCampus.performClick());
        //第一次知道还能这么写


        allTagButtonsCampus = generateTags(searcher.getAllCampusNames(), layoutTagsCampus, campusTagButtonsMap);
        allTagButtonsCanteen = generateTags(searcher.getAllCanteenNames(), layoutTagsCanteen, canteenTagButtonsMap);
        allTagButtonsFloor = generateTags(searcher.getAllFloorNames(), layoutTagsFloor, floorTagButtonsMap);

        scrollViewCampus.setVisibility(View.GONE);
        scrollViewCanteen.setVisibility(View.GONE);
        scrollViewFloor.setVisibility(View.GONE);
        //endregion

        bindAllTagButtons();


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        // 2. 设置下拉刷新监听（核心）
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 下拉触发时执行刷新逻辑
                refreshData();
            }
        });

        // 可选：设置刷新图标颜色（一行代码搞定）
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light
        );

        // 模拟首次进入自动刷新（直接显示刷新图标 + 执行刷新逻辑）
        //swipeRefreshLayout.setRefreshing(true);
        //refreshData();
        getAllFood();
    }

    private void initSearcherData() {

        Campus campus1 = new Campus("中关村校区");
        Canteen canteen11 = new Canteen("中央食堂");
        searcher.addCampus(campus1);
        searcher.addCanteenToCampus(campus1.getCampusName(), canteen11);
        searcher.addFloorToCanteen(canteen11.getCanteenName(), new Floor("中央食堂一层"));
        searcher.addFloorToCanteen(canteen11.getCanteenName(), new Floor("中央食堂二层"));

        Campus campus2 = new Campus("房山校区");
        Canteen canteen21 = new Canteen("南食堂");
        Canteen canteen22 = new Canteen("北食堂");
        Canteen canteen23 = new Canteen("东食堂");
        Canteen canteen24 = new Canteen("清真食堂");
        searcher.addCampus(campus2);
        searcher.addCanteenToCampus(campus2.getCampusName(), canteen21);
        searcher.addCanteenToCampus(campus2.getCampusName(), canteen22);
        searcher.addCanteenToCampus(campus2.getCampusName(), canteen23);
        searcher.addCanteenToCampus(campus2.getCampusName(), canteen24);
        searcher.addFloorToCanteen(canteen21.getCanteenName(), new Floor("南食堂一层"));
        searcher.addFloorToCanteen(canteen21.getCanteenName(), new Floor("南食堂二层"));
        searcher.addFloorToCanteen(canteen21.getCanteenName(), new Floor("南食堂三层"));
        searcher.addFloorToCanteen(canteen22.getCanteenName(), new Floor("北食堂一层"));
        searcher.addFloorToCanteen(canteen22.getCanteenName(), new Floor("北食堂二层"));
        searcher.addFloorToCanteen(canteen22.getCanteenName(), new Floor("北食堂三层"));
        searcher.addFloorToCanteen(canteen23.getCanteenName(), new Floor("东食堂一层"));
        searcher.addFloorToCanteen(canteen23.getCanteenName(), new Floor("东食堂二层"));
        searcher.addFloorToCanteen(canteen23.getCanteenName(), new Floor("东食堂三层"));
        searcher.addFloorToCanteen(canteen24.getCanteenName(), new Floor("清真食堂一层"));
        //searcher.addFloorToCanteen(canteen24.getCanteenName(), new Floor("清真食堂二层"));
    }

    private void bindAllTagButtons()
    {
        //首先是campus标签
        for(Button btn : allTagButtonsCampus) {
            String text = btn.getText().toString();
            btn.setOnClickListener(v -> {
                System.out.println("点击了campus标签: " + text);
                //Toast.makeText(this.getContext(), "点击了标签: " + text, Toast.LENGTH_SHORT).show();
                //searcher.setSelectedCampusName(text);
                // 更新食堂标签显示
                List<String> canteenNames = searcher.getCanteenNamesByCampus(text);
                //List<String> canteenNames = searcher.getCanteenNamesBySelectedCampus();
                setButtonsVisibility(allTagButtonsCanteen, canteenTagButtonsMap, canteenNames);

                // 更新楼层标签显示（先清空）
                List<String> floorNames = searcher.getFloorNamesByCanteens(canteenNames);
                setButtonsVisibility(allTagButtonsFloor, floorTagButtonsMap, floorNames);
            });
        }

        //然后是canteen标签
        for(Button btn : allTagButtonsCanteen) {
            String text = btn.getText().toString();
            btn.setOnClickListener(v -> {
                System.out.println("点击了canteen标签: " + text);
                //Toast.makeText(this.getContext(), "点击了标签: " + text, Toast.LENGTH_SHORT).show();
                //searcher.setSelectedCanteenName(text);
                // 更新楼层标签显示
                List<String> floorNames = searcher.getFloorNamesByCanteen(text);
                setButtonsVisibility(allTagButtonsFloor, floorTagButtonsMap, floorNames);

                // 校区标签只显示食堂所属校区
                List<String> campusNames = new ArrayList<>();
                String campusName = searcher.getCampusNameByCanteen(text);
                campusNames.add(campusName);
                setButtonsVisibility(allTagButtonsCampus, campusTagButtonsMap, campusNames);

                // 食堂标签只显示同校区的食堂
                List<String> canteenNames = searcher.getCanteenNamesByCampus(campusName);
                setButtonsVisibility(allTagButtonsCanteen, canteenTagButtonsMap, canteenNames);
            });
        }

        //最后是floor标签
        for(Button btn : allTagButtonsFloor) {
            String text = btn.getText().toString();
            btn.setOnClickListener(v -> {
                System.out.println("点击了floor标签: " + text);
                //Toast.makeText(this.getContext(), "点击了标签: " + text, Toast.LENGTH_SHORT).show();
                //searcher.setSelectedFloorName(text);
                // 更新食堂标签显示，只显示所属食堂
                List<String> canteenNames = new ArrayList<>();
                String canteenName = searcher.getCanteenNameByFloor(text);
                canteenNames.add(canteenName);
                setButtonsVisibility(allTagButtonsCanteen, canteenTagButtonsMap, canteenNames);

                // 更新校区标签显示
                List<String> campusNames = new ArrayList<>();
                String campusName = searcher.getCampusNameByCanteen(canteenName);
                campusNames.add(campusName);
                setButtonsVisibility(allTagButtonsCampus, campusTagButtonsMap, campusNames);
            });
        }
    }

    private void setButtonsVisibility(Button[] buttons, Map<String, Button> buttonsMap, List<String> namesToShow)
    {
        // 先把所有按钮隐藏
        for (Button btn : buttons) {
            btn.setVisibility(View.GONE);
        }
        // 再把需要显示的按钮显示出来
        for (String name : namesToShow) {
            Button btn = buttonsMap.get(name);
            if (btn != null) {
                btn.setVisibility(View.VISIBLE);
            }
        }
    }




    private void getAllFood()
    {
        repository.getAllFoods()
                .subscribe(foods -> {
                    System.out.println("查询到 " + foods.size() + " 个食品");
                    for (Food food : foods) {
                        System.out.println("Food: " + food.getName() + ", Location: " + food.getFullLocation());
                    }
                    adapter.submitList(foods);
                }, throwable -> {
                    System.err.println("查询食品失败: " + throwable.getMessage());
                });
    }

    // 模拟刷新数据（你可以在这里请求接口、加载数据库等）
    private void refreshData() {
        currentPage = 1; // 刷新时重置页码
        //hasMore = true; // 重置是否有更多数据的标志
        loadPageOnly(currentPage); // 仅加载第一页数据，替换当前列表
        swipeRefreshLayout.setRefreshing(false);
    }
}