package com.example.caratexpense.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.caratexpense.models.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC")
    List<Goal> getAllGoals();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Goal goal);
    
    @Update
    void update(Goal goal);
    
    @Delete
    void delete(Goal goal);
    
    @Query("SELECT * FROM goals WHERE id = :id")
    Goal getGoalById(long id);
}
