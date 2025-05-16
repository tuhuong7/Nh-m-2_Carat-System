package com.example.caratexpense.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.caratexpense.models.UserProfile;

@Dao
public interface UserProfileDao {
    @Query("SELECT * FROM user_profile LIMIT 1")
    UserProfile getUserProfile();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserProfile userProfile);
    
    @Update
    void update(UserProfile userProfile);
}
