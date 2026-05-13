package com.example.canteen.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class PostWithComments {

    @Embedded
    public Post post;

    @Relation(
            parentColumn = "id",
            entityColumn = "post_id"
    )
    public List<Comment> comments;
}