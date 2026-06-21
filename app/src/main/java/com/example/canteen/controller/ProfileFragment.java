package com.example.canteen.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import com.example.canteen.R;
import com.example.canteen.data.entity.FeedType;
import com.example.canteen.data.entity.ProfileInfo;
import com.example.canteen.data.repository.ProfileRepository;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.schedulers.Schedulers;

public class ProfileFragment extends Fragment {

    private ProfileRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private ImageView ivAvatar;
    private TextView tvName, tvUid, tvJoinTime, tvPermission;
    private TextView tvPostCount, tvViewCount, tvLikeCount, tvCommentCount;
    private AppCompatButton btnSettings, btnRefresh;
    private MaterialCardView cardMyPosts, cardHistory, cardFavorites;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new ProfileRepository(requireActivity().getApplication());


        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvUid = view.findViewById(R.id.tvUid);
        tvJoinTime = view.findViewById(R.id.tvJoinTime);
        tvPermission = view.findViewById(R.id.tvPermission);

        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvViewCount = view.findViewById(R.id.tvViewCount);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);
        tvCommentCount = view.findViewById(R.id.tvCommentCount);

        btnSettings = view.findViewById(R.id.btnSettings);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        cardMyPosts = view.findViewById(R.id.cardMyPosts);
        cardHistory = view.findViewById(R.id.cardHistory);
        cardFavorites = view.findViewById(R.id.cardFavorites);

        btnSettings.setOnClickListener(v -> {
            // 这里先留空，等你做设置页后再接导航
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_profileFragment_to_settingsFragment);
        });

        btnRefresh.setOnClickListener(v -> loadProfile());

        cardMyPosts.setOnClickListener(v -> openFeed(FeedType.MY_POSTS));
        cardHistory.setOnClickListener(v -> openFeed(FeedType.HISTORY));
        cardFavorites.setOnClickListener(v -> openFeed(FeedType.FAVORITES));

        loadProfile();
    }

    private void loadProfile() {
        disposables.add(
                repository.loadMyProfile()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::bindProfile, throwable -> {
                            tvName.setText("加载失败");
                            tvUid.setText("");
                            tvJoinTime.setText("");
                            tvPermission.setText("");
                            tvPostCount.setText("--");
                            tvViewCount.setText("--");
                            tvLikeCount.setText("--");
                            tvCommentCount.setText("--");
                        })
        );
    }

    private void bindProfile(ProfileInfo profile) {
        ivAvatar.setImageResource(android.R.drawable.sym_def_app_icon);
        tvName.setText(profile.getName());
        tvUid.setText("UID：" + profile.getUid());
        tvJoinTime.setText("加入时间：" + profile.getJoinTimeText());
        tvPermission.setText("权限：" + profile.getPermission());

        tvPostCount.setText(String.valueOf(profile.getPostCount()));
        tvViewCount.setText(String.valueOf(profile.getViewCount()));
        tvLikeCount.setText(String.valueOf(profile.getLikeCount()));
        tvCommentCount.setText(String.valueOf(profile.getCommentCount()));
    }

    private void openFeed(FeedType type) {
        Bundle args = new Bundle();
        args.putString("feedType", type.name());
        args.putString("title", type.getTitle());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_profileFragment_to_postFeedFragment, args);
    }

    @Override
    public void onDestroyView() {
        disposables.clear();
        super.onDestroyView();
    }
}