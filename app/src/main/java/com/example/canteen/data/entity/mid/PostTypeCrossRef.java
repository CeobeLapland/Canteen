package com.example.canteen.data.entity.mid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(
        tableName = "post_type_cross_ref",
        primaryKeys = {"post_id", "type_id"},
        indices = {@Index(value = "post_id"), @Index(value = "type_id")}
)
public class PostTypeCrossRef {
    @ColumnInfo(name = "post_id")
    public long postId;

    @ColumnInfo(name = "type_id")
    public long typeId;
}
