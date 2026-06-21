package com.example.canteen.controller.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.canteen.controller.CookFragment;
import com.example.canteen.controller.RecipeListFragment;


public class CookingFrameworkPagerAdapter extends FragmentStateAdapter {

    public CookingFrameworkPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new RecipeListFragment();
        }
        return new CookFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}