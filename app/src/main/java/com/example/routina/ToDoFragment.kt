package com.example.routina

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

data class Task(
    var name: String,
    var status: String = "Pending"
)

data class DailyData(
    var tasks: MutableList<Task> = mutableListOf(),
    var feeling: String = "😐" // default neutral
)

class ToDoFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var todoContainer: LinearLayout
    private lateinit var btnAddTask: Button
    private lateinit var tvDayFeeling: TextView  // Day-level emoji

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDate = dateFormat.format(Date())

    private val gson = Gson()
    private val PREFS_NAME = "todo_prefs"
    private val DATA_KEY = "todo_data"

    private var todoMap = mutableMapOf<String, DailyData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_to_do, container, false)

        calendarView = view.findViewById(R.id.calendarView2)
        todoContainer = view.findViewById(R.id.todoContainer)
        btnAddTask = view.findViewById(R.id.btnAddTask)

        // Inflate a top TextView for day-level feeling emoji
        tvDayFeeling = TextView(requireContext()).apply {
            textSize = 24f
            setPadding(16, 16, 16, 16)
            text = "😐"
        }
        todoContainer.addView(tvDayFeeling, 0) // add at top

        calendarView.maxDate = System.currentTimeMillis()
        loadTasks()

        // Load today tasks
        loadTasksForDate(todayDate)

        // Calendar selection
        calendarView.setOnDateChangeListener { _, year, month, day ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            loadTasksForDate(selectedDate)
        }

        btnAddTask.setOnClickListener { showAddTaskDialog(todayDate) }

        return view
    }

    private fun loadTasksForDate(date: String) {
        todoContainer.removeViews(1, todoContainer.childCount - 1) // keep top emoji
        val isToday = date == todayDate
        btnAddTask.visibility = if (isToday) View.VISIBLE else View.GONE

        val dailyData = todoMap.getOrPut(date) { DailyData() }
        tvDayFeeling.text = dailyData.feeling

        if (isToday) {
            tvDayFeeling.setOnClickListener { showFeelingDialog(date) }
        } else {
            tvDayFeeling.setOnClickListener(null) // read-only
        }

        for (task in dailyData.tasks) {
            val taskView = layoutInflater.inflate(R.layout.item_todo, todoContainer, false)
            val tvTask = taskView.findViewById<TextView>(R.id.tvTask)
            val tvStatus = taskView.findViewById<TextView>(R.id.tvStatus)
            val btnEdit = taskView.findViewById<ImageView>(R.id.btnEdit)
            val btnDelete = taskView.findViewById<ImageView>(R.id.btnDelete)

            tvTask.text = task.name
            tvStatus.text = task.status

            if (isToday) {
                btnEdit.visibility = View.VISIBLE
                btnDelete.visibility = View.VISIBLE

                btnEdit.setOnClickListener { showEditTaskDialog(date, task) }
                btnDelete.setOnClickListener {
                    dailyData.tasks.remove(task)
                    saveTasks()
                    loadTasksForDate(date)
                }
                tvStatus.setOnClickListener { showStatusDialog(date, task) }

            } else {
                btnEdit.visibility = View.GONE
                btnDelete.visibility = View.GONE
            }

            todoContainer.addView(taskView)
        }
    }

    private fun showAddTaskDialog(date: String) {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val taskName = input.text.toString().trim()
                if (taskName.isNotEmpty()) {
                    val dailyData = todoMap.getOrPut(date) { DailyData() }
                    dailyData.tasks.add(Task(taskName))
                    saveTasks()
                    loadTasksForDate(date)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditTaskDialog(date: String, task: Task) {
        val input = EditText(requireContext())
        input.setText(task.name)
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Task")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val newTaskName = input.text.toString().trim()
                if (newTaskName.isNotEmpty()) {
                    task.name = newTaskName
                    saveTasks()
                    loadTasksForDate(date)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showStatusDialog(date: String, task: Task) {
        val statuses = arrayOf("Pending", "In-Progress", "Completed")
        val currentIndex = statuses.indexOf(task.status)
        AlertDialog.Builder(requireContext())
            .setTitle("Change Status")
            .setSingleChoiceItems(statuses, currentIndex) { dialog, which ->
                task.status = statuses[which]
                saveTasks()
                loadTasksForDate(date)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showFeelingDialog(date: String) {
        val feelings = arrayOf("😢", "😐", "😊", "😡", "❤️")
        val dailyData = todoMap[date]!!
        val currentIndex = feelings.indexOf(dailyData.feeling)

        AlertDialog.Builder(requireContext())
            .setTitle("Today's Feeling")
            .setSingleChoiceItems(feelings, currentIndex) { dialog, which ->
                dailyData.feeling = feelings[which]
                saveTasks()
                loadTasksForDate(date)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ---------- SharedPreferences ----------
    private fun saveTasks() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(todoMap)
        prefs.edit().putString(DATA_KEY, json).apply()
    }

    private fun loadTasks() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(DATA_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableMap<String, DailyData>>() {}.type
            todoMap = gson.fromJson(json, type)
        }
    }
}
