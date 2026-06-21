package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.canteen.data.entity.Food;
import com.example.canteen.data.entity.Window;

import java.util.List;

public class WindowWithFoods {
    @Embedded
    public Window window;

    @Relation(
            parentColumn = "id",
            entityColumn = "window"
    )
    public List<Food> foods;
}
