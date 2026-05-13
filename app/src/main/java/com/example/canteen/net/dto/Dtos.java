package com.example.canteen.net.dto;

import java.io.Serializable;
//import java.time.*;

import java.util.List;
import java.util.Set;
public class Dtos {

    public class UserDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String name;
        private String createdAt; // ISO datetime string

        public UserDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        @Override
        public String toString() {
            return "UserDto{id=" + id + ", name='" + name + "', createdAt='" + createdAt + "'}";
        }
    }


    public class FoodSummaryDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String name;
        private Integer price;
        private String imageUrl;
        private String campus;
        private String canteen;
        private Float averageRating;

        public FoodSummaryDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCampus() { return campus; }
        public void setCampus(String campus) { this.campus = campus; }
        public String getCanteen() { return canteen; }
        public void setCanteen(String canteen) { this.canteen = canteen; }
        public Float getAverageRating() { return averageRating; }
        public void setAverageRating(Float averageRating) { this.averageRating = averageRating; }

        @Override
        public String toString() {
            return "FoodSummaryDto{id=" + id + ", name='" + name + "', price=" + price + "}";
        }
    }


    public class FoodDetailDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String name;
        private String description;
        private Integer price;
        private String imageUrl;
        private String createdAt;
        private String campus;
        private String canteen;
        private String floor;
        private String window;
        private String sellTime;
        private List<String> tags;
        private Float averageRating;
        private Integer ratingCount;
        private int postCount;

        public FoodDetailDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
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
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Float getAverageRating() { return averageRating; }
        public void setAverageRating(Float averageRating) { this.averageRating = averageRating; }
        public Integer getRatingCount() { return ratingCount; }
        public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
        public int getPostCount() { return postCount; }
        public void setPostCount(int postCount) { this.postCount = postCount; }

        @Override
        public String toString() {
            return "FoodDetailDto{id=" + id + ", name='" + name + "'}";
        }
    }


    public class PostSummaryDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String title;
        private Integer viewCount;
        private Integer likeCount;
        private UserDto author;
        private List<FoodSummaryDto> foods;
        private int commentCount;
        private String createdAt;

        public PostSummaryDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getViewCount() { return viewCount; }
        public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public UserDto getAuthor() { return author; }
        public void setAuthor(UserDto author) { this.author = author; }
        public List<FoodSummaryDto> getFoods() { return foods; }
        public void setFoods(List<FoodSummaryDto> foods) { this.foods = foods; }
        public int getCommentCount() { return commentCount; }
        public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        @Override
        public String toString() {
            return "PostSummaryDto{id=" + id + ", title='" + title + "'}";
        }
    }


    public class PostDetailDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String title;
        private String content;
        private Integer viewCount;
        private Integer likeCount;
        private UserDto author;
        private Set<FoodSummaryDto> foods;
        private List<CommentDto> comments;
        private String createdAt;
        private String updatedAt;

        public PostDetailDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Integer getViewCount() { return viewCount; }
        public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public UserDto getAuthor() { return author; }
        public void setAuthor(UserDto author) { this.author = author; }
        public Set<FoodSummaryDto> getFoods() { return foods; }
        public void setFoods(Set<FoodSummaryDto> foods) { this.foods = foods; }
        public List<CommentDto> getComments() { return comments; }
        public void setComments(List<CommentDto> comments) { this.comments = comments; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        @Override
        public String toString() {
            return "PostDetailDto{id=" + id + ", title='" + title + "'}";
        }
    }


    public class CommentDto implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;
        private String content;
        private UserDto author;
        private String createdAt;

        public CommentDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public UserDto getAuthor() { return author; }
        public void setAuthor(UserDto author) { this.author = author; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        @Override
        public String toString() {
            return "CommentDto{id=" + id + ", content='" + content + "'}";
        }
    }

}