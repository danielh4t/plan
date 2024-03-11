package app.stacq.plan.util

import java.util.*

class CalendarUtil {

    private val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    private val localCalendar = Calendar.getInstance(TimeZone.getDefault())

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

    fun localTime(): Date {
        return localCalendar.time
    }

    fun time(): Date {
        return calendar.time
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

    /**
     * Returns the time in milliseconds represented by the local calendar instance.
     * If the local time zone is ahead of UTC, time value is negative.
     * This function ensures that it returns 0 to handle negative time values gracefully.
     *
     * @return The time in milliseconds, or 0 if the time is negative.
     */
    fun getLocalTimeInMillis(): Long {
        val timeInMillis = localCalendar.timeInMillis
        return if (timeInMillis < 0) 0 else timeInMillis
    }

    fun getLocalTimeUTCInSeconds(): Long {
        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcCalendar.timeInMillis = localCalendar.timeInMillis
        return utcCalendar.timeInMillis / 1000L
    }

    fun getTodayTimeInMillis(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun setTodayTimeInMillis() {
        localCalendar.timeInMillis = Calendar.getInstance().timeInMillis
    }

    fun reset() {
        localCalendar.timeInMillis = 0L
    }

    fun getUTCStartOfDayInMillis(): Long {
        // Set the time fields to zero
        val calendar = localCalendar.clone() as Calendar
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Get the timestamp in milliseconds
        return calendar.timeInMillis
    }

    fun getUTCStartOfOtherDayInMillis(difference: Int): Long {
        val calendar = localCalendar.clone() as Calendar
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        calendar.add(Calendar.DAY_OF_MONTH, difference)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Get the timestamp in milliseconds
        return calendar.timeInMillis
    }

    fun getUTCCurrentTimeInMillis(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        return calendar.timeInMillis
    }

    fun getLocalCurrentTimeInMillis(): Long {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        return calendar.timeInMillis
    }

    fun differenceInDays(epochTimeInSeconds: Long): Long {
        val differenceCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        differenceCalendar.timeInMillis = epochTimeInSeconds * 1000L

        // Calculate the difference in milliseconds between the current time and past epoch time
        val differenceInMillis = calendar.timeInMillis - differenceCalendar.timeInMillis

        // Convert the difference in milliseconds to days
        return differenceInMillis / (24 * 60 * 60 * 1000)
    }

    fun startToEndDifferenceInSeconds(startTimeInSeconds: Long, endTimeInSeconds: Long): Long {
        val startCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        startCalendar.timeInMillis = startTimeInSeconds * 1000L

        val endCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        endCalendar.timeInMillis = endTimeInSeconds * 1000L

        // Calculate the difference in milliseconds between the current time and past epoch time
        val differenceInMillis = endCalendar.timeInMillis - startCalendar.timeInMillis

        // Convert the difference in milliseconds to seconds
        return differenceInMillis.millisToSeconds()
    }

    private fun Long.millisToSeconds(): Long {
        return this / 1000L
    }
}