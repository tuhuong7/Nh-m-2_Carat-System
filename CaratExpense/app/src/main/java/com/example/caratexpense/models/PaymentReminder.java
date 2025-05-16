package com.example.caratexpense.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "payment_reminders")
public class PaymentReminder implements Serializable {
    public static final String CYCLE_ONCE = "Chỉ 1 lần";
    public static final String CYCLE_DAILY = "Hàng ngày";
    public static final String CYCLE_MONTHLY = "Hàng tháng";
    
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String content;
    private String reminderDate;
    private String cycle;
    private boolean isRead;
    
    public PaymentReminder(long id, String content, String reminderDate, String cycle, boolean isRead) {
        this.id = id;
        this.content = content;
        this.reminderDate = reminderDate;
        this.cycle = cycle;
        this.isRead = isRead;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getReminderDate() {
        return reminderDate;
    }
    
    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    public String getCycle() {
        return cycle;
    }
    
    public void setCycle(String cycle) {
        this.cycle = cycle;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
}
