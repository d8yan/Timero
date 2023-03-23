package com.conestoga.timero;

public class Todo {
    private int id;
    private String task;
    private boolean isCompleted;

    public Todo() {
        id = 0;
        task = "";
        isCompleted = false;
    }

    public Todo(int id, String task, int isCompleted) {
        this.id = id;
        this.task = task;
        this.isCompleted = isCompleted != 0;
    }

    public int getId() {
        return this.id;
    }

    public String getTask() {
        return this.task;
    }

    public boolean getIsCompleted() {
        return this.isCompleted;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setIsCompleted(int value) {
       isCompleted = value != 0;
    }
}
