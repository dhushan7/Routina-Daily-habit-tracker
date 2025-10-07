package com.example.routina

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt

class SensorManager(private val context: Context) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometer: Sensor? = null
    private var stepCounter: Sensor? = null
    
    private var lastShakeTime = 0L
    private val SHAKE_THRESHOLD = 15.0f
    private val SHAKE_SLOP_TIME_MS = 500L
    
    private var stepCount = 0
    private var lastStepCount = 0
    
    var onShakeDetected: (() -> Unit)? = null
    var onStepCountChanged: ((Int) -> Unit)? = null
    
    init {
        initializeSensors()
    }
    
    private fun initializeSensors() {
        // Initialize accelerometer for shake detection
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        // Initialize step counter
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    handleAccelerometerEvent(sensorEvent)
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    handleStepCounterEvent(sensorEvent)
                }
            }
        }
    }
    
    private fun handleAccelerometerEvent(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        
        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        
        val currentTime = System.currentTimeMillis()
        if (acceleration > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > SHAKE_SLOP_TIME_MS) {
            lastShakeTime = currentTime
            onShakeDetected?.invoke()
        }
    }
    
    private fun handleStepCounterEvent(event: SensorEvent) {
        val currentStepCount = event.values[0].toInt()
        
        if (lastStepCount == 0) {
            lastStepCount = currentStepCount
        }
        
        val newSteps = currentStepCount - lastStepCount
        if (newSteps > 0) {
            stepCount += newSteps
            lastStepCount = currentStepCount
            onStepCountChanged?.invoke(stepCount)
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    
    fun getCurrentStepCount(): Int = stepCount
    
    fun resetStepCount() {
        stepCount = 0
        lastStepCount = 0
    }
    
    fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }
    
    fun registerListeners() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        stepCounter?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }
}

