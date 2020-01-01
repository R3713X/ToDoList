package com.example.todolist.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todolist.activities.EditTaskActivity;
import com.example.todolist.R;
import com.example.todolist.controllers.Controller_CRUD;
import com.example.todolist.models.Task;

import java.util.ArrayList;

import static com.example.todolist.helpers.DateHelper.*;

public class TaskListAdapter extends ArrayAdapter<Task> {
    Context context;

    ArrayList<Task> tasks;
    Controller_CRUD crudController;

    public TaskListAdapter(Context context, ArrayList<Task> tasks) {
        super(context, R.layout.task_row, R.id.taskTextView, tasks);
        this.context = context;
        this.crudController = new Controller_CRUD(context);
        this.tasks = tasks;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = null;
        if (layoutInflater != null) {
            row = layoutInflater.inflate(R.layout.task_row, parent, false);
        }
        assert row != null;
        final CheckBox isTaskDoneCheckbox = row.findViewById(R.id.isTaskDoneCheckbox);
        final TextView taskTextView = row.findViewById(R.id.taskTextView);
        final TextView taskReminderTextView = row.findViewById(R.id.taskReminderTextView);
        final TextView taskDueTextView = row.findViewById(R.id.taskDueTextView);
        final ToggleButton isImportantButton = row.findViewById(R.id.isImportantToggleButton);
        final Task task = tasks.get(position);

        final String originalText = task.getText();

        if(!task.getReminderDateTimeString().equals("")){
            taskReminderTextView.setText("\uD83D\uDD14: "+ getPresentableDateTimeStringFromDatabaseDateTime(task.getReminderDateTimeString()));
        } else {
            taskReminderTextView.setText("");
        }


        if(!task.getDueDateString().equals("")){
            taskDueTextView.setText("\uD83D\uDCC5: "+ getPresentableDateStringFromDatabaseDate(task.getDueDateString()));
        } else {
            taskDueTextView.setText("");
        }


        isTaskDoneCheckbox.setChecked(task.isDone());
        if (!task.isDone()) {
            taskTextView.setText(originalText);
            taskTextView.setTextColor(Color.BLACK);
        } else {
            taskTextView.setTextColor(Color.GRAY);
            SpannableString content = new SpannableString(originalText);
            content.setSpan(new StrikethroughSpan(), 0, originalText.length(), 0);
            taskTextView.setText(content);

        }
        taskTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, EditTaskActivity.class);
                intent.putExtra("key", task.getKey());
                context.startActivity(intent);
                return false;
            }
        });
        isImportantButton.setChecked(task.isImportant());



        isImportantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImportantButton.isChecked()) {
                    task.setImportant(true);
                    isImportantButton.setChecked(true);
                } else {
                    task.setImportant(false);
                    isImportantButton.setChecked(false);
                }
                crudController.updateTask(task);
            }
        });

        isTaskDoneCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTaskDoneCheckbox.isChecked()) {
                    taskTextView.setTextColor(Color.GRAY);
                    SpannableString content = new SpannableString(originalText);
                    task.setDone(true);
                    content.setSpan(new StrikethroughSpan(), 0, originalText.length(), 0);
                    taskTextView.setText(content);
                } else {
                    taskTextView.setTextColor(Color.BLACK);
                    taskTextView.setText(originalText);
                    task.setDone(false);
                }
                crudController.updateTask(task);
            }
        });


        return row;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetInvalidated();
    }
}
