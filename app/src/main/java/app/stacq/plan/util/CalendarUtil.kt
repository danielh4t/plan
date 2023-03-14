package app.stacq.plan.util

import java.util.*

class CalendarUtil {

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))

    fun hour(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun minute(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MINUTE)
    }

    fun days(): Int {
        val calendar = Calendar.getInstance()
        calendar.get(Calendar.YEAR)
        return calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
    }

    fun yearStartAt(): Long {
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis / 1000L
    }

    fun yearStartAtMillis(): Long {
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, 0)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun todayStartAtMillis(): Long {
        return calendar.timeInMillis
    }

    fun differenceInDays(timestamp: Long): Long {
        val differenceCalendar = Calendar.getInstance()
        differenceCalendar.timeInMillis = timestamp * 1000L

        // Calculate the difference in milliseconds between the two dates
        val differenceInMillis = calendar.timeInMillis - differenceCalendar.timeInMillis

        // Convert the difference in milliseconds to days
        return differenceInMillis / (24 * 60 * 60 * 1000)
    }
}