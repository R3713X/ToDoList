package com.example.todolist.models;

import android.provider.BaseColumns;

public final class TodoListContract {
    // To prevent someone from accidentally instantiating the contract class,
    // the constructor is private.
    private TodoListContract() {
    }

    public static final String SQL_CREATE_ENTRIES_TASK =
            "CREATE TABLE IF NOT EXISTS " + taskEntry.TABLE_NAME + " (" +
                    taskEntry.COLUMN_NAME_TEXT + " VARCHAR(200)," +
                    taskEntry.COLUMN_NAME_IS_IMPORTANT + " BIT," +
                    taskEntry.COLUMN_NAME_IS_DONE + " BIT," +
                    taskEntry.COLUMN_NAME_CREATED_DATE + " VARCHAR(200)," +
                    taskEntry.COLUMN_NAME_IS_DUE + " BIT," +
                    taskEntry.COLUMN_NAME_DUE_DATE + " VARCHAR(200)," +
                    taskEntry.COLUMN_NAME_IS_REMINDER + " BIT," +
                    taskEntry.COLUMN_NAME_REMINDER_DATETIME + " VARCHAR(200)," +
                    taskEntry.COLUMN_NAME_IS_REPEATED + " BIT," +
                    taskEntry.COLUMN_NAME_REPETITION_FREQUENCY + " VARCHAR(200)," +
                    taskEntry.COLUMN_NAME_TASK_PRIMARY_KEY + " VARCHAR(200)," +
                    taskEntry.COLUMN_NAME_LIST_FOREIGN_KEY + " VARCHAR(200))";

    public static final String SQL_CREATE_ENTRIES_LIST =
            "CREATE TABLE IF NOT EXISTS " + listEntry.TABLE_NAME + " (" +
                    listEntry.COLUMN_NAME_TITLE + " VARCHAR(200)," +
                    listEntry.COLUMN_NAME_LIST_PRIMARY_KEY + " VARCHAR(200)," +
                    listEntry.COLUMN_NAME_GROUP_FOREIGN_KEY + " VARCHAR(200))";

    public static final String SQL_DELETE_ENTRIES_TASK =
            "DROP TABLE IF EXISTS " + taskEntry.TABLE_NAME;
    public static final String SQL_DELETE_ENTRIES_LIST =
            "DROP TABLE IF EXISTS " + listEntry.TABLE_NAME;
    public static final String SQL_DELETE_ENTRIES_GROUP =
            "DROP TABLE IF EXISTS " + listEntry.TABLE_NAME;

    /* Inner class that defines the table contents */
    public static class taskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_IS_IMPORTANT = "is_important";
        public static final String COLUMN_NAME_CREATED_DATE = "created_date";
        public static final String COLUMN_NAME_IS_DONE = "is_done";
        public static final String COLUMN_NAME_IS_DUE = "is_due";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_IS_REMINDER = "is_reminder";
        public static final String COLUMN_NAME_REMINDER_DATETIME = "reminder_datetime";
        public static final String COLUMN_NAME_IS_REPEATED = "is_repeated";
        public static final String COLUMN_NAME_REPETITION_FREQUENCY = "repetition_frequency";
        public static final String COLUMN_NAME_TASK_PRIMARY_KEY = "task_KEY";
        public static final String COLUMN_NAME_LIST_FOREIGN_KEY = "list_KEY";
    }

    public static class listEntry implements BaseColumns {
        public static final String TABLE_NAME = "lists";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LIST_PRIMARY_KEY = "list_KEY";
        public static final String COLUMN_NAME_GROUP_FOREIGN_KEY = "group_KEY";
    }

}