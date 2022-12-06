package app.stacq.plan.util


import java.time.Instant
import java.time.YearMonth
import java.util.*

fun String.sentenceCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun isFinishAtInFuture(finishAt: Long): Boolean {
    val now: Long = Instant.now().epochSecond
    return now > finishAt
}

fun millisInFuture(finishAt: Long): Long {
    val now: Long = Instant.now().epochSecond
    return (finishAt - now) * 1000L
}

fun defaultColors(color: String): String {
    val palette = listOf(
        "#FF74B1",
        "#FFB200",
        "#B2A4FF",
        "#FF00E4",
        "#FF4848",
        "#00EAD3",
        "#FC5404",
        "#F637EC",
        "#4D77FF",
        "#93FFD8"
    )

    return when (color) {
        "Code" -> "#FF7F50"
        "Hack" -> "#2ED573"
        "Life" -> "#FDCD21"
        "Work" -> "#1E90FF"
        else -> palette.random()
    }
}

fun numberOfDays(): Int {
    val calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val ym = YearMonth.of(year, month)
    return ym.lengthOfMonth()
}


fun startOfDay(day: Int): Long {
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

fun endOfDay(day: Int): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    calendar.set(Calendar.DAY_OF_MONTH, day + 1)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    return calendar.timeInMillis / 1000L
}