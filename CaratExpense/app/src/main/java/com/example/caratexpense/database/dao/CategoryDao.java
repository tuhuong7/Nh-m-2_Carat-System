package com.example.caratexpense.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.caratexpense.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);
    
    @Update
    void update(Category category);
    
    @Delete
    void delete(Category category);
    
    @Query("SELECT * FROM categories WHERE isExpense = 1")
    List<Category> getExpenseCategories();
    
    @Query("SELECT * FROM categories WHERE isExpense = 0")
    List<Category> getIncomeCategories();
    
    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(long id);
}
