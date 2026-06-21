package com.example.canteen.data.entity.mid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import lombok.Getter;
import lombok.Setter;

//@SuppressWarnings("unused")
@Getter
@Setter
@Entity(
        tableName = "food_post_cross_ref",
        primaryKeys = {"food_id", "post_id"},
        indices = {@Index(value = "post_id"), @Index(value = "food_id")}
)
public class FoodPostCrossRef {

    @ColumnInfo(name = "food_id")
    public long foodId;

    @ColumnInfo(name = "post_id")
    public long postId;
}