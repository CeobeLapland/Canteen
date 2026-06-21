package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "seasonings")
public class Seasoning {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "price")
    private Integer price;// 单位为分

    @ColumnInfo(name = "window_id")
    private Long windowId;
}
