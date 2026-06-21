package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.canteen.data.entity.Post;
import com.example.canteen.data.entity.Type;

public class PostWithTypes {
    @Embedded
    public Post post;

    @Relation(
            parentColumn = "id",
            entityColumn = "post_id"
    )
    public java.util.List<Type> types;
}
