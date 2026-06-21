package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

/**标签实体*/
@Getter
@Setter
@Entity(tableName = "tags")
public class Tag {

    /** 标签ID，主键，自增 */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /** 标签名称 */
    @ColumnInfo(name = "name")
    private String name;
}
