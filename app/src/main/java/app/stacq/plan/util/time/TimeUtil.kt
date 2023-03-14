package app.stacq.plan.util.time

import android.os.SystemClock
import app.stacq.plan.util.constants.TimerConstants
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class TimeUtil {

    private val instant: Instant = Instant.now()

    fun millisInFuture(finishAt: Long): Long {
        val now: Long = instant.epochSecond
        return (finishAt - now) * 1000L
    }

    fun alarmTriggerTimer(finishAt: Long): Long {
        val now: Long = instant.epochSecond
        val futureMillis = (finishAt - now) * 1000L
        return SystemClock.elapsedRealtime() + futureMillis
    }

    fun plusSecondsEpoch(seconds: Long): Long {
        return instant.plusMillis(seconds).epochSecond
    }

    fun timeSeconds(hour: Int, minute: Int): Long {
        val now = LocalDateTime.now()
        val timerTime = LocalTime.of(hour, minute)
        val timerDateTime = LocalDateTime.of(now.toLocalDate(), timerTime)

        val millis = if (timerDateTime.isAfter(now)) {
            // timer time on clock is in future
            val timerEpochTime =
                timerDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            val nowEpochTime = now.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            timerEpochTime - nowEpochTime
        } else {
            // timer time on clock is past
            val tomorrowDateTime = timerDateTime.plusDays(1)
            val timerEpochTime =
                tomorrowDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            val nowEpochTime = now.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            timerEpochTime - nowEpochTime
        }

        return millis / TimerConstants.TIME_MILLIS_TO_SECONDS
    }
}