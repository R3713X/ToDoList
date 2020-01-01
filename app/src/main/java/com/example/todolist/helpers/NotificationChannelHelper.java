package com.example.todolist.helpers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.todolist.R;
import com.example.todolist.models.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationChannelHelper extends ContextWrapper {
    public static final String CHANNEL_REMINDERS = "channel_reminders";
    public static final String CHANNEL_DUES = "channel_dues";

    private NotificationManager mManager;


    public NotificationChannelHelper(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(CHANNEL_REMINDERS,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH);
        channel1.setDescription("To do list: Reminders Category");
        NotificationChannel channel2 = new NotificationChannel(CHANNEL_DUES,
                "Dues",
                NotificationManager.IMPORTANCE_LOW);
        channel2.setDescription("To do list: Due Tasks Category");
        NotificationManager manager = getSystemService(NotificationManager.class);
        List<NotificationChannel> notificationChannels = new ArrayList<>();
        notificationChannels.add(channel1);
        notificationChannels.add(channel2);
        manager.createNotificationChannels(notificationChannels);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getReminderChannelNotification(String title) {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_REMINDERS)
                .setContentTitle("Reminder")
                .setContentText(title)
                .setPriority(2)
                .setSmallIcon(R.drawable.ic_priority_high_black_24dp);
    }

    public NotificationCompat.Builder getDuesChannelNotification(String title, String description) {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_DUES)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_schedule_black_24dp);
    }

}
