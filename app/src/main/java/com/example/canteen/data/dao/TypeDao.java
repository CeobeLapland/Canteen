package com.example.canteen.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.canteen.data.entity.Type;

@Dao
public interface TypeDao {

    //基本增删改查
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    long insert(Type type);

    @Delete
    void delete(Type type);


}
