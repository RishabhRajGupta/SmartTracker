package com.example.progresstracker;

public class Note {
    private int id;
    private String date;
    private String title;
    private String driveLink;

    public Note(int id, String date, String title, String driveLink) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.driveLink = driveLink;
    }

    public int getId() { return id; }
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getDriveLink() { return driveLink; }
}
