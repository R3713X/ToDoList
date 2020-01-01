package com.example.todolist.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;


import com.example.todolist.controllers.Controller_CRUD;
import com.example.todolist.models.Task;

import java.util.Objects;

public class AlertReceiver extends BroadcastReceiver {
    static int counter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationChannelHelper notificationChannelHelper = new NotificationChannelHelper(context);
        counter++;
        Bundle extras = intent.getExtras();
        String type = Objects.requireNonNull(extras).getString("type");
        if (type!=null) {
            String text = Objects.requireNonNull(extras.getString("text"));
            int id = Integer.valueOf(Objects.requireNonNull(Objects.requireNonNull(extras).getString("id")));

            if (type.equals("reminder")) {
                NotificationCompat.Builder nb = notificationChannelHelper.getReminderChannelNotification(
                        text);
                notificationChannelHelper.getManager().notify(id, nb.build());
            } else if (type.equals("due")) {

                Task key = new Controller_CRUD(context).getTaskFromKey(extras.getString("key"));
                if (!key.isDone()) {
                    NotificationCompat.Builder nb = notificationChannelHelper.getDuesChannelNotification(
                            text,
                            "This task was due today.");

                    notificationChannelHelper.getManager().notify(id, nb.build());
                }
            } else {
                Log.i("ERROR", "Notification Type not specified");
            }
        }
    }
}
