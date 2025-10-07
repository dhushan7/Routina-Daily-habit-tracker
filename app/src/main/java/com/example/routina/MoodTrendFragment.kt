package com.example.routina

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class MoodTrendFragment : Fragment() {

    private lateinit var lineChart: LineChart
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_trend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lineChart = view.findViewById(R.id.moodChart)
        setupChart()
        loadMoodData()
    }

    private fun setupChart() {
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)
        lineChart.setBackgroundColor(Color.WHITE)

        //Configure X-axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -6 + value.toInt())
                return displayDateFormat.format(calendar.time)
            }
        }

        //Configure Y-axis
        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 5f
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> "😢"
                    1 -> "😐"
                    2 -> "😊"
                    3 -> "😡"
                    4 -> "❤️"
                    else -> ""
                }
            }
        }

        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = false
    }

    private fun loadMoodData() {
        val entries = mutableListOf<Entry>()
        val calendar = Calendar.getInstance()
        
        //get last 7 days data
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_YEAR, if (i == 6) -6 else 1)
            val date = dateFormat.format(calendar.time)
            val moodValue = getMoodValueForDate(date)
            entries.add(Entry((6 - i).toFloat(), moodValue.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Mood Trend")
        dataSet.color = Color.parseColor("#4CAF50")         //line
        dataSet.setCircleColor(Color.parseColor("#4CAF50")) //points
        dataSet.lineWidth = 3f
        dataSet.circleRadius = 6f
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawValues(false)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#558BC34A")

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun getMoodValueForDate(date: String): Int {
        val prefs = requireContext().getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("todo_data", null)
        
        if (json.isNullOrEmpty()) return 1 // Default neutral mood
        
        val type = object : TypeToken<MutableMap<String, DailyData>>() {}.type
        val todoMap: MutableMap<String, DailyData> = gson.fromJson(json, type)
        val dailyData = todoMap[date] ?: return 1

        return when (dailyData.feeling) {
            "😢" -> 0
            "😐" -> 1
            "😊" -> 2
            "😡" -> 3
            "❤️" -> 4
            else -> 1
        }
    }
}

