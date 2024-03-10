package app.stacq.plan.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtil {

    fun formatTimeAgo(completedAt: Long): String {
        val currentTime = TimeUtil().nowInSeconds()
        // elapsed time between completed at and current time
        val timestampDiff = currentTime - completedAt

        // epoch seconds of start of today and yesterday
        val startOfToday = CalendarUtil().getUTCStartOfDayInMillis() / 1000L
        val startOfYesterday = CalendarUtil().getUTCStartOfOtherDayInMillis(-1) / 1000L
        val startOfThreeDays = CalendarUtil().getUTCStartOfOtherDayInMillis(-3) / 1000L

        return when {
            timestampDiff < (currentTime - startOfToday) -> "Today"
            timestampDiff < (currentTime - startOfYesterday) -> "Yesterday"
            else -> {
                val dateFormat =
                    // day of week
                    if (timestampDiff < (currentTime - startOfThreeDays))
                        SimpleDateFormat("EEEE", Locale.getDefault())
                    else  SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val date = Date(completedAt * 1000)
                return dateFormat.format(date)
            }
        }
    }
}
