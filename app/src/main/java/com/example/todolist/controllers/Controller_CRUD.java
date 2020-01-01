package com.example.todolist.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.todolist.helpers.DbHelper;
import com.example.todolist.models.Task;
import com.example.todolist.models.TaskList;
import com.example.todolist.models.TodoListContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Controller_CRUD {
    DbHelper dbHelper;
    SQLiteDatabase db;
    Context context;


    String[] taskProjections = {
            TodoListContract.taskEntry.COLUMN_NAME_TEXT,
            TodoListContract.taskEntry.COLUMN_NAME_IS_DONE,
            TodoListContract.taskEntry.COLUMN_NAME_IS_IMPORTANT,
            TodoListContract.taskEntry.COLUMN_NAME_CREATED_DATE,
            TodoListContract.taskEntry.COLUMN_NAME_IS_DUE,
            TodoListContract.taskEntry.COLUMN_NAME_DUE_DATE,
            TodoListContract.taskEntry.COLUMN_NAME_IS_REMINDER,
            TodoListContract.taskEntry.COLUMN_NAME_REMINDER_DATETIME,
            TodoListContract.taskEntry.COLUMN_NAME_IS_REPEATED,
            TodoListContract.taskEntry.COLUMN_NAME_REPETITION_FREQUENCY,
            TodoListContract.taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY,
            TodoListContract.taskEntry.COLUMN_NAME_LIST_FOREIGN_KEY
    };

    String[] taskListProjections = {
            TodoListContract.listEntry.COLUMN_NAME_TITLE,
            TodoListContract.listEntry.COLUMN_NAME_LIST_PRIMARY_KEY,
    };
    // How you want the results sorted in the resulting Cursor
    String sortASCOrder =
            TodoListContract.taskEntry.COLUMN_NAME_CREATED_DATE + " ASC";


    public Controller_CRUD(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public Context getContext() {
        return context;
    }

    public void saveTask(Task newTask) {
        ContentValues values = getTaskValues(newTask);
        long newRowId = db.insert(TodoListContract.taskEntry.TABLE_NAME, null, values);
        Log.i("TASK added", newRowId + " a");
    }


    public ArrayList<Task> readAllTasks() {
        Cursor cursor = db.query(
                TodoListContract.taskEntry.TABLE_NAME,   // The table to query
                taskProjections,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortASCOrder               // The sort order
        );
        return getTasksFromCursor(cursor);
    }

    public ArrayList<Task> readAllTodayTasks() {
        Calendar today = Calendar.getInstance();
        int day = today.getTime().getDate();
        int month = today.getTime().getMonth() + 1;
        int year = today.getTime().getYear() + 1900;

        String getYear = year + "-" + month + "-" + day;
        String selection = TodoListContract.taskEntry.COLUMN_NAME_CREATED_DATE + " LIKE \'" + getYear + "%\'";
        Cursor cursor = db.query(
                TodoListContract.taskEntry.TABLE_NAME,   // The table to query
                taskProjections,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortASCOrder               // The sort order
        );
        return getTasksFromCursor(cursor);
    }

    public ArrayList<Task> readAllTasksFromList(String listId) {
        String selection = TodoListContract.taskEntry.COLUMN_NAME_LIST_FOREIGN_KEY + " = ? ";
        String[] sellectionARgs = {listId};
        Cursor cursor = db.query(
                TodoListContract.taskEntry.TABLE_NAME,   // The table to query
                taskProjections,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                sellectionARgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        Log.i("s --- -", cursor.toString());
        return getTasksFromCursor(cursor);
    }

    public ArrayList<Task> readAllImportantTasks() {
        String selection = TodoListContract.taskEntry.COLUMN_NAME_IS_IMPORTANT + " = ?";
        String[] selectionArgs = {"1"};

        Cursor cursor = db.query(
                TodoListContract.taskEntry.TABLE_NAME,   // The table to query
                taskProjections,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortASCOrder               // The sort order
        );
        return getTasksFromCursor(cursor);
    }

    public ArrayList<Task> readAllPlannedTasks() {
        String selection = TodoListContract.taskEntry.COLUMN_NAME_IS_DUE + " = 1 " +
                "OR " + TodoListContract.taskEntry.COLUMN_NAME_IS_REMINDER + " = 1 ";

        Cursor cursor = db.query(
                TodoListContract.taskEntry.TABLE_NAME,   // The table to query
                taskProjections,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortASCOrder               // The sort order
        );
        return getTasksFromCursor(cursor);
    }

    public void deleteTask(Task task) {
        db.delete(TodoListContract.taskEntry.TABLE_NAME, TodoListContract.taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY + " = " + "\"" + task.getKey() + "\"", null);
        Log.i("Task DELETED", task.toString());
    }

    public void deleteList(String listKey) {
        db.delete(TodoListContract.taskEntry.TABLE_NAME, TodoListContract.taskEntry.COLUMN_NAME_LIST_FOREIGN_KEY + " = " + "\"" + listKey + "\"", null);
        db.delete(TodoListContract.listEntry.TABLE_NAME, TodoListContract.listEntry.COLUMN_NAME_LIST_PRIMARY_KEY + " = " + "\"" + listKey + "\"", null);
    }
    // Caution
    public void deleteAllTasks() {
        db.delete(TodoListContract.taskEntry.TABLE_NAME, null, null);
    }

    public void updateTask(Task updateTask) {
        ContentValues values = getTaskValues(updateTask);
        db.update(TodoListContract.taskEntry.TABLE_NAME, values, TodoListContract.taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY + " = " + "\"" + updateTask.getKey() + "\"", null);
    }

    public void saveList(TaskList newList) {
        ContentValues values = new ContentValues();

        values.put(TodoListContract.listEntry.COLUMN_NAME_TITLE, newList.getTitle());
        values.put(TodoListContract.listEntry.COLUMN_NAME_LIST_PRIMARY_KEY, newList.getKey());

        long newRowId = db.insert(TodoListContract.listEntry.TABLE_NAME, null, values);
        Log.i("List added", newRowId + " - " + values);
    }

    public List<TaskList> getAllTaskLists() {
        Cursor cursor = db.query(
                TodoListContract.listEntry.TABLE_NAME,   // The table to query
                taskListProjections,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        int titleIndex = cursor.getColumnIndex(TodoListContract.listEntry.COLUMN_NAME_TITLE);
        int keyIndex = cursor.getColumnIndex(TodoListContract.listEntry.COLUMN_NAME_LIST_PRIMARY_KEY);


        ArrayList<TaskList> taskLists = new ArrayList<>();
        while (cursor.moveToNext()) {
            String title = cursor.getString(titleIndex);
            String key = cursor.getString(keyIndex);

            TaskList taskList = new TaskList(title, key);
            taskLists.add(taskList);
        }
        cursor.close();

        return taskLists;
    }

    public Task getTaskFromKey(String key) {
        String selection = TodoListContract.taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY + " = ?";
        String[] selectionArgs = {key};

        Cursor cursor = db.query(
                TodoListContract.taskEntry.TABLE_NAME,   // The table to query
                taskProjections,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortASCOrder               // The sort order
        );

        return getTasksFromCursor(cursor).get(0);
    }

    public TaskList getListFromKey(String listKey) {
        String selection = TodoListContract.listEntry.COLUMN_NAME_LIST_PRIMARY_KEY + " = ?";
        String[] selectionArgs = {listKey};

        Cursor cursor = db.query(
                TodoListContract.listEntry.TABLE_NAME,   // The table to query
                taskListProjections,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null // The sort order
        );
        boolean b = cursor.moveToFirst();
        if (!b) {
            return null;
        }
        int titleIndex = cursor.getColumnIndex(TodoListContract.listEntry.COLUMN_NAME_TITLE);
        int keyIndex = cursor.getColumnIndex(TodoListContract.listEntry.COLUMN_NAME_LIST_PRIMARY_KEY);
        String title = cursor.getString(titleIndex);
        String key = cursor.getString(keyIndex);
        return new TaskList(title, key);
    }

    private ContentValues getTaskValues(Task task) {
        ContentValues values = new ContentValues();

        values.put(TodoListContract.taskEntry.COLUMN_NAME_TEXT, task.getText());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_IS_IMPORTANT, task.isImportant());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_IS_DONE, task.isDone());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_IS_DUE, task.isDueDate());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_DUE_DATE, task.getDueDateString());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_IS_REMINDER, task.hasReminder());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_REMINDER_DATETIME, task.getReminderDateTimeString());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_CREATED_DATE, task.getCreatedDateString());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_IS_REPEATED, task.isRepeated());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_REPETITION_FREQUENCY, task.getRepeatFrequency());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY, task.getKey());
        values.put(TodoListContract.taskEntry.COLUMN_NAME_LIST_FOREIGN_KEY, task.getListKey());

        return values;

    }

    private ArrayList<Task> getTasksFromCursor(Cursor cursor) {
        int textIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_TEXT);
        int isDoneIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_IS_DONE);
        int isImportantIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_IS_IMPORTANT);
        int createdDateIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_CREATED_DATE);
        int isDueIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_IS_DUE);
        int dueDateIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_DUE_DATE);
        int hasReminderIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_IS_REMINDER);
        int reminderDateTimeIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_REMINDER_DATETIME);
        int isRepeatedIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_IS_REPEATED);
        int repetitionFrequencyIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_REPETITION_FREQUENCY);
        int primaryKeyIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY);
        int foreignKeyIndex = cursor.getColumnIndex(TodoListContract.taskEntry.COLUMN_NAME_LIST_FOREIGN_KEY);

        ArrayList<Task> tasks = new ArrayList<>();
        while (cursor.moveToNext()) {
            String text = cursor.getString(textIndex);
            String isDone = cursor.getString(isDoneIndex);
            String isImportant = cursor.getString(isImportantIndex);
            String createdDate = cursor.getString(createdDateIndex);
            String isDue = cursor.getString(isDueIndex);
            String dueDate = cursor.getString(dueDateIndex);
            String hasReminder = cursor.getString(hasReminderIndex);
            String reminderDatetime = cursor.getString(reminderDateTimeIndex);
            String isRepeated = cursor.getString(isRepeatedIndex);
            String repetitionFrequency = cursor.getString(repetitionFrequencyIndex);
            String primaryKey = cursor.getString(primaryKeyIndex);
            String foreignKey = cursor.getString(foreignKeyIndex);
            Task dbReadTask = new Task(text, isDone, isImportant, isRepeated, repetitionFrequency, createdDate, isDue,
                    dueDate, hasReminder, reminderDatetime, primaryKey, foreignKey);
            tasks.add(dbReadTask);
        }
        cursor.close();
        return tasks;
    }
}
