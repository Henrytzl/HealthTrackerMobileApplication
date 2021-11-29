package com.example.healthtracker.reminder;

public class RecycleViewReminder {

    String reminderTitle, reminderTime, reminderID, reminderDesc;
    Boolean reminderActivate;
    int requestCodeID;

    public String getReminderTitle() {
        return reminderTitle;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public String getReminderID() {
        return reminderID;
    }

    public String getReminderDesc() {
        return reminderDesc;
    }

    public Boolean getReminderActivate() {
        return reminderActivate;
    }

    public int getRequestCodeID(){
        return requestCodeID;
    }
}
