package app.stacq.plan.util

import android.os.SystemClock
import java.time.Instant

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
        return instant.plusSeconds(seconds).epochSecond
    }
}