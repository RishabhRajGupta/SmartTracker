package com.example.progresstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProgressTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Daily Progress Table
    private static final String TABLE_DAILY = "daily_progress";
    private static final String COL_DAILY_ID = "id";
    private static final String COL_DAILY_DATE = "date";
    private static final String COL_DAILY_CONTENT = "content";
    private static final String COL_DAILY_TOPICS = "topics";
    private static final String COL_DAILY_COMPLETED = "completed";

    // Goals Table
    private static final String TABLE_GOALS = "goals";
    private static final String COL_GOAL_ID = "id";
    private static final String COL_GOAL_TITLE = "title";
    private static final String COL_GOAL_DESCRIPTION = "description";
    private static final String COL_GOAL_CREATED = "created_date";

    // Goal Progress Table (for daily checklist)
    private static final String TABLE_GOAL_PROGRESS = "goal_progress";
    private static final String COL_GP_ID = "id";
    private static final String COL_GP_GOAL_ID = "goal_id";
    private static final String COL_GP_DATE = "date";
    private static final String COL_GP_COMPLETED = "completed";

    // Notes Table
    private static final String TABLE_NOTES = "notes";
    private static final String COL_NOTE_ID = "id";
    private static final String COL_NOTE_DATE = "date";
    private static final String COL_NOTE_TITLE = "title";
    private static final String COL_NOTE_DRIVE_LINK = "drive_link";

    // Revision Schedule Table
    private static final String TABLE_REVISION = "revision_schedule";
    private static final String COL_REV_ID = "id";
    private static final String COL_REV_TOPIC = "topic";
    private static final String COL_REV_LEARNED_DATE = "learned_date";
    private static final String COL_REV_NEXT_REVISION = "next_revision_date";
    private static final String COL_REV_INTERVAL = "interval_days";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Createw Daily Progress table
        String createDailyTable = "CREATE TABLE " + TABLE_DAILY + " (" +
                COL_DAILY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DAILY_DATE + " TEXT UNIQUE, " +
                COL_DAILY_CONTENT + " TEXT, " +
                COL_DAILY_TOPICS + " TEXT, " +
                COL_DAILY_COMPLETED + " INTEGER DEFAULT 0)";
        db.execSQL(createDailyTable);

        // Create Goals table
        String createGoalsTable = "CREATE TABLE " + TABLE_GOALS + " (" +
                COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_GOAL_TITLE + " TEXT NOT NULL, " +
                COL_GOAL_DESCRIPTION + " TEXT, " +
                COL_GOAL_CREATED + " TEXT)";
        db.execSQL(createGoalsTable);

        // Create Goal Progress table
        String createGoalProgressTable = "CREATE TABLE " + TABLE_GOAL_PROGRESS + " (" +
                COL_GP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_GP_GOAL_ID + " INTEGER, " +
                COL_GP_DATE + " TEXT, " +
                COL_GP_COMPLETED + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_GP_GOAL_ID + ") REFERENCES " + TABLE_GOALS + "(" + COL_GOAL_ID + "), " +
                "UNIQUE(" + COL_GP_GOAL_ID + ", " + COL_GP_DATE + "))";
        db.execSQL(createGoalProgressTable);

        // Create Notes table
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTE_DATE + " TEXT, " +
                COL_NOTE_TITLE + " TEXT, " +
                COL_NOTE_DRIVE_LINK + " TEXT)";
        db.execSQL(createNotesTable);

        // Create Revision Schedule table
        String createRevisionTable = "CREATE TABLE " + TABLE_REVISION + " (" +
                COL_REV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REV_TOPIC + " TEXT, " +
                COL_REV_LEARNED_DATE + " TEXT, " +
                COL_REV_NEXT_REVISION + " TEXT, " +
                COL_REV_INTERVAL + " INTEGER)";
        db.execSQL(createRevisionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOAL_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVISION);
        onCreate(db);
    }

    // Daily Progress Methods
    public boolean addDailyProgress(String date, String content, String topics) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DAILY_DATE, date);
        values.put(COL_DAILY_CONTENT, content);
        values.put(COL_DAILY_TOPICS, topics);
        values.put(COL_DAILY_COMPLETED, 1);

        long result = db.insertWithOnConflict(TABLE_DAILY, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // Add topics to revision schedule
        if (!topics.isEmpty()) {
            String[] topicArray = topics.split(",");
            for (String topic : topicArray) {
                addToRevisionSchedule(topic.trim(), date);
            }
        }

        return result != -1;
    }

    public Cursor getDailyProgress(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DAILY, null, COL_DAILY_DATE + "=?",
                new String[]{date}, null, null, null);
    }

    public Cursor getAllCompletedDates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_DAILY, new String[]{COL_DAILY_DATE},
                COL_DAILY_COMPLETED + "=1", null, null, null, COL_DAILY_DATE + " DESC");
    }

    // Goals Methods
    public long addGoal(String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GOAL_TITLE, title);
        values.put(COL_GOAL_DESCRIPTION, description);
        values.put(COL_GOAL_CREATED, getCurrentDate());

        return db.insert(TABLE_GOALS, null, values);
    }

    public Cursor getAllGoals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_GOALS, null, null, null, null, null, COL_GOAL_ID + " DESC");
    }

    public boolean updateGoalProgress(int goalId, String date, boolean completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_GP_GOAL_ID, goalId);
        values.put(COL_GP_DATE, date);
        values.put(COL_GP_COMPLETED, completed ? 1 : 0);

        long result = db.insertWithOnConflict(TABLE_GOAL_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean isGoalCompletedToday(int goalId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GOAL_PROGRESS, new String[]{COL_GP_COMPLETED},
                COL_GP_GOAL_ID + "=? AND " + COL_GP_DATE + "=?",
                new String[]{String.valueOf(goalId), date}, null, null, null);

        boolean completed = false;
        if (cursor.moveToFirst()) {
            completed = cursor.getInt(0) == 1;
        }
        cursor.close();
        return completed;
    }

    public boolean deleteGoal(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete goal progress entries
        db.delete(TABLE_GOAL_PROGRESS, COL_GP_GOAL_ID + "=?", new String[]{String.valueOf(goalId)});
        // Delete goal
        return db.delete(TABLE_GOALS, COL_GOAL_ID + "=?", new String[]{String.valueOf(goalId)}) > 0;
    }

    // Notes Methods
    public long addNote(String date, String title, String driveLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOTE_DATE, date);
        values.put(COL_NOTE_TITLE, title);
        values.put(COL_NOTE_DRIVE_LINK, driveLink);

        return db.insert(TABLE_NOTES, null, values);
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTES, null, null, null, null, null, COL_NOTE_DATE + " DESC");
    }

    public Cursor getNotesByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NOTES, null, COL_NOTE_DATE + "=?",
                new String[]{date}, null, null, null);
    }

    // Revision Schedule Methods
    public void addToRevisionSchedule(String topic, String learnedDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REV_TOPIC, topic);
        values.put(COL_REV_LEARNED_DATE, learnedDate);
        values.put(COL_REV_NEXT_REVISION, addDaysToDate(learnedDate, 1));
        values.put(COL_REV_INTERVAL, 1);

        db.insert(TABLE_REVISION, null, values);
    }

    public List<String> getTopicsDueForRevision(String currentDate) {
        List<String> topics = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REVISION, new String[]{COL_REV_TOPIC, COL_REV_INTERVAL},
                COL_REV_NEXT_REVISION + "<=?", new String[]{currentDate}, null, null, null);

        while (cursor.moveToNext()) {
            topics.add(cursor.getString(0) + " (Interval: " + cursor.getInt(1) + " days)");
        }
        cursor.close();
        return topics;
    }

    public void updateRevisionSchedule(String topic, int currentInterval) {
        SQLiteDatabase db = this.getWritableDatabase();
        int nextInterval = getNextInterval(currentInterval);
        String nextDate = addDaysToDate(getCurrentDate(), nextInterval);

        ContentValues values = new ContentValues();
        values.put(COL_REV_NEXT_REVISION, nextDate);
        values.put(COL_REV_INTERVAL, nextInterval);

        db.update(TABLE_REVISION, values, COL_REV_TOPIC + "=?", new String[]{topic});
    }

    private int getNextInterval(int current) {
        if (current == 1) return 3;
        if (current == 3) return 7;
        if (current == 7) return 15;
        if (current == 15) return 30;
        return 30;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String addDaysToDate(String date, int days) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d = sdf.parse(date);
            long newTime = d.getTime() + (days * 24 * 60 * 60 * 1000L);
            return sdf.format(new Date(newTime));
        } catch (Exception e) {
            return date;
        }
    }
}
