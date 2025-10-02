package com.example.routina

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("health_prefs", Context.MODE_PRIVATE)
        val hydrationLimit = prefs.getInt("hydration_limit", 2000)
        val caloriesLimit = prefs.getInt("calories_limit", 2500)

        prefs.edit()
            .putInt("hydration_added", 0)
            .putInt("calories_added", 0)
            .apply()
    }
}
