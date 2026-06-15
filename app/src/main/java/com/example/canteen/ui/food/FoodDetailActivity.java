package com.example.canteen.ui.food;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canteen.R;
import com.example.canteen.data.entity.Food;
import com.example.canteen.data.repository.FoodRepository;
import com.example.canteen.ui.post.PostAdapter;


import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 食品详情 Activity
 * 显示：食品完整信息、综合评分、用户评分交互、关联帖子列表
 */
public class FoodDetailActivity extends AppCompatActivity {

    /** Intent 携带的食品 id 键名 */
    public static final String EXTRA_FOOD_ID = "extra_food_id";

    private int foodId;

    // ── Views ─────────────────────────────────────────────
    private TextView  tvName, tvLocation, tvPrice, tvSellTime,
                      tvDescription, tvTags, tvRatingCount;
    private RatingBar ratingBarDisplay, ratingBarInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // 后退按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 从 Intent 获取食品 id
        foodId = getIntent().getIntExtra(EXTRA_FOOD_ID, -1);
        if (foodId == -1) {
            finish();
            return;
        }

        // ── 绑定 Views ────────────────────────────────────
        tvName          = findViewById(R.id.tv_detail_name);
        tvLocation      = findViewById(R.id.tv_detail_location);
        tvPrice         = findViewById(R.id.tv_detail_price);
        tvSellTime      = findViewById(R.id.tv_detail_sell_time);
        tvDescription   = findViewById(R.id.tv_detail_description);
        tvTags          = findViewById(R.id.tv_detail_tags);
        tvRatingCount   = findViewById(R.id.tv_detail_rating_count);
        ratingBarDisplay = findViewById(R.id.rating_bar_display);
        ratingBarInput   = findViewById(R.id.rating_bar_input);

        FoodRepository.instance.getFoodById(foodId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(food -> {
                    if(food == null) {
                        runOnUiThread(() -> Toast.makeText(this, "食品不存在", Toast.LENGTH_SHORT).show());
                        finish();
                        return;
                    }
                    setTitle(food.getName());
                    tvName.setText(food.getName());
                    tvLocation.setText(food.getFullLocation());
                    tvPrice.setText(String.format("¥%.1f", food.getPrice()));
                    tvSellTime.setText("售卖时间：" + food.getSellTime());
                    tvDescription.setText(food.getDescription());
                    List<String> tags = food.getTags();
                    tvTags.setText("标签：" + ((tags != null && !tags.isEmpty())
                            ? String.join("  ·  ", tags) : "暂无"));
                    ratingBarDisplay.setRating(food.getAverageRating());
                    tvRatingCount.setText(food.getRatingCount() + " 人评价，综合 "
                            + String.format("%.1f", food.getAverageRating()) + " 分");
                }, throwable -> {
                    System.out.println(foodId + "详情加载失败: " + throwable.getMessage());
                    runOnUiThread(() -> Toast.makeText(this, "加载食品详情失败", Toast.LENGTH_SHORT).show());
                });

        // ── 用户打分 ──────────────────────────────────────
        ratingBarInput.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser && rating > 0) {
                FoodRepository.instance.addRating(foodId, rating);
                Toast.makeText(this, "感谢您的评分！", Toast.LENGTH_SHORT).show();
                // 防止重复评分（简单方案：评完后置零并禁用）
                bar.setIsIndicator(true);
            }
        });

        // ── 关联帖子 RecyclerView ─────────────────────────
        RecyclerView rvPosts = findViewById(R.id.rv_related_posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        /*PostAdapter postAdapter = new PostAdapter(post -> {
            // 跳转帖子详情（TODO：实现 PostDetailActivity）
            postViewModel.selectPost(post.getId());
        }, post -> {
            // 长按删除（简单方案，生产环境应鉴权）
        });
        rvPosts.setAdapter(postAdapter);*/

        //postViewModel.getPostsByFood(foodId).observe(this, foodWithPosts -> {
        //    postAdapter.submitList(foodWithPosts.posts);
        //});
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
