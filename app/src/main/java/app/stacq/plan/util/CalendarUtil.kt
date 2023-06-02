package app.stacq.plan.util

import java.util.*

class CalendarUtil {

    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    private val localCalendar = Calendar.getInstance()

    fun setLocalHour(hour: Int) {
        localCalendar.set(Calendar.HOUR_OF_DAY, hour)
    }

    fun localHour(): Int {
        return localCalendar.get(Calendar.HOUR_OF_DAY)
    }

    fun setLocalMinute(minute: Int) {
        localCalendar.set(Calendar.MINUTE, minute)
    }

    fun localMinute(): Int {
        return localCalendar.get(Calendar.MINUTE)
    }

    fun time(): Date {
        return calendar.time
    }

    fun localTime(): Date {
        return localCalendar.time
    }

    fun setLocalTime(millis: Long) {
        localCalendar.timeInMillis = millis
    }

    fun getLocalTimeUTC(): Long {
        localCalendar.timeZone = TimeZone.getTimeZone("UTC")
        return localCalendar.timeInMillis / 1000L
    }

    fun getTodayTimeInMillis(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun differenceInDays(epochTimeInSeconds: Long): Long {
        val differenceCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        differenceCalendar.timeInMillis = epochTimeInSeconds * 1000L

        // Calculate the difference in milliseconds between the current time and past epoch time
        val differenceInMillis = calendar.timeInMillis - differenceCalendar.timeInMillis

        // Convert the difference in milliseconds to days
        return differenceInMillis / (24 * 60 * 60 * 1000)
    }

    fun getDayElapsedMinutes(): Int {
        val hour = localCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = localCalendar.get(Calendar.MINUTE)
        return (hour * 60 + minute)
    }
}