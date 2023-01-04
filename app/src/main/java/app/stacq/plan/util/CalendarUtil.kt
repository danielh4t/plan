package app.stacq.plan.util

import java.util.*

class CalendarUtil {

    private val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))

    fun currentYear(): String {
        val year: Int = calendar.get(Calendar.YEAR)
        return year.toString()
    }

    fun day(): Int {
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun days(): Int {
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
}