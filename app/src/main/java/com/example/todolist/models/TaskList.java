package com.example.todolist.models;

import java.util.UUID;

public class TaskList {
    private String title;

    private String key = UUID.randomUUID().toString();

    public TaskList(String title) {
        this.title = title;
    }


    public TaskList(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
