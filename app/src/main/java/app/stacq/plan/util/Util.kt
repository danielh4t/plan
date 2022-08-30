package app.stacq.plan.util

fun String.sentenceCase(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}