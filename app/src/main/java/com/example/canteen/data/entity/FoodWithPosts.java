package com.example.canteen.data.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class FoodWithPosts {

    @Embedded
    public Food food;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = FoodPostCrossRef.class,
                    parentColumn = "food_id",
                    entityColumn = "post_id"
            )
    )
    public List<Post> posts;
}