package com.example.canteen.controller.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.canteen.controller.DetailCommentsFragment;
import com.example.canteen.controller.RelatedPostsFragment;

public class DetailPagerAdapter extends FragmentStateAdapter {

    private final long postId;

    public DetailPagerAdapter(@NonNull Fragment fragment, long postId) {
        super(fragment);
        this.postId = postId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return DetailCommentsFragment.newInstance(postId);
        }
        return RelatedPostsFragment.newInstance(postId);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}