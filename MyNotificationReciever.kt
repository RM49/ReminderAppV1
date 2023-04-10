package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("notification")
        if (message != null) {
            // Create notification channel (required for Android 8.0+)
            createNotificationChannel2(context)
            Log.d("TAG", "and here!!!!")
            // Create notification builder
            val builder = NotificationCompat.Builder(context!!, "my_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Remember!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // Show notification
            with(NotificationManagerCompat.from(context)) {
                notify((0..100).random(), builder.build()) // id is random, could collide should be more sophisticated
            }
        }
    }

    private fun createNotificationChannel2(context: Context?) {
        Log.d("TAG", "ALSO HERE!")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My App Notification Channel"
            val descriptionText = "Channel for My App notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("my_channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}