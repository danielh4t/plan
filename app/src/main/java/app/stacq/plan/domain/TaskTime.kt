package app.stacq.plan.domain

data class TaskTime(
    val id: String,
    val name: String,
    val createdAt: Long,
    val time: String,
)