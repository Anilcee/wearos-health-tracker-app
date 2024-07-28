package com.example.gethealthy.presentation

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.gethealthy.R

class NotificationManager {

    companion object {
        private const val CHANNEL_ID = "MY_CHANNEL_ID"
        private const val CHANNEL_NAME = "My Channel"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = "My Channel Description"
                }

                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        @SuppressLint("MissingPermission")
        fun sendNotification(context: Context, title: String, message: String) {
            val largeIconBitmap = ContextCompat.getDrawable(context, R.drawable.gethealthyicon)?.toBitmap()

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.gethealthyicon)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIconBitmap)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            NotificationManagerCompat.from(context).notify(CHANNEL_ID.hashCode(), notification)
        }
    }
}