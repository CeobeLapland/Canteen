package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.Post;

import java.util.List;

public class PostWithFoods {

    @Embedded
    public Post post;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(
                    value = FoodPostCrossRef.class,
                    parentColumn = "post_id",
                    entityColumn = "food_id"
            )
    )
    public List<Food> foods;
}