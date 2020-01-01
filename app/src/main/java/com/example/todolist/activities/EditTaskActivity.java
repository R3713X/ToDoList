package com.example.todolist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.todolist.R;
import com.example.todolist.controllers.Controller_CRUD;
import com.example.todolist.helpers.DialogHelper;
import com.example.todolist.helpers.NotificationHelper;
import com.example.todolist.models.Task;
import com.example.todolist.models.TaskList;

import static com.example.todolist.helpers.DateHelper.getPresentableDateStringFromDatabaseDate;
import static com.example.todolist.helpers.DateHelper.getPresentableDateTimeStringFromDatabaseDateTime;

public class EditTaskActivity extends AppCompatActivity {

    private TextView taskTextView;
    private TextView listTextView;
    private ToggleButton reminderToggleButton;
    private ToggleButton dueToggleButton;
    private ToggleButton repeatToggleButton;
    private TextView createdTextView;

    private ToggleButton isImportantToggleButton;
    private CheckBox isDoneCheckBox;
    private Button goBackButton;
    private Button saveButton;
    private DialogHelper dialogHelper;

    private Controller_CRUD crudController;
    private String repeatFrequencyPresentationDatabaseText;
    private String dueDateDatabaseText;
    private String dueDatePresentationText;
    private String reminderDateTimeDatabaseText;
    private String reminderDayTimePresentationText;

    private NotificationHelper notificationHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);


        crudController = new Controller_CRUD(getApplicationContext());
        notificationHelper = new NotificationHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        String key = extras.getString("key");
        final Task selectedTask = crudController.getTaskFromKey(key);
        TaskList listFromKey = crudController.getListFromKey(selectedTask.getListKey());

        taskTextView = findViewById(R.id.taskTextView);
        listTextView = findViewById(R.id.listNameTextView);
        reminderToggleButton = findViewById(R.id.reminderBTextView);
        dueToggleButton = findViewById(R.id.dueBTextView);
        repeatToggleButton = findViewById(R.id.repeatBTextView);
        createdTextView = findViewById(R.id.createdTextView);


        taskTextView.setText(selectedTask.getText());
        if (listFromKey != null) {
            listTextView.setText(listFromKey.getTitle());
        }

        if (!selectedTask.getReminderDateTimeString().isEmpty()) {
            reminderDateTimeDatabaseText = selectedTask.getReminderDateTimeString();
            reminderToggleButton.setChecked(true);
            reminderToggleButton.setText("Reminder: " + getPresentableDateTimeStringFromDatabaseDateTime(selectedTask.getReminderDateTimeString()));
        } else {
            reminderDateTimeDatabaseText = "";
            reminderToggleButton.setChecked(false);
            reminderToggleButton.setText("Add a Remi nder");
        }


        if (!selectedTask.getDueDateString().isEmpty()) {
            dueDateDatabaseText = selectedTask.getDueDateString();
            dueToggleButton.setChecked(true);
            dueToggleButton.setText("Due: " + getPresentableDateStringFromDatabaseDate(selectedTask.getDueDateString()));
        } else {
            dueDateDatabaseText = "";
            dueToggleButton.setChecked(false);
            dueToggleButton.setText("Add a Due Date");
        }
        if (!selectedTask.getRepeatFrequency().isEmpty()) {
            repeatFrequencyPresentationDatabaseText = selectedTask.getRepeatFrequency();
            repeatToggleButton.setChecked(true);
            repeatToggleButton.setText("Repeat " + selectedTask.getRepeatFrequency());
        } else {
            repeatFrequencyPresentationDatabaseText = "";
            repeatToggleButton.setChecked(false);
            repeatToggleButton.setText("Repeat");
        }

        createdTextView.setText("Created on " + getPresentableDateStringFromDatabaseDate(selectedTask.getCreatedDateString().split(" ")[0]));
        prepareDialogs();

        isImportantToggleButton = findViewById(R.id.isImportantToggleButton);
        isDoneCheckBox = findViewById(R.id.isDoneCheckBox);

        isImportantToggleButton.setChecked(selectedTask.isImportant());
        isImportantToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImportantToggleButton.isChecked()) {
                    selectedTask.setImportant(true);
                    isImportantToggleButton.setChecked(true);
                } else {
                    selectedTask.setImportant(false);
                    isImportantToggleButton.setChecked(false);
                }
                crudController.updateTask(selectedTask);
            }
        });

        isDoneCheckBox.setChecked(selectedTask.isDone());
        isDoneCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDoneCheckBox.isChecked()) {
                    selectedTask.setDone(true);
                    isDoneCheckBox.setChecked(true);
                } else {
                    selectedTask.setDone(false);
                    isDoneCheckBox.setChecked(false);
                }
                crudController.updateTask(selectedTask);
            }

        });

        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }

        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTask(selectedTask);
            }

        });

    }

    private void updateTask(Task updateTask) {
        prepareDialogResults();

        updateTask.setDueDate(dueDateDatabaseText != null && !dueDateDatabaseText.isEmpty());

        notificationHelper.cancelDueNotification(updateTask);
        updateTask.setDueDateString(dueDateDatabaseText);
        if (dueDateDatabaseText != null && !dueDateDatabaseText .isEmpty()){
            notificationHelper.setDueNotification(updateTask, true);
        }

            notificationHelper.cancelReminderNotification(updateTask);
        updateTask.setReminderDateTimeString(reminderDateTimeDatabaseText);
        if (reminderDateTimeDatabaseText != null && !reminderDateTimeDatabaseText.isEmpty()) {
            notificationHelper.setReminderNotification(updateTask, true);
        }


        updateTask.setRepeated(repeatFrequencyPresentationDatabaseText != null
                && !repeatFrequencyPresentationDatabaseText.isEmpty());
        updateTask.setRepeatFrequency(repeatFrequencyPresentationDatabaseText);

        crudController.updateTask(updateTask);
        Toast.makeText(EditTaskActivity.this, "Saved",
                Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private void prepareDialogs() {
        dialogHelper = new DialogHelper(EditTaskActivity.this, dueToggleButton, reminderToggleButton, repeatToggleButton, getLayoutInflater());
    }

    private void prepareDialogResults() {
        if (dialogHelper.repeatWasTouched()) {
            repeatFrequencyPresentationDatabaseText = dialogHelper.getRepeatFrequency();
        }

        if (dialogHelper.dueWasTouched()) {
            dueDateDatabaseText = dialogHelper.getDueDateDatabaseText();
            dueDatePresentationText = dialogHelper.getDueDatePresentationText();
        }

        if (dialogHelper.reminderWasTouched()) {
            reminderDateTimeDatabaseText = dialogHelper.getReminderDateTimeDatabaseText();
            reminderDayTimePresentationText = dialogHelper.getReminderDayTimePresentationText();
        }
    }
}
