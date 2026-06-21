package com.example.canteen.data.entity.mid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(
        tableName = "food_tag_cross_ref",
        primaryKeys = {"food_id", "tag_id"},
        indices = {@Index(value = "food_id"), @Index(value = "tag_id")}
)
public class FoodTagCrossRef {
    @ColumnInfo(name = "food_id")
    public long foodId;

    @ColumnInfo(name = "tag_id")
    public long tagId;
}
