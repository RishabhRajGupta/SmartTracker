// MainActivity.java
package com.example.progresstracker;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.os.Build;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make status bar transparent and text dark
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.parseColor("#4F46E5"));
        }

        dbHelper = new DatabaseHelper(this);

        // Create notification channels
        createNotificationChannels();

        // Setup bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new DailyProgressFragment())
                    .commit();
        }

        // Schedule daily reminders and quotes
        scheduleDailyReminders();
        scheduleMotivationalQuotes();
        scheduleRevisionCheck();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.nav_daily) {
                        selectedFragment = new DailyProgressFragment();
                    } else if (itemId == R.id.nav_goals) {
                        selectedFragment = new GoalsFragment();
                    } else if (itemId == R.id.nav_calendar) {
                        selectedFragment = new CalendarFragment();
                    } else if (itemId == R.id.nav_notes) {
                        selectedFragment = new NotesFragment();
                    } else if (itemId == R.id.nav_revision) {
                        selectedFragment = new RevisionFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, selectedFragment)
                                .commit();
                    }

                    return true;
                }
            };

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel reminderChannel = new NotificationChannel(
                    "reminder_channel",
                    "Daily Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            reminderChannel.setDescription("Daily progress reminders");

            NotificationChannel quoteChannel = new NotificationChannel(
                    "quote_channel",
                    "Motivational Quotes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            quoteChannel.setDescription("Motivational quotes throughout the day");

            NotificationChannel revisionChannel = new NotificationChannel(
                    "revision_channel",
                    "Revision Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            revisionChannel.setDescription("Spaced repetition revision reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(reminderChannel);
            manager.createNotificationChannel(quoteChannel);
            manager.createNotificationChannel(revisionChannel);
        }
    }

    private void scheduleDailyReminders() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setAction("DAILY_REMINDER");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void scheduleMotivationalQuotes() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setAction("MOTIVATIONAL_QUOTE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Send quotes every 4 hours
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 4 * 60 * 60 * 1000,
                4 * 60 * 60 * 1000,
                pendingIntent
        );
    }

    private void scheduleRevisionCheck() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setAction("REVISION_CHECK");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }
}