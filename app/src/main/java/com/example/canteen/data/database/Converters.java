package com.example.canteen.data.database;

import com.example.canteen.net.dto.Dtos;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.room.TypeConverter;
import java.util.List;

import java.util.stream.Collectors;
import com.example.canteen.data.entity.*;
import com.example.canteen.data.entity.mid.*;
import com.example.canteen.net.dto.SyncDto.*;

public class Converters {

    // List<String> 转 JSON 字符串
    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null)
            return null;
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    // JSON 字符串 转回 List<String>
    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null)
            return null;
        Gson gson = new Gson();
        TypeToken<List<String>> token = new TypeToken<List<String>>() {};
        return gson.fromJson(value, token.getType());
    }


    // LocalDateTime 转 Long（Unix 时间戳，毫秒）
    @TypeConverter
    public static Long fromLocalDateTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // Long（Unix 时间戳，毫秒） 转回 LocalDateTime
    @TypeConverter
    public static java.time.LocalDateTime toLocalDateTime(Long timestamp) {
        if (timestamp == null)
            return null;
        return java.time.Instant.ofEpochMilli(timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    // List<Integer> 转 JSON 字符串
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null)
            return null;
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    // JSON 字符串 转回 List<Integer>
    @TypeConverter
    public static List<Integer> toIntegerList(String value) {
        if (value == null)
            return null;
        Gson gson = new Gson();
        TypeToken<List<Integer>> token = new TypeToken<List<Integer>>() {};
        return gson.fromJson(value, token.getType());
    }


    // ===================== FoodSyncDto -> Food =====================
    public static Food convert(FoodSyncDto dto) {
        if (dto == null) return null;
        Food entity = new Food();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setCampus(dto.getCampus());
        entity.setCanteen(dto.getCanteen());
        entity.setFloor(dto.getFloor());
        entity.setWindow(dto.getWindowId().toString());
        entity.setSellTime(dto.getSellTime());
        entity.setAverageRating(dto.getAverageRating());
        entity.setRatingCount(dto.getRatingCount());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static Food convert(Dtos.FoodDetailDto dto) {
        if (dto == null) return null;
        Food entity = new Food();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setCampus(dto.getCampus());
        entity.setCanteen(dto.getCanteen());
        entity.setFloor(dto.getFloor());
        entity.setWindow(dto.getWindow());//.toString());
        entity.setSellTime(dto.getSellTime());
        entity.setAverageRating(dto.getAverageRating());
        entity.setRatingCount(dto.getRatingCount());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Food> convertFoodList(List<FoodSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    public static List<Food> convertFoodDetailList(List<Dtos.FoodDetailDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== TagSyncDto -> Tag =====================
    public static Tag convert(TagSyncDto dto) {
        if (dto == null) return null;
        Tag entity = new Tag();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Tag> convertTagList(List<TagSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== WindowSyncDto -> Window =====================
    public static Window convert(WindowSyncDto dto) {
        if (dto == null) return null;
        Window entity = new Window();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCanteenName(dto.getCanteenName());
        entity.setCampusName(dto.getCampusName());
        entity.setFloorName(dto.getFloorName());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Window> convertWindowList(List<WindowSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== SeasoningSyncDto -> Seasoning =====================
    public static Seasoning convert(SeasoningSyncDto dto) {
        if (dto == null) return null;
        Seasoning entity = new Seasoning();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setWindowId(dto.getWindowId());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Seasoning> convertSeasoningList(List<SeasoningSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== PostSyncDto -> Post =====================
    public static Post convert(PostSyncDto dto) {
        if (dto == null) return null;
        Post entity = new Post();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setViewCount(dto.getViewCount());
        entity.setLikeCount(dto.getLikeCount());
        entity.setAuthorName(dto.getUserId().toString());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Post> convertPostList(List<PostSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== TypeSyncDto -> Type =====================
    public static Type convert(TypeSyncDto dto) {
        if (dto == null) return null;
        Type entity = new Type();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Type> convertTypeList(List<TypeSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== CommentSyncDto -> Comment =====================
    public static Comment convert(CommentSyncDto dto) {
        if (dto == null) return null;
        Comment entity = new Comment();
        entity.setId(dto.getId());
        entity.setContent(dto.getContent());
        entity.setAuthorName(dto.getUserId().toString());
        entity.setPostId(dto.getPostId());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<Comment> convertCommentList(List<CommentSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== FoodTagSyncDto -> FoodTagCrossRef 多对多中间表 =====================
    public static FoodTagCrossRef convert(FoodTagSyncDto dto) {
        if (dto == null) return null;
        FoodTagCrossRef entity = new FoodTagCrossRef();
        entity.setFoodId(dto.getFoodId());
        entity.setTagId(dto.getTagId());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<FoodTagCrossRef> convertFoodTagList(List<FoodTagSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== FoodPostSyncDto -> FoodPostCrossRef =====================
    public static FoodPostCrossRef convert(FoodPostSyncDto dto) {
        if (dto == null) return null;
        FoodPostCrossRef entity = new FoodPostCrossRef();
        entity.setFoodId(dto.getFoodId());
        entity.setPostId(dto.getPostId());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<FoodPostCrossRef> convertFoodPostList(List<FoodPostSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }

    // ===================== PostTypeSyncDto -> PostTypeCrossRef =====================
    public static PostTypeCrossRef convert(PostTypeSyncDto dto) {
        if (dto == null) return null;
        PostTypeCrossRef entity = new PostTypeCrossRef();
        entity.setPostId(dto.getPostId());
        entity.setTypeId(dto.getTypeId());
        // 忽略 createdAt、updatedAt
        return entity;
    }

    public static List<PostTypeCrossRef> convertPostTypeList(List<PostTypeSyncDto> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream()
                .map(Converters::convert)
                .collect(Collectors.toList());
    }
}