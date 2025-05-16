package com.example.caratexpense.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;
import java.util.List;

import com.example.caratexpense.models.TransactionWithCategory;

@Dao
public interface TransactionDao {

    @Insert
    long insert(com.example.caratexpense.models.Transaction transaction);

    @Update
    void update(com.example.caratexpense.models.Transaction transaction);

    @Delete
    void delete(com.example.caratexpense.models.Transaction transaction);

    // Lấy tất cả các giao dịch có danh mục theo thời gian (ngày, tháng, năm)
    @Transaction
    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    List<TransactionWithCategory> getAllTransactionsWithCategory();

    // Lọc các giao dịch chi tiêu theo thời gian
    @Transaction
    @Query("SELECT * FROM transactions WHERE isIncome = 0 ORDER BY dateTime DESC")
    List<TransactionWithCategory> getExpenseTransactions();

    // Lọc các giao dịch thu nhập theo thời gian
    @Transaction
    @Query("SELECT * FROM transactions WHERE isIncome = 1 ORDER BY dateTime DESC")
    List<TransactionWithCategory> getIncomeTransactions();

    // Lọc các giao dịch trong khoảng thời gian (ngày, tháng, năm)
    @Transaction
    @Query("SELECT * FROM transactions WHERE dateTime BETWEEN :startDate AND :endDate ORDER BY dateTime DESC")
    List<TransactionWithCategory> getTransactionsByPeriod(String startDate, String endDate);

    // Lọc giao dịch trong khoảng thời gian và theo danh mục
    @Transaction
    @Query("SELECT * FROM transactions WHERE dateTime BETWEEN :startDate AND :endDate AND categoryId = :categoryId ORDER BY dateTime DESC")
    List<TransactionWithCategory> getTransactionsByPeriodAndCategory(String startDate, String endDate, long categoryId);

    // Lọc giao dịch theo thời gian và danh mục với đầy đủ thông tin về danh mục
    @Transaction
    @Query("SELECT * FROM transactions WHERE dateTime BETWEEN :startDate AND :endDate AND categoryId = :categoryId ORDER BY dateTime DESC")
    List<TransactionWithCategory> getTransactionsWithCategoryByPeriodAndCategory(String startDate, String endDate, long categoryId);


    // Lọc giao dịch trong khoảng thời gian và có đầy đủ thông tin về danh mục
    @Transaction
    @Query("SELECT * FROM transactions WHERE dateTime BETWEEN :startDate AND :endDate")
    List<TransactionWithCategory> getTransactionsWithCategoryByPeriod(String startDate, String endDate);

    // Tổng thu nhập trong cơ sở dữ liệu
    @Query("SELECT IFNULL(SUM(amount), 0) FROM transactions WHERE isIncome = 1")
    double getTotalIncome();

    // Tổng chi tiêu trong cơ sở dữ liệu
    @Query("SELECT IFNULL(SUM(amount), 0) FROM transactions WHERE isIncome = 0")
    double getTotalExpense();


}
