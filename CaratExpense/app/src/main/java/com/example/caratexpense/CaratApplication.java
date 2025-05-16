package com.example.caratexpense;

import android.app.Application;

import com.example.caratexpense.utils.NotificationHelper;

public class CaratApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);
    }
}
