package com.example.gethealthy.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.gethealthy.R
import java.text.SimpleDateFormat
import java.util.*

class Calories : ComponentActivity() {

    private var steps = 0
    private lateinit var dbHelper: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories)

        dbHelper = Database(this)

        val userInfo = dbHelper.getUserInfo()
        val currentDate = getCurrentDate()
        val stepData = dbHelper.getStepData(currentDate)


        steps = stepData?.steps ?: 0
        val age = userInfo?.age ?: 30
        val gender = userInfo?.gender ?: "female"
        val weight = userInfo?.weight?.toFloat() ?: 70f
        val height = userInfo?.height?.toFloat() ?: 170f

        val calories = bazalMetobolizma(age, gender, weight, height).toInt()
        findViewById<TextView>(R.id.calorieCounter).text ="$calories"
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun bazalMetobolizma(age: Int, gender: String, weight: Float, height: Float): Float {
        val bmr = if (gender == "male") {
            88.362f + (13.397f * weight) + (4.799f * height) - (5.677f * age)
        } else {
            447.593f + (9.247f * weight) + (3.098f * height) - (4.330f * age)
        }
        val activeCalories = steps * 0.05f
        return bmr + activeCalories
    }
}
