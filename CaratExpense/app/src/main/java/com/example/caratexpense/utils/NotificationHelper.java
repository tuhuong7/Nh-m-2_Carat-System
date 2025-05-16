package com.example.caratexpense.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.models.PaymentReminder;

import java.util.Calendar;

public class NotificationHelper {
    private static final String CHANNEL_ID = "payment_reminder_channel";
    private static final String CHANNEL_NAME = "Payment Reminders";
    private static final String CHANNEL_DESCRIPTION = "Notifications for payment reminders";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public static void scheduleNotification(Context context, int notificationId, String content, long triggerTime, String cycle) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Kiểm tra nếu app không được phép đặt lịch báo chính xác
            if (!alarmManager.canScheduleExactAlarms()) {
                // Có thể ghi log, hiển thị cảnh báo hoặc fallback nếu muốn
                return;
            }
        }

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("content", content);
        intent.putExtra("cycle", cycle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        try {
            if (cycle.equals(PaymentReminder.CYCLE_ONCE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else if (cycle.equals(PaymentReminder.CYCLE_DAILY)) {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            } else if (cycle.equals(PaymentReminder.CYCLE_MONTHLY)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace(); // Log nếu quyền bị từ chối
        }
    }

    public static void cancelNotification(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

    public static void showNotification(Context context, int notificationId, String content) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nhắc nhở thanh toán")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace(); // Nếu chưa cấp quyền POST_NOTIFICATIONS
        }
    }

    public static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int notificationId = intent.getIntExtra("notificationId", 0);
            String content = intent.getStringExtra("content");
            String cycle = intent.getStringExtra("cycle");

            showNotification(context, notificationId, content);

            if (cycle != null && cycle.equals(PaymentReminder.CYCLE_MONTHLY)) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);

                scheduleNotification(
                        context,
                        notificationId,
                        content,
                        calendar.getTimeInMillis(),
                        cycle
                );
            }
        }
    }
}
