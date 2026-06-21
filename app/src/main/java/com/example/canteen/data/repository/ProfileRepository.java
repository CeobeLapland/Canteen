package com.example.canteen.data.repository;

import android.app.Application;

import com.example.canteen.data.entity.FeedPostItem;
import com.example.canteen.data.entity.FeedType;
import com.example.canteen.data.entity.Post;
import com.example.canteen.data.entity.ProfileInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class ProfileRepository {

    private static volatile ProfileRepository instance;
    public static ProfileRepository getInstance() {
        return instance;
    }

    public ProfileRepository(Application application) {
        instance = this;
    }

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public Single<ProfileInfo> loadMyProfile() {
        return Single.fromCallable(() -> new ProfileInfo(
                null,
                "小刻",
                307193467L,
                "2026-06-20 12:30",
                "普通用户",
                0,
                0,
                0,
                0
        ));
    }


    public Single<List<FeedPostItem>> loadFeedPosts(FeedType type, int page, int pageSize) {
        return Single.fromCallable(() -> {
            // 示例：前 3 页有数据，后面返回空，方便你测试“无缝追加”
            if (page >= 3) {
                return new ArrayList<>();
            }

            List<FeedPostItem> result = new ArrayList<>();
            int startIndex = page * pageSize;

            for (int i = 0; i < pageSize; i++) {
                int index = startIndex + i + 1;

                String prefix;
                switch (type) {
                    case HISTORY:
                        prefix = "历史浏览";
                        break;
                    case FAVORITES:
                        prefix = "收藏";
                        break;
                    default:
                        prefix = "我发的";
                        break;
                }

                Post post = new Post();
                // 如果你的 Post 没有 setter，这里改成你自己的构造方式/赋值方式
                // 这里只演示字段思路
                // post.setId(index);
                // post.setAuthorName("作者" + index);
                // post.setTitle(prefix + "标题 " + index);
                // post.setViewCount(100 + index);
                // post.setLikeCount(20 + index);
                // post.setCommentCount(5 + index);

                // 由于你贴的是部分字段，这里给你一个更通用的写法：
                // 如果你的实体已有 setter，就直接用 setter。
                // 如果没有 setter，就建议给 Post 补一个构造器或者 builder。

                // ——为了让示例完整，这里假设你已经有 setter——
                post.setId(index);
                post.setAuthorName("作者" + index);
                post.setTitle(prefix + "标题 " + index);
                post.setContent("内容略");
                post.setCreatedAt(LocalDateTime.now().minusHours(index));
                post.setViewCount(100 + index);
                post.setLikeCount(20 + index);
                post.setCommentCount(5 + index);

                String browseTime = LocalDateTime.now().minusDays(i).format(TIME_FORMATTER);
                result.add(new FeedPostItem(post, browseTime));
            }
            return result;
        });
    }
}
