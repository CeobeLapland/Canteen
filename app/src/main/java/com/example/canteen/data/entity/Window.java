package com.example.canteen.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "windows")
public class Window {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "campus_name")
    private String campusName;

    @ColumnInfo(name = "canteen_name")
    private String canteenName;

    @ColumnInfo(name = "floor_name")
    private String floorName;
}
