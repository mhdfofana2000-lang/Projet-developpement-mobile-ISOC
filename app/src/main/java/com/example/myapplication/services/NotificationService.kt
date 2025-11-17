package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationService(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "default_channel"
        const val NOTIFICATION_ID = 1
    }

    fun showSimpleNotification(title: String, message: String) {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Vérification simplifiée des permissions
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            // Gérer l'absence de permission (log ou message silencieux)
            android.util.Log.e("NotificationService", "Permission notifications manquante", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notifications MyApplication",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal par défaut pour MyApplication"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}