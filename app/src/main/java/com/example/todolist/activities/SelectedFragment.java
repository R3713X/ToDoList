package com.example.todolist.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.todolist.R;
import com.example.todolist.adapters.TaskListAdapter;
import com.example.todolist.controllers.Controller_CRUD;
import com.example.todolist.helpers.DialogHelper;
import com.example.todolist.helpers.NotificationHelper;
import com.example.todolist.models.SelectionType;
import com.example.todolist.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.todolist.models.SelectionType.DAY;
import static com.example.todolist.models.SelectionType.IMPORTANT;
import static com.example.todolist.models.SelectionType.LIST;
import static com.example.todolist.models.SelectionType.PLANNED;

public class SelectedFragment extends Fragment {
    private EditText typeTaskEditText;
    private LinearLayout addTaskLayout;
    private FloatingActionButton addTaskFab;
    private FloatingActionButton deleteListFab;
    SwipeMenuListView taskListView;

    private ArrayList<Task> tasks;
    private Controller_CRUD crudController;


    DialogHelper dialogHelper;
    /**
     * Due date selection
     */
    ToggleButton setDueDateButton;
    String dueDateDatabaseText;
    String dueDatePresentationText;

    /**
     * Reminder date selection
     */
    ToggleButton setReminderButton;
    String reminderDayTimePresentationText;
    String reminderDateTimeDatabaseText;
    NotificationHelper notificationHelper;

    /**
     * Repeat frequency selection
     */
    ToggleButton setRepeatFrequencyButton;
    String repeatFrequencyPresentationDatabaseText;

    private TaskListAdapter taskListAdapter;
    private int maxHeight = 0;

    SelectionType selectionType = DAY;

