package com.example.todolist.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.todolist.models.Task;

import java.util.Calendar;

import static com.example.todolist.helpers.DateHelper.getCalendarFromDatabaseDate;
import static com.example.todolist.helpers.DateHelper.getCalendarFromDatabaseDateTime;

public final class NotificationHelper {

    private Context context;
    private AlarmManager alarmManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminderNotification(Task newTask, boolean isUpdate) {
        Calendar calendar = getCalendarFromDatabaseDateTime(newTask.getReminderDateTimeString());
        startReminderAlarm(calendar, newTask, isUpdate);
    }

    public void setDueNotification(Task newTask, boolean isUpdate) {
        Calendar calendar = getCalendarFromDatabaseDate(newTask.getDueDateString());
        startDueAlarm(calendar, newTask, isUpdate);
    }


    public void cancelReminderNotification(Task newTask) {
        cancelReminderAlarm(newTask);
    }

    public void cancelDueNotification(Task newTask) {
        cancelDueAlarm(newTask);
    }


    private void cancelReminderAlarm(Task newTask) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newTask.hashCode() + 1, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }

    private void cancelDueAlarm(Task newTask) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newTask.hashCode() - 1, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);
    }


    static int counter = 0;

    private void startReminderAlarm(Calendar c, Task newTask, boolean isUpdate) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("type", "reminder");
        intent.putExtra("text", newTask.getText());
        intent.putExtra("id", String.valueOf(newTask.hashCode()));
        PendingIntent pendingIntent;
        if (!isUpdate) {
            pendingIntent = PendingIntent.getBroadcast(context, newTask.hashCode() + 1, intent, PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, newTask.hashCode() + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        // just to not have them override each other ?
        c.set(Calendar.SECOND, counter);
        counter++;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void startDueAlarm(Calendar calendar, Task newTask, boolean isUpdate) {
        Intent intent = new Intent(context, AlertReceiver.class);
        intent.putExtra("type", "due");
        intent.putExtra("text", newTask.getText());
        intent.putExtra("key", newTask.getKey());
        intent.putExtra("id", String.valueOf(newTask.hashCode()));
        PendingIntent pendingIntent;
        if (!isUpdate) {
            pendingIntent = PendingIntent.getBroadcast(context, newTask.hashCode() - 1, intent, PendingIntent.FLAG_ONE_SHOT);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, newTask.hashCode() - 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
        // just to not have them override each other ?
        calendar.set(Calendar.SECOND, counter);
        counter++;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
