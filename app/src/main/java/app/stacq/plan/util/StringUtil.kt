package app.stacq.plan.util

import app.stacq.plan.R

fun String.sentenceCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

fun defaultColors(color: String): Int {
    val palette = listOf(
        R.color.plan_red,
        R.color.plan_orange,
        R.color.plan_indigo,
        R.color.plan_blue,
    )

    return when (color) {
        "Code" -> R.color.plan_orange
        "Hack" -> R.color.plan_green
        "Life" -> R.color.plan_indigo
        "Work" -> R.color.plan_blue
        else -> palette.random()
    }
}
