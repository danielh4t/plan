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