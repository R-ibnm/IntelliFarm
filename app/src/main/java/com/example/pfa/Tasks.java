package com.example.pfa;
public class Tasks {
    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private boolean isDone;

    public Tasks() {
        // Required by Firebase
    }

    public Tasks(String taskId, String taskTitle) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.isDone = false; // Initialize status as not done
    }

    public Tasks(String taskId, String taskTitle, boolean isDone) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.isDone = isDone; // Initialize status as not done
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return taskDescription;
    }


}