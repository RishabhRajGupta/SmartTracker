package com.example.progresstracker;

public class Goal {
    private int id;
    private String title;
    private String description;
    private boolean completedToday;

    public Goal(int id, String title, String description, boolean completedToday) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completedToday = completedToday;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompletedToday() { return completedToday; }
    public void setCompletedToday(boolean completed) { this.completedToday = completed; }
}