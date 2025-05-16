package com.example.caratexpense.utils;

import android.content.Context;

import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.CategoryDao;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.models.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class SampleDataGenerator {
    
    public static void generateSampleData(Context context) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            CategoryDao categoryDao = db.categoryDao();
            TransactionDao transactionDao = db.transactionDao();
            
            // Check if we already have data
            if (transactionDao.getAllTransactionsWithCategory().size() > 0) {

                return;
            }
            
            // Get existing categories
            Category foodCategory = null;
            Category beautyCategory = null;
            Category salaryCategory = null;
            
            for (Category category : categoryDao.getAllCategories()) {
                if (category.getName().equals("Đồ Ăn")) {
                    foodCategory = category;
                } else if (category.getName().equals("Sắc Khỏe")) {
                    beautyCategory = category;
                } else if (category.getName().equals("Lương")) {
                    salaryCategory = category;
                }
            }
            
            // Create sample transactions
            Calendar cal = Calendar.getInstance();
            
            // Current month transactions
            
            // Food expenses
            if (foodCategory != null) {
                // Day 25
                cal.set(Calendar.DAY_OF_MONTH, 25);
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 30);
                
                Transaction t1 = new Transaction(
                        0,
                        530000,
                        "Tiệc sinh nhật",
                        formatDate(cal.getTime()),
                        foodCategory.getId(),
                        foodCategory,
                        false
                );
                transactionDao.insert(t1);
                
                // Day 26
                cal.set(Calendar.DAY_OF_MONTH, 26);
                cal.set(Calendar.HOUR_OF_DAY, 18);
                cal.set(Calendar.MINUTE, 45);
                
                Transaction t2 = new Transaction(
                        0,
                        50000,
                        "Ăn tối",
                        formatDate(cal.getTime()),
                        foodCategory.getId(),
                        foodCategory,
                        false
                );
                transactionDao.insert(t2);
                
                // Another transaction on day 26
                cal.set(Calendar.HOUR_OF_DAY, 12);
                cal.set(Calendar.MINUTE, 15);
                
                Transaction t3 = new Transaction(
                        0,
                        50000,
                        "Ăn trưa",
                        formatDate(cal.getTime()),
                        foodCategory.getId(),
                        foodCategory,
                        false
                );
                transactionDao.insert(t3);
                
                // Day 27
                cal.set(Calendar.DAY_OF_MONTH, 27);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.MINUTE, 0);
                
                Transaction t4 = new Transaction(
                        0,
                        70000,
                        "Ăn sáng",
                        formatDate(cal.getTime()),
                        foodCategory.getId(),
                        foodCategory,
                        false
                );
                transactionDao.insert(t4);
            }
            
            // Beauty expenses
            if (beautyCategory != null) {
                // Day 25
                cal.set(Calendar.DAY_OF_MONTH, 25);
                cal.set(Calendar.HOUR_OF_DAY, 15);
                cal.set(Calendar.MINUTE, 0);
                
                Transaction t5 = new Transaction(
                        0,
                        530000,
                        "Spa",
                        formatDate(cal.getTime()),
                        beautyCategory.getId(),
                        beautyCategory,
                        false
                );
                transactionDao.insert(t5);
                
                // Day 26
                cal.set(Calendar.DAY_OF_MONTH, 26);
                cal.set(Calendar.HOUR_OF_DAY, 10);
                cal.set(Calendar.MINUTE, 30);
                
                Transaction t6 = new Transaction(
                        0,
                        50000,
                        "Mỹ phẩm",
                        formatDate(cal.getTime()),
                        beautyCategory.getId(),
                        beautyCategory,
                        false
                );
                transactionDao.insert(t6);
                
                // Another transaction on day 26
                cal.set(Calendar.HOUR_OF_DAY, 16);
                cal.set(Calendar.MINUTE, 45);
                
                Transaction t7 = new Transaction(
                        0,
                        50000,
                        "Dưỡng da",
                        formatDate(cal.getTime()),
                        beautyCategory.getId(),
                        beautyCategory,
                        false
                );
                transactionDao.insert(t7);
                
                // Day 27
                cal.set(Calendar.DAY_OF_MONTH, 27);
                cal.set(Calendar.HOUR_OF_DAY, 14);
                cal.set(Calendar.MINUTE, 15);
                
                Transaction t8 = new Transaction(
                        0,
                        70000,
                        "Làm móng",
                        formatDate(cal.getTime()),
                        beautyCategory.getId(),
                        beautyCategory,
                        false
                );
                transactionDao.insert(t8);
            }
            
            // Salary income
            if (salaryCategory != null) {
                // Beginning of month
                cal.set(Calendar.DAY_OF_MONTH, 5);
                cal.set(Calendar.HOUR_OF_DAY, 9);
                cal.set(Calendar.MINUTE, 0);
                
                Transaction t9 = new Transaction(
                        0,
                        2007000,
                        "Lương tháng",
                        formatDate(cal.getTime()),
                        salaryCategory.getId(),
                        salaryCategory,
                        true
                );
                transactionDao.insert(t9);
            }
        }).start();
    }
    
    private static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}
