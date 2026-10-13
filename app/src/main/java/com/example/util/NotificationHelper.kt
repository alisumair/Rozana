package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    const val CHANNEL_ID_BILLS = "rozana_bills_channel"
    const val CHANNEL_ID_TASKS = "rozana_tasks_channel"
    const val CHANNEL_ID_NAMAZ = "rozana_namaz_channel"
    const val CHANNEL_ID_MEALS = "rozana_meals_channel"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val billChannel = NotificationChannel(
                CHANNEL_ID_BILLS,
                "Bill & Payment Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for electricity, gas, internet and utility bills"
                enableVibration(true)
            }

            val taskChannel = NotificationChannel(
                CHANNEL_ID_TASKS,
                "Daily Tasks & Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for daily activities and personal documents"
            }

            val namazChannel = NotificationChannel(
                CHANNEL_ID_NAMAZ,
                "Prayer & Azan Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for Fajr, Dhuhr, Asr, Maghrib, and Isha prayers"
                enableVibration(true)
            }

            val mealsChannel = NotificationChannel(
                CHANNEL_ID_MEALS,
                "Breakfast, Lunch & Dinner Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for timely meals and hydration"
            }

            notificationManager.createNotificationChannel(billChannel)
            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(namazChannel)
            notificationManager.createNotificationChannel(mealsChannel)
        }
    }

    fun showBillReminderNotification(
        context: Context,
        notificationId: Int,
        billTitle: String,
        amount: Double,
        dueDateFormatted: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BILLS)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Bill Reminder: $billTitle")
            .setContentText("Amount: Rs. ${amount.toInt()} due on $dueDateFormatted. Don't forget to pay!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun showNamazAlertNotification(
        context: Context,
        prayerName: String,
        prayerTime: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_NAMAZ)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🕌 وقتِ نماز: $prayerName ($prayerTime)")
            .setContentText("نماز کا وقت ہو چکا ہے۔ 'حی علی الصلاۃ، حی علی الفلاح'")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(prayerName.hashCode(), notification)
    }

    fun showMealAlertNotification(
        context: Context,
        mealName: String,
        mealTime: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MEALS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🍽️ $mealName کا وقت ($mealTime)")
            .setContentText("کھانے اور پانی پینے کا وقت ہو گیا ہے۔ اپنی صحت کا خیال رکھیں۔")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(mealName.hashCode(), notification)
    }
}
