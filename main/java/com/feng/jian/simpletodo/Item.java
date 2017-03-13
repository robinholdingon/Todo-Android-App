package com.feng.jian.simpletodo;

import java.util.Date;

/**
 * Created by jian_feng on 3/11/17.
 */

public class Item {
    public enum Priority{
        HIGH, MEDIUM, LOW
    }

    private Priority priority;
    private String description;
    private boolean isComplete;
    private Date due;
    private long id;

    public Item(String title) {
        this.title = title;
        this.description = "";
        this.isComplete = false;
        this.priority = Priority.HIGH;
        this.due = new Date();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

}