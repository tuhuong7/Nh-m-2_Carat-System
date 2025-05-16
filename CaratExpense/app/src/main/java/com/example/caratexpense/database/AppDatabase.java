package com.example.caratexpense.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.caratexpense.database.dao.CategoryDao;
import com.example.caratexpense.database.dao.GoalDao;
import com.example.caratexpense.database.dao.PaymentReminderDao;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.database.dao.UserProfileDao;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.models.Goal;
import com.example.caratexpense.models.PaymentReminder;
import com.example.caratexpense.models.Transaction;
import com.example.caratexpense.models.UserProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Transaction.class, Category.class, Goal.class, UserProfile.class, PaymentReminder.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "carat_db";
    private static AppDatabase instance;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract GoalDao goalDao();
    public abstract UserProfileDao userProfileDao();
    public abstract PaymentReminderDao paymentReminderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            databaseWriteExecutor.execute(() -> {
                                seedDatabase();
                            });
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            databaseWriteExecutor.execute(() -> {
                                CategoryDao categoryDao = instance.categoryDao();
                                if (categoryDao.getAllCategories().isEmpty()) {
                                    seedDatabase();
                                }
                            });
                        }

                        private void seedDatabase() {
                            CategoryDao categoryDao = instance.categoryDao();
                            PaymentReminderDao paymentReminderDao = instance.paymentReminderDao();

                            // Expense categories
                            categoryDao.insert(new Category("Mua Sắm", true, "ic_shopping"));
                            categoryDao.insert(new Category("Đồ Ăn", true, "ic_food"));
                            categoryDao.insert(new Category("Điện Thoại", true, "ic_phone"));
                            categoryDao.insert(new Category("Giải Trí", true, "ic_entertainment"));
                            categoryDao.insert(new Category("Quần Áo", true, "ic_clothes"));
                            categoryDao.insert(new Category("Du Lịch", true, "ic_travel"));
                            categoryDao.insert(new Category("Sức Khỏe", true, "ic_health"));

                            // Income categories
                            categoryDao.insert(new Category("Lương", false, "ic_salary"));
                            categoryDao.insert(new Category("Trợ Cấp", false, "ic_allowance"));
                            categoryDao.insert(new Category("Đầu Tư", false, "ic_investment"));
                            categoryDao.insert(new Category("Tiền Thưởng", false, "ic_bonus"));

                            // Payment reminders
                            paymentReminderDao.insert(new PaymentReminder(0, "Tiền điện", "21/03/2025", PaymentReminder.CYCLE_MONTHLY, false));
                            paymentReminderDao.insert(new PaymentReminder(0, "Tiền nhà", "01/03/2025", PaymentReminder.CYCLE_MONTHLY, false));
                        }

                    })
                    .build();
        }
        return instance;
    }
}
