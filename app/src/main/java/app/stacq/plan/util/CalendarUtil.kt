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

    fun setLocalTimeSeconds(seconds: Long) {
        localCalendar.timeInMillis = seconds * 1000L
    }

    fun setLocalDate(millis: Long) {
        val calendarToUpdate = Calendar.getInstance()
        calendarToUpdate.timeInMillis = millis

        // Extract year, month, and day from the selected date
        val year = calendarToUpdate.get(Calendar.YEAR)
        val month = calendarToUpdate.get(Calendar.MONTH)
        val dayOfMonth = calendarToUpdate.get(Calendar.DAY_OF_MONTH)

        localCalendar.set(Calendar.YEAR, year)
        localCalendar.set(Calendar.MONTH, month)
        localCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }
    fun getLocalTimeInMillis(): Long {
        return localCalendar.timeInMillis
    }

    fun getLocalTimeUTC(): Long {
        localCalendar.timeZone = TimeZone.getTimeZone("UTC")
        return localCalendar.timeInMillis / 1000L
    }

    fun getTodayTimeInMillis(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun setTodayTimeInMillis() {
        localCalendar.timeInMillis = Calendar.getInstance().timeInMillis
    }

    fun differenceInDays(epochTimeInSeconds: Long): Long {
        val differenceCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        differenceCalendar.timeInMillis = epochTimeInSeconds * 1000L

        // Calculate the difference in milliseconds between the current time and past epoch time
        val differenceInMillis = calendar.timeInMillis - differenceCalendar.timeInMillis

        // Convert the difference in milliseconds to days
        return differenceInMillis / (24 * 60 * 60 * 1000)
    }

    fun startToEndDifferenceInSeconds(startTimeInSeconds: Long, endTimeInSeconds: Long): Long {
        val startCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        startCalendar.timeInMillis = startTimeInSeconds * 1000L

        val endCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        endCalendar.timeInMillis = endTimeInSeconds * 1000L

        // Calculate the difference in milliseconds between the current time and past epoch time
        val differenceInMillis = endCalendar.timeInMillis - startCalendar.timeInMillis

        // Convert the difference in milliseconds to seconds
        return differenceInMillis / 1000L
    }
}