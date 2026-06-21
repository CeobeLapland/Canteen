package com.example.canteen.net.dto;


import lombok.Data;

import java.time.LocalDateTime;

/*
 * 这是从后端复制过来的DTO类，专门用于前端room数据库全量同步的DTO
 * 用于前端room数据库全量同步的DTO
 * 在这个文件里要把所有的实体信息和联系信息全量传给前端的缓存数据库，前端会根据这个DTO的结构来创建表和字段。
 * 所有实体都有id、createdAt、updatedAt等基础字段，后续如果有新的实体或者字段都要在这里添加。
 */
public class SyncDto {
    /**
     * Food 实体的全量信息
     */
    @Data
    public static class FoodSyncDto {
        private Long id;
        private String name;
        private String description;
        private Integer price;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private String campus;
        private String canteen;
        private String floor;
        // window存的是Id
        private Long windowId;

        private String sellTime;

        private Float averageRating;
        private Integer ratingCount;
    }


    /**
     * Tag 实体的全量信息
     */
    @Data
    public static class TagSyncDto {
        private Long id;
        private String name;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


    /**
     * Window 实体的全量信息
     */
    @Data
    public static class WindowSyncDto {
        private Long id;
        private String name;

        private String canteenName;
        private String campusName;
        private String floorName;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Seasoning 实体的全量信息
     */
    @Data
    public static class SeasoningSyncDto {
        private Long id;
        private String name;

        private Long windowId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Post 实体的全量信息
     */
    @Data
    public static class PostSyncDto {
        private Long id;
        private String title;
        private String content;

        private Integer viewCount;
        private Integer likeCount;

        private Long userId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Type 实体的全量信息
     */
    @Data
    public static class TypeSyncDto {
        private Long id;
        private String name;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Comment 实体的全量信息
     */
    @Data
    public static class CommentSyncDto {
        private Long id;
        private String content;

        private Long userId;
        private Long postId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    //User好像不用同步缓存
    //但还是先写个吧

    /**
     * User 实体的全量信息
     */
    @Data
    public static class UserSyncDto {
        private Long id;
        private String username;
        private String permission;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


    // 多对多的中间表信息

    /**
     * Food-Tag 关联信息
     */
    @Data
    public static class FoodTagSyncDto {
        private Long foodId;
        private Long tagId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Food-Post 关联信息
     */
    @Data
    public static class FoodPostSyncDto {
        private Long foodId;
        private Long postId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /**
     * Post-Type 关联信息
     */
    @Data
    public static class PostTypeSyncDto {
        private Long postId;
        private Long typeId;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


    /**
     * 整体Dto，即包含所有实体和关联信息的DTO，供前端全量同步使用
     */
    @Data
    public static class AllSyncDto {
        //先把所有privite全改成public，后续再根据需要改回private并添加getter/setter
        public java.util.List<FoodSyncDto> foods;
        public java.util.List<TagSyncDto> tags;
        public java.util.List<WindowSyncDto> windows;
        public java.util.List<SeasoningSyncDto> seasonings;
        public java.util.List<PostSyncDto> posts;
        public java.util.List<TypeSyncDto> types;
        public java.util.List<CommentSyncDto> comments;
        public java.util.List<UserSyncDto> users;

        public java.util.List<FoodTagSyncDto> foodTags;
        public java.util.List<FoodPostSyncDto> foodPosts;
        public java.util.List<PostTypeSyncDto> postTypes;
    }
}
