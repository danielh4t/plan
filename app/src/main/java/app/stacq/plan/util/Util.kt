package app.stacq.plan.util

fun String.titleCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}