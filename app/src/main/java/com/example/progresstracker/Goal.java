package com.example.progresstracker;

import com.google.gson.annotations.SerializedName;

public class Goal {

    @SerializedName("id")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("createdDate")
    private String createdDate; // Backend se Instant ko String me le lenge

    @SerializedName("completed")
    private boolean completed; // To-do functionality ke liye field

    // Constructor for creating a new goal to send to backend
    public Goal(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false; // Default
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCreatedDate() { return createdDate; }
    public boolean isCompleted() { return completed; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}