package com.example.canteen.data.entity.mid;


import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.Tag;

import java.util.List;

public class TagWithFoods {
    @Embedded
    public Tag tag;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = FoodTagCrossRef.class,
                    parentColumn = "tag_id",
                    entityColumn = "food_id"
            )
    )
    public List<Food> foods;
}
