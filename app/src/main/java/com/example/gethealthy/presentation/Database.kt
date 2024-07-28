package com.example.gethealthy.presentation

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class User(
    val gender: String,
    val height: Int,
    val weight: Int,
    val age: Int,
    val stepGoal: Int
)

data class StepData(
    val date: String,
    val steps: Int
)

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "user_info.db"
        const val DATABASE_VERSION = 3
        const val TABLE_USER_INFO = "user_info"
        const val TABLE_STEP_DATA = "step_data"
        const val COLUMN_ID = "id"
        const val COLUMN_GENDER = "gender"
        const val COLUMN_HEIGHT = "height"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_STEP_GOAL = "step_goal"
        const val COLUMN_AGE = "age"
        const val COLUMN_DATE = "date"
        const val COLUMN_STEPS = "steps"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserInfoTable = ("CREATE TABLE $TABLE_USER_INFO ("
                + "$COLUMN_ID INTEGER PRIMARY KEY,"
                + "$COLUMN_GENDER TEXT,"
                + "$COLUMN_HEIGHT INTEGER,"
                + "$COLUMN_WEIGHT INTEGER,"
                + "$COLUMN_AGE INTEGER,"
                + "$COLUMN_STEP_GOAL INTEGER)")

        val createStepDataTable = ("CREATE TABLE $TABLE_STEP_DATA ("
                + "$COLUMN_DATE TEXT PRIMARY KEY,"
                + "$COLUMN_STEPS INTEGER)")

        db?.execSQL(createUserInfoTable)
        db?.execSQL(createStepDataTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER_INFO")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STEP_DATA")
        onCreate(db)
    }

    fun saveOrUpdateUserInfo(userInfo: User) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, 1)
            put(COLUMN_GENDER, userInfo.gender)
            put(COLUMN_HEIGHT, userInfo.height)
            put(COLUMN_WEIGHT, userInfo.weight)
            put(COLUMN_AGE, userInfo.age)
            put(COLUMN_STEP_GOAL, userInfo.stepGoal)
        }

        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USER_INFO WHERE $COLUMN_ID = 1", null)

        if (cursor.moveToFirst()) {
            db.update(TABLE_USER_INFO, values, "$COLUMN_ID = ?", arrayOf("1"))
        } else {
            db.insert(TABLE_USER_INFO, null, values)
        }
        cursor.close()
    }

    fun getUserInfo(): User? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USER_INFO WHERE $COLUMN_ID = 1", null)
        return if (cursor.moveToFirst()) {
            val gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
            val height = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT))
            val weight = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT))
            val age = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AGE))
            val stepGoal = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEP_GOAL))
            User(gender, height, weight, age, stepGoal)
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun saveOrUpdateStepData(stepData: StepData) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, stepData.date)
            put(COLUMN_STEPS, stepData.steps)
        }

        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_STEP_DATA WHERE $COLUMN_DATE = ?", arrayOf(stepData.date))

        if (cursor.moveToFirst()) {
            db.update(TABLE_STEP_DATA, values, "$COLUMN_DATE = ?", arrayOf(stepData.date))
        } else {
            db.insert(TABLE_STEP_DATA, null, values)
        }
        cursor.close()
    }

    fun getStepData(date: String): StepData? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_STEP_DATA WHERE $COLUMN_DATE = ?", arrayOf(date))
        return if (cursor.moveToFirst()) {
            val steps = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STEPS))
            StepData(date, steps)
        } else {
            null
        }.also {
            cursor.close()
        }
    }
    fun getLast7DaysStepData(): List<StepData> {
        val db = this.readableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val stepDataList = mutableListOf<StepData>()

        for (i in 0 until 7) {
            val date = dateFormat.format(calendar.time)
            val stepData = getStepData(date) ?: StepData(date, 0)
            stepDataList.add(stepData)
            calendar.add(Calendar.DATE, -1)
        }

        return stepDataList
    }
}

