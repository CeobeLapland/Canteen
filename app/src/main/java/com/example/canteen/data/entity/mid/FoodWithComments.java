package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.Food;

import java.util.List;

public class FoodWithComments {
    @Embedded
    public Food food;

    @Relation(
        parentColumn = "id",
        entityColumn = "food_id"
    )
    public List<Comment> comments;
}