    String listKey;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.my_fragment, container, false);
        crudController = new Controller_CRUD(getContext());
        selectionType = SelectionType.get(getArguments().getString("type"));
        switch (selectionType) {
            case DAY:
                tasks = crudController.readAllTodayTasks();
                break;
            case LIST:
                listKey = Objects.requireNonNull(getArguments().getString("key"));
                tasks = crudController.readAllTasksFromList(listKey);
                break;
            case PLANNED:
                tasks = crudController.readAllPlannedTasks();
                break;
            case IMPORTANT:
                tasks = crudController.readAllImportantTasks();
                break;
            case ALL:
                tasks = crudController.readAllTasks();
                break;
        }
        closeKeyboard();
        inflate.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onLayoutChange(View v, int left, int top, int right
                    , int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int nowHeight = bottom - top;
                if (!isDisabledFromSelectionType()) {
                    if (maxHeight < nowHeight) {
                        maxHeight = nowHeight;
                    }
                    if (maxHeight > nowHeight) {
                        addTaskLayout.setVisibility(View.VISIBLE);
                        addTaskFab.setVisibility(View.INVISIBLE);
                        if (selectionType == LIST) {
                            deleteListFab.setVisibility(View.INVISIBLE);
                        }
                        typeTaskEditText.requestFocus();
                    } else {
                        addTaskLayout.setVisibility(View.INVISIBLE);
                        addTaskFab.setVisibility(View.VISIBLE);
                        if (selectionType == LIST) {
                            deleteListFab.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    addTaskFab.setVisibility(View.INVISIBLE);
                }
            }
        });

        return inflate;
    }

    private boolean isDisabledFromSelectionType() {
        return selectionType == PLANNED || selectionType == IMPORTANT;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            createAndSaveTask();
            cleanUp();

        }
    };

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void cleanUp() {
        typeTaskEditText.setText("");

        repeatFrequencyPresentationDatabaseText = "";
        setRepeatFrequencyButton.setChecked(false);
        setRepeatFrequencyButton.setText("Set Repeater");

        dueDateDatabaseText = "";
        setDueDateButton.setChecked(false);
        setDueDateButton.setText("Set Date");

        reminderDateTimeDatabaseText = "";
        setReminderButton.setChecked(false);
        setReminderButton.setText("Set Reminder");
    }

    private void createAndSaveTask() {
        if (taskTextIsEntered()) {
            prepareDialogResults();
            Task newTask = new Task(typeTaskEditText.getText().toString());
            if (dueDateDatabaseText != null && !dueDateDatabaseText.isEmpty()) {
                newTask.setDueDate(true);
                newTask.setDueDateString(dueDateDatabaseText);
                notificationHelper.setDueNotification(newTask, false);
            }
            if (reminderDateTimeDatabaseText != null && !reminderDateTimeDatabaseText.isEmpty()) {
                newTask.setReminderDateTimeString(reminderDateTimeDatabaseText);
                notificationHelper.setReminderNotification(newTask, false);
            }
            if (repeatFrequencyPresentationDatabaseText != null &&
                    !repeatFrequencyPresentationDatabaseText.isEmpty()) {
                newTask.setRepeated(true);
                newTask.setRepeatFrequency(repeatFrequencyPresentationDatabaseText);
            }
            if (selectionType == LIST) {
                newTask.setListKey(listKey);
            }

            tasks.add(newTask);
            crudController.saveTask(newTask);
            taskListAdapter.setTasks(tasks);
            taskListAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Please enter a task name", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean taskTextIsEntered() {
        return !typeTaskEditText.getText().toString().equals("");
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("activity-says-hi"));
        taskListAdapter = new TaskListAdapter(getContext(), tasks);
        notificationHelper = new NotificationHelper(getContext());

        addTaskLayout = view.findViewById(R.id.addTaskLayout);
        typeTaskEditText = view.findViewById(R.id.typeTaskEditText);
        addTaskFab = view.findViewById(R.id.addTaskFab);
        addTaskFab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                addTaskLayout.setVisibility(View.VISIBLE);
                addTaskFab.setVisibility(View.INVISIBLE);
                typeTaskEditText.requestFocus();

            }
        });
        deleteListFab = view.findViewById(R.id.deleteListFab);
        if (selectionType == LIST) {
            deleteListFab.setVisibility(View.VISIBLE);
        }
        deleteListFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                askIfTheyWantToDelete();
                return true;
            }
        });


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_sweep_black_24dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        taskListView = view.findViewById(R.id.taskListView);
        taskListView.setAdapter(taskListAdapter);
        taskListView.setMenuCreator(creator);
        taskListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Task taskToDelete = tasks.get(position);
                crudController.deleteTask(taskToDelete);
                tasks.remove(taskToDelete);
                if (!taskToDelete.getReminderDateTimeString().equals(""))
                    notificationHelper.cancelReminderNotification(taskToDelete);
                taskListView.setAdapter(taskListAdapter);
                return true;
            }
        });
        Button addTaskButton = view.findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndSaveTask();
                cleanUp();
            }
        });

        prepareDialogs();


    }

    private void askIfTheyWantToDelete() {
        createDeleteListDialogInterface().show();
    }

    private AlertDialog createDeleteListDialogInterface() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_list_dialog_layout, null);
        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                })
                .setPositiveButton("Delete List", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteList();
                        goBackToMyDay();
                    }
                });

        return builder.create();
    }

    private void goBackToMyDay() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.example.todolist", Context.MODE_PRIVATE);
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("type", "day");
        sharedPreferences.edit().putString("last", "day").apply();
        startActivity(intent);
    }

    private void deleteList() {
        crudController.deleteList(listKey);
    }

    private void prepareDialogs() {
        setDueDateButton = getView().findViewById(R.id.setDueDateButton);
        setDueDateButton.setText("Set Date");
        setReminderButton = getView().findViewById(R.id.setRemindMeButton);
        setRepeatFrequencyButton = getView().findViewById(R.id.setRepeatFrequencyButton);
        dialogHelper = new DialogHelper(getContext(), setDueDateButton, setReminderButton, setRepeatFrequencyButton, getActivity().getLayoutInflater());
    }

    private void prepareDialogResults() {
        repeatFrequencyPresentationDatabaseText = dialogHelper.getRepeatFrequency();
        dueDateDatabaseText = dialogHelper.getDueDateDatabaseText();
        dueDatePresentationText = dialogHelper.getDueDatePresentationText();
        reminderDateTimeDatabaseText = dialogHelper.getReminderDateTimeDatabaseText();
        reminderDayTimePresentationText = dialogHelper.getReminderDayTimePresentationText();
        prepareDialogs();
    }
}
