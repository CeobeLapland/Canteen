package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.Tag;

import java.util.List;

public class FoodWithTags {
    @Embedded
    public Food food;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = FoodTagCrossRef.class,
                    parentColumn = "food_id",
                    entityColumn = "tag_id"
            )
    )
    public List<Tag> tags;

    // 把标签列表转换成字符串列表，方便显示
    public List<String> toStringList() {
        // 版本太低用不了toList方法，手动转换一下
        List<String> tagNames = new java.util.ArrayList<>();
        for (Tag tag : tags) {
            tagNames.add(tag.getName());
        }
        return tagNames;
    }
}
