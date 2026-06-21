package com.example.canteen.controller;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.canteen.R;
import com.example.canteen.controller.adapter.CookingFrameworkPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CookingFrameworkFragment extends Fragment {

    public CookingFrameworkFragment() {
        super(R.layout.fragment_cooking_framework);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cooking_framework, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        FloatingActionButton fab = view.findViewById(R.id.fabContribute);

        viewPager.setAdapter(new CookingFrameworkPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("食谱");
                //fab.setVisibility(View.VISIBLE);
            } else {
                tab.setText("做饭");
                //fab.setVisibility(View.GONE);
            }
            System.out.println("Tab selected: " + position);
        }).attach();

        fab.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_cookingFrameworkFragment_to_contributeRecipeFragment);
        });


    }
}