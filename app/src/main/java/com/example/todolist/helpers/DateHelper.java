package com.example.todolist.helpers;

import java.util.Calendar;

public class DateHelper {


    public static Calendar getCalendarFromDatabaseDateTime(String s) {
        int[] ints = seperateValuesFromDayTimeString(s);
        Calendar calendar = Calendar.getInstance();
        calendar.set(ints[0], ints[1]-1, ints[2], ints[3], ints[4], 0);
        return calendar;
    }

    public static Calendar getCalendarFromDatabaseDate(String s) {
        int[] ints = seperateValuesFromDate(s);
        Calendar calendar = Calendar.getInstance();
        calendar.set(ints[0], ints[1]-1, ints[2]+1, 0, 0, 0);
        return calendar;
    }

    public static String getPresentableDateTimeStringFromDatabaseDateTime(String s) {
        int[] ints = seperateValuesFromDayTimeString(s);

        return ints[3] + ":" + normalizeNumber(ints[4]) + " " + normalizeNumber(ints[2]) + "/" + normalizeNumber(ints[1]) + "/" + ints[0];
    }

    public static String normalizeNumber(int input) {

        String number = "" + input;
        if (number.length() == 1) {
            number = "0" + number;
        }
        return number;
    }


    public static String getPresentableDateStringFromDatabaseDate(String s) {
        int[] ints = seperateValuesFromDate(s);
        return normalizeNumber(ints[2]) + "/" + normalizeNumber(ints[1]) + "/" + ints[0];
    }

    private static int[] seperateValuesFromDayTimeString(String dateTimeString) {
        String[] dateNtimeParts = dateTimeString.split(" ");
        String[] yearMonthDayParts = dateNtimeParts[0].split("-");
        String[] hourMinSecParts = dateNtimeParts[1].split(":");
        int year = Integer.valueOf(yearMonthDayParts[0]);
        int month = Integer.valueOf(yearMonthDayParts[1]);
        int day = Integer.valueOf(yearMonthDayParts[2]);
        int hour = Integer.valueOf(hourMinSecParts[0]);
        int min = Integer.valueOf(hourMinSecParts[1]);
        return new int[]{year, month, day, hour, min};
    }

    private static int[] seperateValuesFromDate(String dateString) {
        String[] yearMonthDayParts = dateString.split("-");
        int year = Integer.valueOf(yearMonthDayParts[0]);
        int month = Integer.valueOf(yearMonthDayParts[1]);
        int day = Integer.valueOf(yearMonthDayParts[2]);
        return new int[]{year, month, day};
    }
}
