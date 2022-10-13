package app.stacq.plan.util


import java.time.Instant

fun String.sentenceCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun isFinishAtInFuture(finishAt: Long): Boolean {
    val now: Long = Instant.now().epochSecond
    return now > finishAt
}

fun millisInFuture(finishAt: Long, now: Long): Long {
    return (finishAt - now) * 1000L
}

