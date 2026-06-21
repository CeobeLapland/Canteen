package com.example.canteen.data.entity.mid;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.canteen.data.entity.Comment;
import com.example.canteen.data.entity.Post;

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