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
    var status: String = "Pending",
    var category: String = "General",
    var priority: String = "Medium",
    var time: String = ""
)

data class DailyData(
    var tasks: MutableList<Task> = mutableListOf(),
    var feeling: String = "😐" //default neutral
)

class ToDoFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var todoContainer: LinearLayout
    private lateinit var btnAddTask: Button
    private lateinit var tvDayFeeling: TextView
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvCompletionPercentage: TextView

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
        tvDayFeeling = view.findViewById(R.id.tvDayFeeling)
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        tvCompletionPercentage = view.findViewById(R.id.tvCompletionPercentage)

        calendarView.maxDate = System.currentTimeMillis()
        loadTasks()

        //Load today’s tasks by default
        loadTasksForDate(todayDate)

        //Handle date selection
        calendarView.setOnDateChangeListener { _, year, month, day ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            loadTasksForDate(selectedDate)
        }

        btnAddTask.setOnClickListener { showAddTaskDialog(todayDate) }

        return view
    }

    private fun loadTasksForDate(date: String) {
        //Clear task container
        todoContainer.removeAllViews()

        val isToday = date == todayDate
        btnAddTask.visibility = if (isToday) View.VISIBLE else View.GONE

        val dailyData = todoMap.getOrPut(date) { DailyData() }

        //update date , emoji , completion percentage
        tvSelectedDate.text = date
        tvDayFeeling.text = dailyData.feeling
        
        //Completion percentage
        val completionPercentage = if (dailyData.tasks.isEmpty()) {
            0
        } else {
            val completedTasks = dailyData.tasks.count { it.status == "Completed" }
            (completedTasks * 100) / dailyData.tasks.size
        }
        tvCompletionPercentage.text = "${completionPercentage}%"

        if (isToday) {
            tvDayFeeling.setOnClickListener { showFeelingDialog(date) }
        } else {
            tvDayFeeling.setOnClickListener(null) // read-only
        }

        //Load all tasks for this day
        for (task in dailyData.tasks) {
            val taskView = layoutInflater.inflate(R.layout.item_todo, todoContainer, false)
            val tvTask = taskView.findViewById<TextView>(R.id.tvTask)
            val tvStatus = taskView.findViewById<TextView>(R.id.tvStatus)
            val tvCategory = taskView.findViewById<TextView>(R.id.tvCategory)
            val tvPriority = taskView.findViewById<TextView>(R.id.tvPriority)
            val tvTime = taskView.findViewById<TextView>(R.id.tvTime)
            val btnEdit = taskView.findViewById<ImageView>(R.id.btnEdit)
            val btnDelete = taskView.findViewById<ImageView>(R.id.btnDelete)

            tvTask.text = task.name
            tvStatus.text = task.status
            tvCategory.text = task.category
            tvPriority.text = task.priority
            tvTime.text = if (task.time.isNotEmpty()) task.time else "No time set"

            //Set status colors on status
            when (task.status) {
                "Completed" -> tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
                "In-Progress" -> tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"))
                else -> tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E"))
            }
            //is today, user can edit data
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
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_todo, null)
        val inputName = dialogView.findViewById<EditText>(R.id.etHabitName)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerPriority = dialogView.findViewById<Spinner>(R.id.spinnerPriority)
        val inputTime = dialogView.findViewById<EditText>(R.id.etHabitTime)
        
        //Setup category
        val categories = arrayOf("Health", "Work", "Personal", "Learning", "General")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
        
        //Setup priority
        val priorities = arrayOf("Low", "Medium", "High")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter


        //add new task
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val habitName = inputName.text.toString().trim()
                if (habitName.isNotEmpty()) {
                    val dailyData = todoMap.getOrPut(date) { DailyData() }
                    val task = Task(
                        name = habitName,
                        category = categories[spinnerCategory.selectedItemPosition],
                        priority = priorities[spinnerPriority.selectedItemPosition],
                        time = inputTime.text.toString().trim()
                    )
                    dailyData.tasks.add(task)
                    saveTasks()
                    loadTasksForDate(date)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //edit the task
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
    //status
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

    //add/edit mood
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

    //Saved in SharedPreferences
    private fun saveTasks() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(todoMap)
        prefs.edit().putString(DATA_KEY, json).apply()
    }


    //load from the local storage
    private fun loadTasks() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(DATA_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableMap<String, DailyData>>() {}.type
            todoMap = gson.fromJson(json, type)
        }
    }
}
