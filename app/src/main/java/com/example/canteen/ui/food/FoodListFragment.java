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

import com.example.canteen.R;
import com.example.canteen.data.entity.Campus;
import com.example.canteen.data.entity.Canteen;
import com.example.canteen.data.entity.Floor;

import com.example.canteen.data.entity.Food;
import com.example.canteen.viewmodel.FoodViewModel;


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

    private FoodViewModel viewModel;
    private FoodAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        System.out.println("FoodListFragment onCreateView");

        return inflater.inflate(R.layout.fragment_food_list, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("FoodListFragment onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("FoodListFragment onDestroyView");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("FoodListFragment onPause");
    }

    // 控件声明
    private EditText etSearch;
    private Button btnSearch;
    //private TextView tvPreview;

    // 筛选数据
    public Searcher searcher = new Searcher();

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

    // 筛选标签文字（你可以随便改）
    //private final List<String> tagList = new ArrayList<>(Arrays.asList("全部", "热门", "最新上线", "价格低", "评分高", "距离近", "优惠多", "新品", "推荐"));


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

            /*
            //tagBtn.setPadding(20, 10, 20, 10);
            //tagBtn.setBackgroundResource(R.drawable.tag_bg); // 圆角样式
            //tagBtn.setTextColor(ContextCompat.getColor(this, R.color.tag_text));

            // 标签点击事件
            tagBtn.setOnClickListener(v -> {
                System.out.println("点击了标签: " + text);
                // 这里可以写你的筛选逻辑
            });*/

            // 添加到布局
            layoutTags.addView(tagBtn);
        }
        return allTagButtons;
    }




    private RecyclerView recyclerView;
    // 分页参数
    private int currentPage = 1;    // 当前页码


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── ViewModel 初始化（由 Activity 作用域管理，Fragment 共享） ──
        viewModel = new ViewModelProvider(requireActivity()).get(FoodViewModel.class);

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
                if (!adapter.isLoading && adapter.hasMore && lastVisible >= totalCount - 2) {
                    adapter.isLoading = true; // 标记加载中
                    currentPage++; // 页码+1
                    //loadFoodData(currentPage); // 请求下一页
                    viewModel.addPage(currentPage);
                }
            }
        });
        // 首次加载第一页
        //loadFoodData(currentPage);






        // ── 观察食品列表，数据变化自动更新 UI ──────────────
        viewModel.foodList.observe(getViewLifecycleOwner(), foods -> {
            if(foods != null)
            {
                adapter.submitList(foods);
                System.out.println("食品列表已更新，当前共 " + foods.size() + " 条数据");
            }
        });


        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        //tvPreview = view.findViewById(R.id.tv_preview);


        // 搜索按钮点击事件
        btnSearch.setOnClickListener(v -> {
            String searchContent = etSearch.getText().toString().trim();

            System.out.println("点击了搜索按钮，搜索内容: " + searchContent);
            viewModel.setSearchQuery(searchContent); // 更新搜索关键词，触发数据刷新
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
        /*btnToggleCampus.setOnClickListener(v -> {
            isExpandedCampus = !isExpandedCampus;
            if (isExpandedCampus) {
                scrollViewCampus.setVisibility(View.VISIBLE);
            } else {
                scrollViewCampus.setVisibility(View.GONE);
            }
        });
        btnToggleCanteen.setOnClickListener(v -> {
            isExpandedCanteen = !isExpandedCanteen;
            if (isExpandedCanteen) {
                scrollViewCanteen.setVisibility(View.VISIBLE);
            } else {
                scrollViewCanteen.setVisibility(View.GONE);
            }
        });
        btnToggleFloor.setOnClickListener(v -> {
            isExpandedFloor = !isExpandedFloor;
            if (isExpandedFloor) {
                scrollViewFloor.setVisibility(View.VISIBLE);
            } else {
                scrollViewFloor.setVisibility(View.GONE);
            }
        });*/
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

    /*private void bindAllTagButtons(Button[] allTagButtons, List<String> tagList, String type)
    {
        for (int i = 0; i < allTagButtons.length; i++) {
            Button tagBtn = allTagButtons[i];
            String text = tagList.get(i);
            tagBtn.setOnClickListener(v -> {
                System.out.println("点击了标签: " + text);
                // 这里可以写你的筛选逻辑
                Toast.makeText(this.getContext(), "点击了标签: " + text, Toast.LENGTH_SHORT).show();
                // 根据 type 判断是校区、食堂还是楼层，更新 searcher 的选中值
                if (type.equals("campus")) {
                    searcher.setSelectedCampus(text);
                } else if (type.equals("canteen")) {
                    searcher.setSelectedCanteen(text);
                } else if (type.equals("floor")) {
                    searcher.setSelectedFloor(text);
                }
            });
        }
    }*/





}