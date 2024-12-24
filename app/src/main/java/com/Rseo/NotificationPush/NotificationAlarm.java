package com.Rseo.NotificationPush;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.Rseo.R;

import java.util.Calendar;


public class NotificationAlarm {

    private static final String NOTIFICATION_CHANNEL_ID = "my_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "My Channel";
    private static final int NOTIFICATION_ID = 1;
    private static final String BARRE_NOTIFICATION_CHANNEL_ID = "your_channel_id";


    public static void scheduleNotification(Context context, Bundle bundle ) {

        cancelAlarmAndNotification(context);

        Intent intent = new Intent(context, NotificationRec.class);
        intent.putExtra("NOTIFICATION_BUNDLE", bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long repeatTime = 60*1000*60;

        if (alarmManager != null && repeatTime > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(), repeatTime, pendingIntent);
                } else {
                    // Use inexact alternatives as a fallback
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(), repeatTime, pendingIntent);
                }
            }else {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimeInMillis(), repeatTime, pendingIntent);
            }

        }
    }



    public static long getTimeInMillis() {
        // Crée une instance de Calendar avec la date actuelle
        Calendar calendar = Calendar.getInstance();

        // Régle l'heure sur 11:17 AM
        calendar.set(Calendar.HOUR_OF_DAY, 12); // 11 AM
        calendar.set(Calendar.MINUTE, 20);     // 17 minutes
        calendar.set(Calendar.SECOND, 0);      // 0 secondes
        calendar.set(Calendar.MILLISECOND, 0); // 0 millisecondes

        // Si l'heure est déjà passée, ajoute un jour
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
//            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.add(Calendar.HOUR_OF_DAY, 12);
        }

        return calendar.getTimeInMillis();

    }


    public static void showNotification(Context context,Bundle bundle ,Class destination) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        String calcule = bundle.getString("content","");
        String title = bundle.getString("title","");

        Intent intent = new Intent(context, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 11, intent, PendingIntent.FLAG_IMMUTABLE);

        builder
                .setSmallIcon(R.drawable.ic_icon)
                .setContentTitle(title)
                .setContentText(calcule)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    public static void cancelAlarmAndNotification(Context context) {
        Intent intent = new Intent(context, NotificationRec.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 11, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


}
