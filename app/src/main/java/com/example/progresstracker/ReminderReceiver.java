package com.example.progresstracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String[] MOTIVATIONAL_QUOTES = {
            "The expert in anything was once a beginner.",
            "Success is the sum of small efforts repeated day in and day out.",
            "Don't watch the clock; do what it does. Keep going.",
            "The secret of getting ahead is getting started.",
            "Learning never exhausts the mind.",
            "Progress, not perfection.",
            "Every accomplishment starts with the decision to try.",
            "Believe you can and you're halfway there.",
            "The only way to do great work is to love what you do.",
            "Keep pushing forward!"
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if ("DAILY_REMINDER".equals(action)) {
            sendDailyReminder(context);
        } else if ("MOTIVATIONAL_QUOTE".equals(action)) {
            sendMotivationalQuote(context);
        } else if ("REVISION_CHECK".equals(action)) {
            checkRevisionReminders(context);
        }
    }

    private void sendDailyReminder(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Daily Progress Reminder")
                .setContentText("Don't forget to log your progress today! 📚")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void sendMotivationalQuote(Context context) {
        Random random = new Random();
        String quote = MOTIVATIONAL_QUOTES[random.nextInt(MOTIVATIONAL_QUOTES.length)];

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "quote_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Motivation Boost")
                .setContentText(quote)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, builder.build());
    }

    private void checkRevisionReminders(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        List<String> topicsDue = dbHelper.getTopicsDueForRevision(currentDate);

        if (!topicsDue.isEmpty()) {
            StringBuilder topics = new StringBuilder();
            for (String topic : topicsDue) {
                topics.append("• ").append(topic).append("\n");
            }

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("open_revision", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "revision_channel")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Time to Revise! 🔄")
                    .setContentText(topicsDue.size() + " topic(s) due for revision")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(topics.toString()))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(3, builder.build());
        }
    }
}
