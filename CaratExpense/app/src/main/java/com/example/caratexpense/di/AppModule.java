package com.example.caratexpense.di;

import android.content.Context;

import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.CategoryDao;
import com.example.caratexpense.database.dao.GoalDao;
import com.example.caratexpense.database.dao.PaymentReminderDao;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.database.dao.UserProfileDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInstance(context);
    }

    @Provides
    @Singleton
    public TransactionDao provideTransactionDao(AppDatabase appDatabase) {
        return appDatabase.transactionDao();
    }

    @Provides
    @Singleton
    public CategoryDao provideCategoryDao(AppDatabase appDatabase) {
        return appDatabase.categoryDao();
    }

    @Provides
    @Singleton
    public GoalDao provideGoalDao(AppDatabase appDatabase) {
        return appDatabase.goalDao();
    }

    @Provides
    @Singleton
    public UserProfileDao provideUserProfileDao(AppDatabase appDatabase) {
        return appDatabase.userProfileDao();
    }

    @Provides
    @Singleton
    public PaymentReminderDao providePaymentReminderDao(AppDatabase appDatabase) {
        return appDatabase.paymentReminderDao();
    }
}