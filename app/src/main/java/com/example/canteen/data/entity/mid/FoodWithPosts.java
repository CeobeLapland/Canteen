package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.Post;

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