package app.stacq.plan.util


import java.time.Instant
import java.time.YearMonth
import java.util.*

fun isFinishAtInFuture(finishAt: Long): Boolean {
    val now: Long = Instant.now().epochSecond
    return now > finishAt
}

fun millisInFuture(finishAt: Long): Long {
    val now: Long = Instant.now().epochSecond
    return (finishAt - now) * 1000L
}

fun numberOfDays(): Int {
    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val ym = YearMonth.of(year, month)
    return ym.lengthOfMonth()
}

fun startDay(day: Int): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, day + 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis / 1000L
}