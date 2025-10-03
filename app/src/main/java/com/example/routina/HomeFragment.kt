package com.example.routina

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvMood: TextView
    private lateinit var tvHydration: TextView
    private lateinit var tvCalories: TextView
    private lateinit var containerMood: View
    private lateinit var containerHydration: View
    private lateinit var containerCalories: View
    private lateinit var containerTodo: View

    private val healthPrefs = "health_prefs"
    private val todoPrefs = "todo_prefs"
    private val gson = com.google.gson.Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDate = dateFormat.format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // find views
        tvGreeting = view.findViewById(R.id.hmtext1)
        tvMood = view.findViewById(R.id.hmfeel)
        tvHydration = view.findViewById(R.id.hmHydration)
        tvCalories = view.findViewById(R.id.hmCal)

        containerMood = view.findViewById(R.id.moodCardContainer)
        containerHydration = view.findViewById(R.id.hydrationCardContainer)
        containerCalories = view.findViewById(R.id.calorieCardContainer)
        containerTodo = view.findViewById(R.id.todoCardContainer)

        // load greeting
        val sharedPref = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "User")
        tvGreeting.text = "Hello, $name"

        // load health data
        loadHealthData()

        // load mood
        loadMood()

        // tap listeners
        containerMood.setOnClickListener { showMoodDialog() }
        containerHydration.setOnClickListener { openFragment(HealthFragment()) }
        containerCalories.setOnClickListener { openFragment(HealthFragment()) }
        containerTodo.setOnClickListener { openFragment(ToDoFragment()) }
    }

    private fun loadHealthData() {
        val prefs = requireContext().getSharedPreferences(healthPrefs, Context.MODE_PRIVATE)
        val hydrationAdded = prefs.getInt("hydration_added", 0)
        val hydrationLimit = prefs.getInt("hydration_limit", 2000)
        val caloriesAdded = prefs.getInt("calories_added", 0)
        val caloriesLimit = prefs.getInt("calories_limit", 2500)

        tvHydration.text = "Hydration: $hydrationAdded / $hydrationLimit ml"
        tvCalories.text = "Calorie: $caloriesAdded / $caloriesLimit kcal"
    }

    private fun loadMood() {
        val prefs = requireContext().getSharedPreferences(todoPrefs, Context.MODE_PRIVATE)
        val json = prefs.getString("todo_data", null)
        if (!json.isNullOrEmpty()) {
            val type = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<MutableMap<String, DailyData>>() {}.type
            val todoMap: MutableMap<String, DailyData> = gson.fromJson(json, type)
            val dailyData = todoMap.getOrPut(todayDate) { DailyData() }
            tvMood.text = dailyData.feeling
        } else {
            tvMood.text = "😐"
        }
    }

    private fun showMoodDialog() {
        val feelings = arrayOf("😢", "😐", "😊", "😡", "❤️")
        val prefs = requireContext().getSharedPreferences(todoPrefs, Context.MODE_PRIVATE)
        val json = prefs.getString("todo_data", null)

        val type = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<MutableMap<String, DailyData>>() {}.type
        var todoMap: MutableMap<String, DailyData> = if (!json.isNullOrEmpty()) {
            gson.fromJson(json, type)
        } else mutableMapOf()

        val dailyData = todoMap.getOrPut(todayDate) { DailyData() }
        val currentIndex = feelings.indexOf(dailyData.feeling)

        AlertDialog.Builder(requireContext())
            .setTitle("Today's Feeling")
            .setSingleChoiceItems(feelings, currentIndex) { dialog, which ->
                dailyData.feeling = feelings[which]
                tvMood.text = dailyData.feeling
                val editor = prefs.edit()
                editor.putString("todo_data", gson.toJson(todoMap))
                editor.apply()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.frameLayout, fragment)
            addToBackStack(null)
        }
    }
}
