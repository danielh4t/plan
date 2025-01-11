package app.stacq.plan.utility

import com.google.android.play.integrity.internal.n
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object TimeFormatter {
    fun formatDate(timestamp: Long, pattern: String = "MMMM d, yyyy", locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(Date(timestamp))
    }

    fun formatTaskTime(createdAt: Long, completedAt: Long?): String {
        // TODO: Improve by having different Task class
        if(completedAt == null) return ""
        // Duration in minutes
        val durationInMinutes = (completedAt - createdAt) / 1000 / 60

        return when {
            durationInMinutes < 60 -> {
                "$durationInMinutes minutes"
            }
            durationInMinutes < 1440 -> {
                val hours = durationInMinutes / 60
                val minutes = durationInMinutes % 60
                "$hours hour${if (hours > 1) "s" else ""} and $minutes minute${if (minutes > 1) "s" else ""}"
            }
            else -> {
                val days = durationInMinutes / 1440
                val hours = (durationInMinutes % 1440) / 60
                "$days day${if (days > 1) "s" else ""} and $hours hour${if (hours > 1) "s" else ""}"
            }
        }
    }
}
