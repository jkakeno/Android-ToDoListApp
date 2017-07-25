package com.example.jkakeno.todolist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Entry {

    String task;
    Long date;
    public int id;

    public Entry(int id, String task, Long date) {
        this.id = id;
        this.task = task;
        this.date = date;
    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Long getDate() {
        return date;
    }

//Helper method to return a formated date
    public String getDateString() {
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTimeInMillis();
        String dateFormatted = new SimpleDateFormat("yyyy/MM/dd").format(new Date(date));
        return dateFormatted;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
