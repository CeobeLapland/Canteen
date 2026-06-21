package com.example.canteen.net.request;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

import java.util.List;
//import java.math.BigDecimal;
import java.util.Set;

import lombok.Data;

public class Requests {

    public class TagRequest implements Serializable {
        @SerializedName("id")
        private Long id;

        @SerializedName("name")
        private String name;

        public TagRequest() {}

        public TagRequest(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }


    public class CreateUserRequest implements Serializable {
        @SerializedName("name")
        private String name;

        public CreateUserRequest() {}

        public CreateUserRequest(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("用户名不能为空");
            }
            int len = name.trim().length();
            if (len < 2 || len > 50) {
                throw new IllegalArgumentException("用户名长度须在 2~50 之间");
            }
        }
    }


    public class CreateFoodRequest implements Serializable {
        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("price")
        private Integer price;

        @SerializedName("imageUrl")
        private String imageUrl;

        @SerializedName("campus")
        private String campus;

        @SerializedName("canteen")
        private String canteen;

        @SerializedName("floor")
        private String floor;

        @SerializedName("window")
        private String window;

        @SerializedName("sellTime")
        private String sellTime;

        @SerializedName("tags")
        private List<TagRequest> tags;

        public CreateFoodRequest() {}

        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getCampus() { return campus; }
        public void setCampus(String campus) { this.campus = campus; }

        public String getCanteen() { return canteen; }
        public void setCanteen(String canteen) { this.canteen = canteen; }

        public String getFloor() { return floor; }
        public void setFloor(String floor) { this.floor = floor; }

        public String getWindow() { return window; }
        public void setWindow(String window) { this.window = window; }

        public String getSellTime() { return sellTime; }
        public void setSellTime(String sellTime) { this.sellTime = sellTime; }

        public List<TagRequest> getTags() { return tags; }
        public void setTags(List<TagRequest> tags) { this.tags = tags; }

        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("菜品名称不能为空");
            }
            if (name.length() > 100) {
                throw new IllegalArgumentException("菜品名称不超过 100 字");
            }
            if (description != null && description.length() > 1000) {
                throw new IllegalArgumentException("描述不超过 1000 字");
            }
            if (price == null) {
                throw new IllegalArgumentException("价格不能为空");
            }
            if (price < 0) {
                throw new IllegalArgumentException("价格必须大于 0");
            }
        }
    }



    /** 筛选菜品请求（GET 请求的查询参数） */
    public static class FilterFoodRequest implements Serializable
    {

        private String name;    // 按名称模糊匹配

        private String campus;  // 按校区精确匹配

        private String canteen; // 按食堂精确匹配

        private String floor;   // 按楼层精确匹配

        private String window;  // 按窗口精确匹配

        private List<String> tags;     // 按标签模糊匹配（至少包含一个标签）

        private Integer minPrice;   // 价格区间下限

        private Integer maxPrice;   // 价格区间上限
    }


    public class CreatePostRequest implements Serializable {
        @SerializedName("title")
        private String title;

        @SerializedName("content")
        private String content;

        @SerializedName("rating")
        private Integer rating;

        @SerializedName("userId")
        private Long userId;

        @SerializedName("foodIds")
        private Set<Long> foodIds;

        public CreatePostRequest() {}

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Set<Long> getFoodIds() { return foodIds; }
        public void setFoodIds(Set<Long> foodIds) { this.foodIds = foodIds; }

        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("标题不能为空");
            }
            if (title.length() > 200) {
                throw new IllegalArgumentException("标题不超过 200 字");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("内容不能为空");
            }
            if (rating != null) {
                if (rating < 1 || rating > 5) {
                    throw new IllegalArgumentException("评分范围 1~5");
                }
            }
            if (userId == null) {
                throw new IllegalArgumentException("作者 ID 不能为空");
            }
            if (foodIds == null || foodIds.isEmpty()) {
                throw new IllegalArgumentException("请至少关联一道菜品");
            }
        }
    }


    public class UpdatePostRequest implements Serializable {
        @SerializedName("title")
        private String title;

        @SerializedName("content")
        private String content;

        @SerializedName("rating")
        private Integer rating;

        @SerializedName("foodIds")
        private Set<Long> foodIds;

        public UpdatePostRequest() {}

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }

        public Set<Long> getFoodIds() { return foodIds; }
        public void setFoodIds(Set<Long> foodIds) { this.foodIds = foodIds; }

        public void validatePartial() {
            if (title != null && title.length() > 200) {
                throw new IllegalArgumentException("标题不超过 200 字");
            }
            if (rating != null && (rating < 1 || rating > 5)) {
                throw new IllegalArgumentException("评分范围 1~5");
            }
        }
    }


    public class CreateCommentRequest implements Serializable {
        @SerializedName("content")
        private String content;

        @SerializedName("userId")
        private Long userId;

        public CreateCommentRequest() {}

        public CreateCommentRequest(String content, Long userId) {
            this.content = content;
            this.userId = userId;
        }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public void validate() {
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("评论内容不能为空");
            }
            if (content.length() > 500) {
                throw new IllegalArgumentException("评论内容不超过 500 字");
            }
            if (userId == null) {
                throw new IllegalArgumentException("评论者 ID 不能为空");
            }
        }
    }


}