package com.example.canteen.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.canteen.R;
import com.example.canteen.ui.food.FoodListFragment;
import com.example.canteen.ui.post.PostListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主 Activity
 * 采用单 Activity + 多 Fragment 架构：
 *  - Activity 仅负责持有底部导航栏和 Fragment 容器
 *  - 页面切换逻辑全部在此处理
 *  - 每个功能模块是独立的 Fragment
 */
public class MainActivity extends AppCompatActivity {

    // Fragment 实例复用（避免每次切换重新创建）
    private FoodListFragment foodListFragment;
    private PostListFragment postListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 Fragment
        foodListFragment = new FoodListFragment();
        postListFragment = new PostListFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, foodListFragment, "FOOD_LIST")
                .add(R.id.fragment_container, postListFragment, "POST_LIST")
                .hide(postListFragment) // 默认隐藏帖子列表
                .commit();

        // 默认显示食品列表页
        //if (savedInstanceState == null)
        //    loadFragment(foodListFragment);

        // 底部导航监听
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_food) {
                loadFragment(foodListFragment);
                return true;
            } else if (itemId == R.id.nav_post) {
                loadFragment(postListFragment);
                return true;
            }
            return false;
        });
    }

    /*
     * 替换 Fragment 容器内容
     * 使用 replace 而非 add，保持返回栈简洁
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }*/
    /** 在 R.id.fragment_container 里替换 Fragment 容器内容，使用 show/hide 保持 Fragment 状态 */
    private void loadFragment(Fragment fragment) {
        System.out.println("Loading fragment");
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // 添加淡入淡出动画
                .hide(foodListFragment)
                .hide(postListFragment)
                .show(fragment)
                .commit();
    }
}
