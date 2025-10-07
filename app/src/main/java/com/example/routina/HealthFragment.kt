package com.example.routina

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class HealthFragment : Fragment() {

    private lateinit var tvHydration: TextView
    private lateinit var btnAddWater: Button
    private lateinit var btnSetHydrationLimit: TextView

    private lateinit var tvCalories: TextView
    private lateinit var btnAddCalories: Button
    private lateinit var btnSetCalorieLimit: TextView

    private val PREFS_NAME = "health_prefs"
    private val HYDRATION_ADDED = "hydration_added"
    private val HYDRATION_LIMIT = "hydration_limit"
    private val CALORIES_ADDED = "calories_added"
    private val CALORIES_LIMIT = "calories_limit"
    private val LAST_DATE = "last_date"

    private var hydrationAdded = 0
    private var hydrationLimit = 2000
    private var caloriesAdded = 0
    private var caloriesLimit = 2500

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_health, container, false)

        //Hydration
        tvHydration = view.findViewById(R.id.tvHydration)
        btnAddWater = view.findViewById(R.id.btnAddWater)
        btnSetHydrationLimit = view.findViewById(R.id.btnSetHydrationLimit)

        //Calories
        tvCalories = view.findViewById(R.id.tvCalories)
        btnAddCalories = view.findViewById(R.id.btnAddCalories)
        btnSetCalorieLimit = view.findViewById(R.id.btnSetCalorieLimit)

        loadData()
        updateUI()

        //Add 250ml water
        btnAddWater.setOnClickListener {
            hydrationAdded += 250
            saveData()
            updateUI()
        }

        //Set hydration limit
        btnSetHydrationLimit.setOnClickListener {
            showInputDialog("Set Hydration Limit (ml)") { input ->
                val newLimit = input.toIntOrNull()
                if (newLimit != null && newLimit > 0) {
                    hydrationLimit = newLimit
                    hydrationAdded = 0
                    saveData()
                    updateUI()
                }
            }
        }

        //Add 100 kcal
        btnAddCalories.setOnClickListener {
            caloriesAdded += 100
            saveData()
            updateUI()
        }

        //Set calorie limit
        btnSetCalorieLimit.setOnClickListener {
            showInputDialog("Set Calorie Limit (kcal)") { input ->
                val newLimit = input.toIntOrNull()
                if (newLimit != null && newLimit > 0) {
                    caloriesLimit = newLimit
                    caloriesAdded = 0
                    saveData()
                    updateUI()
                }
            }
        }

        //Schedule daily reset & reminder
        scheduleAlarms()

        return view
    }
    //update water cals
    private fun updateUI() {
        tvHydration.text = "Water: $hydrationAdded / $hydrationLimit ml"
        tvCalories.text = "Calories: $caloriesAdded / $caloriesLimit kcal"
    }


    //Input dialog
    private fun showInputDialog(title: String, onResult: (String) -> Unit) {
        val input = android.widget.EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                onResult(input.text.toString().trim())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveData() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(HYDRATION_ADDED, hydrationAdded)
            .putInt(HYDRATION_LIMIT, hydrationLimit)
            .putInt(CALORIES_ADDED, caloriesAdded)
            .putInt(CALORIES_LIMIT, caloriesLimit)
            .putString(LAST_DATE, dateFormat.format(Date()))
            .apply()
    }

    private fun loadData() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        hydrationAdded = prefs.getInt(HYDRATION_ADDED, 0)
        hydrationLimit = prefs.getInt(HYDRATION_LIMIT, 2000)
        caloriesAdded = prefs.getInt(CALORIES_ADDED, 0)
        caloriesLimit = prefs.getInt(CALORIES_LIMIT, 2500)

        //Reset daily data if app opened on new day
        val lastDate = prefs.getString(LAST_DATE, "")
        val today = dateFormat.format(Date())
        if (lastDate != today) {
            hydrationAdded = 0
            caloriesAdded = 0
            saveData()
        }
    }


    private fun scheduleAlarms() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Reset Alarm at 11:59 PM
        val resetIntent = Intent(requireContext(), ResetReceiver::class.java)
        val resetPendingIntent = PendingIntent.getBroadcast(
            requireContext(), 1, resetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        //Reset values 11.59 pm
        val resetTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            resetTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            resetPendingIntent
        )

        //Reminder Alarm at 4:00 PM
        val reminderIntent = Intent(requireContext(), ReminderReceiver::class.java)
        val reminderPendingIntent = PendingIntent.getBroadcast(
            requireContext(), 2, reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val reminderTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            reminderTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            reminderPendingIntent
        )
    }
}
