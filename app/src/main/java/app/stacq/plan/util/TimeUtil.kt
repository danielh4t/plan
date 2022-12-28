package app.stacq.plan.util


import android.os.SystemClock
import java.time.Instant
import java.time.YearMonth
import java.util.*

fun millisInFuture(finishAt: Long): Long {
    val now: Long = Instant.now().epochSecond
    return (finishAt - now) * 1000L
}

fun alarmTriggerTimer(finishAt: Long): Long {
    return SystemClock.elapsedRealtime() + millisInFuture(finishAt)
}

fun plusSecondsEpoch(seconds: Long): Long {
    return Instant.now().plusSeconds(seconds).epochSecond
}

fun currentYear(): String {
    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    return year.toString()
}

fun yearStartAt(): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, 0)
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis / 1000L
}