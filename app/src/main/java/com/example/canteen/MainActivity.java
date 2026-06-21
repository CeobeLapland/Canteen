package com.example.canteen;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment not found.");
        }

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // 不要用系统默认的setupWithNavController，会重复创建Fragment
        // NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // 官方标准绑定，自动实现多返回栈+单例
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // 只需要加这一行：重复点击当前Tab不刷新
        bottomNavigationView.setOnItemReselectedListener(item -> {});
        // 自定义Tab选中监听，配置单例跳转
        /*bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)       // 单一实例，不重复创建Fragment
                    .setRestoreState(true)          // 恢复页面历史状态（滚动、输入等）
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                    .build();

            navController.navigate(item.getItemId(), null, navOptions);
            return true;
        });

        // 重复点击当前Tab不做任何操作，避免页面刷新
        bottomNavigationView.setOnItemReselectedListener(item -> {
            // 空实现
        });*/
        // 移除旧的 setupWithNavController，改用官方推荐监听
        /*bottomNavigationView.setOnItemSelectedListener(item -> {
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), true)
                    .build();
            navController.navigate(item.getItemId(), null, navOptions);
            return true;
        });

        // 重复点击当前Tab不执行任何操作，避免重复刷新
        bottomNavigationView.setOnItemReselectedListener(item -> {});*/
    }

}