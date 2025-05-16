package com.example.caratexpense.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.caratexpense.database.Converters;
import androidx.room.Index;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "goals",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "categoryId")})
@TypeConverters(Converters.class)

public class Goal implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private double targetAmount;
    private double currentAmount;
    private Date deadline;
    private boolean isCompleted;
    private String note;
    private long categoryId;
    @Ignore
    private Category category;

    private double completionPercentage;

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Goal(long id, String title, double targetAmount, double currentAmount, Date deadline, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.isCompleted = isCompleted;
        this.note = "";
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public double getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public double getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }
    
    public Date getDeadline() {
        return deadline;
    }
    
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    
    public String getNote() {
        return note != null ? note : "";
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public double getProgressPercentage() {
        if (targetAmount <= 0) {
            return 0;
        }
        return (currentAmount / targetAmount) * 100;
    }
}
