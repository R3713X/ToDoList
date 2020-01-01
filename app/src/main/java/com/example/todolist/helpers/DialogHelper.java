package com.example.todolist.helpers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.todolist.R;

import java.util.Calendar;

import static com.example.todolist.helpers.DateHelper.normalizeNumber;

public class DialogHelper {
    /**
     * Due date selection
     */
    Calendar dueDateSelected = Calendar.getInstance();

    String dueDateDatabaseText = "";
    String dueDatePresentationText = "";

    /**
     * Reminder date selection
     */
    Calendar reminderDateSelected = Calendar.getInstance();

    private DatePickerDialog reminderDatePickerDialog;
    private TimePickerDialog reminderTimePickerDialog;
    String reminderDayTimePresentationText = "";
    String reminderDay;
    String reminderDateTimeDatabaseText = "";

    /**
     * Repeat frequency selection
     */
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String repeatFrequency = "";

    private Context context;
    private ToggleButton setDueDateButton, setReminderButton, setRepeatFrequencyButton;
    private LayoutInflater layoutInflater;

    public DialogHelper(Context context, ToggleButton setDueDateButton, ToggleButton setReminderButton, ToggleButton setRepeatFrequencyButton, LayoutInflater layoutInflater) {
        this.context = context;
        this.setDueDateButton = setDueDateButton;
        this.setRepeatFrequencyButton = setRepeatFrequencyButton;
        this.setReminderButton = setReminderButton;
        this.layoutInflater = layoutInflater;

        attachOnClickListeners();
    }

    private void attachOnClickListeners() {
        setDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDueDateDialog().show();
                dueWasTouched = true;
            }
        });

        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReminderDialog().show();
                reminderWasTouched = true;
            }
        });

        setRepeatFrequencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRepeatInterface();
                repeatWasTouched = true;
            }
        });
    }

    private Dialog getDueDateDialog() {
        Calendar newCalendar = dueDateSelected;
        DatePickerDialog dueDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int realMonth = monthOfYear + 1;
                dueDateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                dueDateDatabaseText = year + "-" + normalizeNumber(realMonth) + "-" + normalizeNumber(dayOfMonth);
                dueDatePresentationText = dayOfMonth + "/" + realMonth + "/" + year;
                setDueDateButton.setChecked(true);
                setDueDateButton.setText("Due: " + dueDatePresentationText);
            }


        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dueDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.cancel();

                            dueDateDatabaseText = "";
                            dueDatePresentationText = "";
                            setDueDateButton.setChecked(false);
                            setDueDateButton.setText("Set Due Date");
                        }
                    }
                });
        return dueDatePickerDialog;
    }


    private Dialog getReminderDialog() {
        Calendar newCalendar = reminderDateSelected;
        reminderDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int realMonth = monthOfYear + 1;
                reminderDateSelected.set(year, realMonth, dayOfMonth, 0, 0);
                reminderDateTimeDatabaseText = year + "-" + normalizeNumber(realMonth) + "-" + normalizeNumber(dayOfMonth);
                reminderDay = dayOfMonth + "/" + realMonth + "/" + year;
                setTimeField();

            }


        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        reminderDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            reminderDay = "";
                            reminderDayTimePresentationText = "";
                            reminderDateTimeDatabaseText = "";

                            setReminderButton.setChecked(false);
                            setReminderButton.setText("Set Reminder");
                            dialog.cancel();
                        }
                    }
                });
        return reminderDatePickerDialog;
    }

    // reminder date-time
    private void setTimeField() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        reminderTimePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                reminderDayTimePresentationText = (reminderDay + " " + normalizeNumber(hourOfDay) + ":" + normalizeNumber(minute));
                reminderDateTimeDatabaseText = reminderDateTimeDatabaseText
                        + " " + normalizeNumber(hourOfDay) + ":" + normalizeNumber(minute) + ":" + "00";
                setReminderButton.setChecked(true);
                setReminderButton.setText("Reminder: " + reminderDayTimePresentationText);


            }
        }, hour, minute, true);
        reminderTimePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {

                            reminderDateTimeDatabaseText = "";
                            reminderDayTimePresentationText = "";
                            setReminderButton.setChecked(false);
                            setReminderButton.setText("Set Reminder");
                            dialog.cancel();

                        }
                    }
                });
        reminderTimePickerDialog.show();
    }


    private void createRepeatInterface() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = layoutInflater.inflate(R.layout.select_repeat_frequency_layout, null);
        radioGroup = view.findViewById(R.id.radioGroup);
        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repeatFrequency = "";
                        setRepeatFrequencyButton.setChecked(false);
                        setRepeatFrequencyButton.setText("Set Repeater");

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
                        setRepeatFrequencyButton.setChecked(true);
                        setRepeatFrequencyButton.setText("Repeat " + radioButton.getText().toString());
                        repeatFrequency = radioButton.getText().toString();
                    }
                });
        builder.create().show();
    }


    public String getReminderDayTimePresentationText() {
        return reminderDayTimePresentationText;
    }


    public String getReminderDateTimeDatabaseText() {
        return reminderDateTimeDatabaseText;
    }

    public String getDueDateDatabaseText() {
        return dueDateDatabaseText;
    }


    public String getDueDatePresentationText() {
        return dueDatePresentationText;
    }


    public String getRepeatFrequency() {
        return repeatFrequency;
    }

    private boolean reminderWasTouched = false;
    private boolean repeatWasTouched = false;
    private boolean dueWasTouched = false;

    public boolean reminderWasTouched() {
        return reminderWasTouched;
    }

    public boolean repeatWasTouched() {
        return repeatWasTouched;
    }

    public boolean dueWasTouched() {
        return dueWasTouched;
    }
}
