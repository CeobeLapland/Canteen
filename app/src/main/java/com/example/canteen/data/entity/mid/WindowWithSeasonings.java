package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.canteen.data.entity.Seasoning;
import com.example.canteen.data.entity.Window;

import java.util.List;

public class WindowWithSeasonings {

    @Embedded
    public Window window;

    @Relation(
            parentColumn = "id",
            entityColumn = "window_id"
    )
    public List<Seasoning> seasonings;
}
