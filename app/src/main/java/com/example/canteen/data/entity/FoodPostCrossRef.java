package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

//@SuppressWarnings("unused")
@Entity(
        tableName = "food_post_cross_ref",
        primaryKeys = {"food_id", "post_id"},
        indices = {@Index(value = "post_id"), @Index(value = "food_id")}
)
public class FoodPostCrossRef {

    @ColumnInfo(name = "food_id")
    public int foodId;

    @ColumnInfo(name = "post_id")
    public int postId;
}