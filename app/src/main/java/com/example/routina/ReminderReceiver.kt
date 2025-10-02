package com.example.routina

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("health_prefs", Context.MODE_PRIVATE)
        val hydrationAdded = prefs.getInt("hydration_added", 0)
        val hydrationLimit = prefs.getInt("hydration_limit", 2000)
        val caloriesAdded = prefs.getInt("calories_added", 0)
        val caloriesLimit = prefs.getInt("calories_limit", 2500)

        val unmetGoals = mutableListOf<String>()
        if (hydrationAdded < hydrationLimit) unmetGoals.add("Hydration")
        if (caloriesAdded < caloriesLimit) unmetGoals.add("Calories")

        if (unmetGoals.isNotEmpty()) {
            val message = "Don't forget to complete: ${unmetGoals.joinToString(", ")}"

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "health_reminder_channel"

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Health Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Health Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1001, notification)
        }
    }
}
