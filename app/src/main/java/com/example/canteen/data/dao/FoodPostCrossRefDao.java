package com.example.canteen.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.canteen.data.entity.FoodPostCrossRef;

@Dao
public interface FoodPostCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCrossRef(FoodPostCrossRef crossRef);

    @Query("DELETE FROM food_post_cross_ref WHERE food_id = :foodId AND post_id = :postId")
    void deleteCrossRef(int foodId, int postId);
}