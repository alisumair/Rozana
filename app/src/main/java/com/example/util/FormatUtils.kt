package com.example.util

import com.example.data.model.AppLanguage
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    private val decimalFormat = DecimalFormat("#,##0")

    fun formatPKR(amount: Double, language: AppLanguage = AppLanguage.ENGLISH): String {
        val formattedNumber = decimalFormat.format(amount)
        return if (language == AppLanguage.URDU) {
            "$formattedNumber روپے"
        } else {
            "Rs. $formattedNumber"
        }
    }

    fun formatDate(timestamp: Long, language: AppLanguage = AppLanguage.ENGLISH): String {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }

        val isToday = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val isYesterday = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - target.get(Calendar.DAY_OF_YEAR) == 1

        val isTomorrow = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                target.get(Calendar.DAY_OF_YEAR) - now.get(Calendar.DAY_OF_YEAR) == 1

        if (isToday) {
            return if (language == AppLanguage.URDU) "آج" else "Today"
        }
        if (isYesterday) {
            return if (language == AppLanguage.URDU) "کل (گزشتہ)" else "Yesterday"
        }
        if (isTomorrow) {
            return if (language == AppLanguage.URDU) "کل (آئندہ)" else "Tomorrow"
        }

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getCurrentMonthYear(): String {
        val cal = Calendar.getInstance()
        return String.format("%04d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

    fun getDaysDifference(targetTimestamp: Long): Long {
        val now = System.currentTimeMillis()
        val diff = targetTimestamp - now
        return diff / (1000 * 60 * 60 * 24)
    }
}
