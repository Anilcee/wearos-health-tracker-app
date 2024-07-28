package com.example.gethealthy.presentation

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.gethealthy.R
import java.text.SimpleDateFormat
import java.util.*

class StepCounter : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var walking = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var stepGoal = 0
    private lateinit var dbHelper: Database
    private var lastSavedDate = ""

    private var halfStepGoalReached = false
    private var stepGoalReached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        NotificationManager.createNotificationChannel(this)

        dbHelper = Database(this)
        val userInfo = dbHelper.getUserInfo()
        stepGoal = userInfo?.stepGoal ?: 10000

        findViewById<TextView>(R.id.stepGoalTextView).text = "$stepGoal"

        val currentDate = getCurrentDate()
        val stepData = dbHelper.getStepData(currentDate)

        totalSteps = stepData?.steps?.toFloat() ?: 0f
        previousTotalSteps = stepData?.steps?.toFloat() ?: 0f
        lastSavedDate = currentDate
        val buttonStepDatas = findViewById<Button>(R.id.buttonStepDatas)
        buttonStepDatas.setOnClickListener {
            val intent = Intent(this, StepDatas::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        walking = true
        val userInfo = dbHelper.getUserInfo()
        stepGoal = userInfo?.stepGoal ?: 10000

        findViewById<TextView>(R.id.stepGoalTextView).text = "$stepGoal"
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Toast.makeText(this, "Sensör bulunamadı.", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        walking = false
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (walking) {
            event?.let {
                val currentSteps = it.values[0]

                val currentDate = getCurrentDate()
                if (lastSavedDate != currentDate) {
                    lastSavedDate = currentDate
                    previousTotalSteps = currentSteps
                    totalSteps = 0f
                    halfStepGoalReached = false
                    stepGoalReached = false
                }

                totalSteps = currentSteps - previousTotalSteps
                findViewById<TextView>(R.id.steptextView).text = totalSteps.toInt().toString()

                if (!halfStepGoalReached && totalSteps >= stepGoal / 2) {
                    sendNotification("Böyle Devam Et!", "Adım hedefinin yarısına ulaştın!")
                    halfStepGoalReached = true
                }
                if (!stepGoalReached && totalSteps >= stepGoal) {
                    sendNotification("Tebrikler! Adım Hedefine Ulaştın", "Adım hedefini tamamladın!")
                    stepGoalReached = true
                }
                dbHelper.saveOrUpdateStepData(StepData(currentDate, totalSteps.toInt()))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun sendNotification(title: String, message: String) {
        NotificationManager.sendNotification(this, title, message)
    }
}
