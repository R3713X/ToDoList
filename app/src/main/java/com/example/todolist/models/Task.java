package com.example.todolist.models;

import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    private String text;


    private boolean isDone = false;
    private boolean isImportant = false;

    private boolean isRepeated = false;
    private String repeatFrequency = "";

    private String createdDateString;

    private boolean isDueDate = false;
    private String dueDateString = "";

    private boolean hasReminder = false;
    private String reminderDateTimeString = "";

    @Override
    public int hashCode() {
        return Integer.valueOf(key.substring(0,10).replaceAll("[^\\d.]", "").replace("-",""));
    }

    private String key = UUID.randomUUID().toString();
    private String listKey = "";


    public Task(String text) {
        this.text = text;
        Date createdDate = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        createdDateString = (createdDate.getYear() + 1900) + "-" + (createdDate.getMonth() + 1) + "-" + createdDate.getDate() + " " + calendar.getTimeInMillis();
    }

    public Task(String text, boolean isDueDate, String dueDateString) {
        this.text = text;
        this.isDueDate = isDueDate;
        this.dueDateString = dueDateString;
    }


    public Task(String text, String isDone, String isImportant, String isRepeated, String repeatFrequency, String createdDateString, String isDueDate, String dueDateString, String hasReminder, String reminderDateTimeString, String key, String listKey) {
        this.text = text;
        this.isDone = isDone.equals("1");
        this.isImportant = isImportant.equals("1");
        this.isRepeated = isRepeated.equals("1");
        this.repeatFrequency = repeatFrequency;
        this.createdDateString = createdDateString;
        this.isDueDate = isDueDate.equals("1");
        this.dueDateString = dueDateString;
        this.hasReminder = hasReminder.equals("1");
        this.reminderDateTimeString = reminderDateTimeString;
        this.key = key;
        this.listKey = listKey;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void setDueDate(boolean dueDate) {
        isDueDate = dueDate;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public void setRepeated(boolean repeated) {
        isRepeated = repeated;
    }

    public String getText() {
        return text;
    }

    public boolean isDueDate() {
        return isDueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public boolean isRepeated() {
        return isRepeated;
    }

    public String getKey() {
        return key;
    }


    public String getCreatedDateString() {
        return createdDateString;
    }

    public void setCreatedDateString(String createdDateString) {
        this.createdDateString = createdDateString;
    }

    public String getDueDateString() {
        return dueDateString;
    }

    public void setDueDateString(String dueDateString) {
        this.dueDateString = dueDateString;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getListKey() {
        return listKey;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public boolean hasReminder() {
        return hasReminder;
    }

    public void setHasReminder(boolean hasReminder) {
        this.hasReminder = hasReminder;
    }

    public String getReminderDateTimeString() {
        return reminderDateTimeString;
    }

    public void setReminderDateTimeString(String reminderDateTimeString) {
        this.reminderDateTimeString = reminderDateTimeString;
    }

    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(String repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public boolean isHasReminder() {
        return hasReminder;
    }
}
