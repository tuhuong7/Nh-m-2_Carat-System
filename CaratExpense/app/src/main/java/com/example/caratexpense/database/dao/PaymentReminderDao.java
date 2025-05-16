package com.example.caratexpense.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.caratexpense.models.PaymentReminder;

import java.util.List;

@Dao
public interface PaymentReminderDao {
    @Insert
    long insert(PaymentReminder paymentReminder);
    
    @Update
    void update(PaymentReminder paymentReminder);
    
    @Delete
    void delete(PaymentReminder paymentReminder);
    
    @Query("SELECT * FROM payment_reminders ORDER BY reminderDate ASC")
    List<PaymentReminder> getAllPaymentReminders();
    
    @Query("SELECT * FROM payment_reminders WHERE id = :id")
    PaymentReminder getPaymentReminderById(long id);
    
    @Query("SELECT COUNT(*) FROM payment_reminders WHERE isRead = 0")
    int getUnreadCount();
}
