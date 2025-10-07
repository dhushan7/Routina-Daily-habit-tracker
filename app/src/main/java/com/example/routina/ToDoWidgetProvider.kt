package com.example.routina

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class ToDoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.todo_widget)
            
            //Completion percentage
            val completionPercentage = calculateTaskCompletion(context)
            val completedTasks = getCompletedTasksCount(context)
            val totalTasks = getTotalTasksCount(context)
            
            //Update widget views
            views.setTextViewText(R.id.widget_percentage, "${completionPercentage.toInt()}%")
            views.setTextViewText(R.id.widget_habits_text, "$completedTasks/$totalTasks tasks")
            views.setProgressBar(R.id.widget_progress, 100, completionPercentage.toInt(), false)
            
            //Set click intent to open app
            val intent = Intent(context, HomePage::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
        
        private fun calculateTaskCompletion(context: Context): Float {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val prefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
            val json = prefs.getString("todo_data", null)
            
            if (json.isNullOrEmpty()) return 0f
            
            val gson = Gson()
            val type = object : TypeToken<MutableMap<String, DailyData>>() {}.type
            val todoMap: MutableMap<String, DailyData> = gson.fromJson(json, type)
            val dailyData = todoMap[todayDate] ?: return 0f
            
            if (dailyData.tasks.isEmpty()) return 0f
            
            val completedTasks = dailyData.tasks.count { it.status == "Completed" }
            return (completedTasks.toFloat() / dailyData.tasks.size) * 100f
        }
        
        private fun getCompletedTasksCount(context: Context): Int {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val prefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
            val json = prefs.getString("todo_data", null)
            
            if (json.isNullOrEmpty()) return 0
            
            val gson = Gson()
            val type = object : TypeToken<MutableMap<String, DailyData>>() {}.type
            val todoMap: MutableMap<String, DailyData> = gson.fromJson(json, type)
            val dailyData = todoMap[todayDate] ?: return 0
            
            return dailyData.tasks.count { it.status == "Completed" }
        }
        
        private fun getTotalTasksCount(context: Context): Int {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val prefs = context.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
            val json = prefs.getString("todo_data", null)
            
            if (json.isNullOrEmpty()) return 0
            
            val gson = Gson()
            val type = object : TypeToken<MutableMap<String, DailyData>>() {}.type
            val todoMap: MutableMap<String, DailyData> = gson.fromJson(json, type)
            val dailyData = todoMap[todayDate] ?: return 0
            
            return dailyData.tasks.size
        }
    }
}