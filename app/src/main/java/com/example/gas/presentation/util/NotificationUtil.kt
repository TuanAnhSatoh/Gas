package com.example.gas.presentation.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.gas.R
import com.example.gas.presentation.ui.activity.AlertActivity
import timber.log.Timber

object NotificationUtil {

    private const val REMINDER_CHANNEL_ID = "medication_reminders"
    private const val REMINDER_CHANNEL_NAME = "Medication Reminders"
    private const val ALERT_CHANNEL_ID = "alert_channel"
    private const val ALERT_CHANNEL_NAME = "Alert Measurement Health"

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    fun createAlertNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ALERT_CHANNEL_ID,
                ALERT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alert Measurement Health Over Threshold"
                enableLights(true)
                enableVibration(true)
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showWarningNotification(context: Context, title: String, message: String) {
        if (!hasNotificationPermission(context)) {
            Timber.tag("NotificationUtil").w("Missing POST_NOTIFICATIONS permission. Skipping warning notification.")
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, AlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_warning)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)

        Timber.tag("NotificationUtil")
            .d("Showing warning notification with title=$title and message=$message")

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}