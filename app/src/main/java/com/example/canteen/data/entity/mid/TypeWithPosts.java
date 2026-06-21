package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.canteen.data.entity.Post;
import com.example.canteen.data.entity.Type;

public class TypeWithPosts {
    @Embedded
    public Type type;

    @Relation(
        parentColumn = "id",
        entityColumn = "type_id"
    )
    public java.util.List<Post> posts;
}
