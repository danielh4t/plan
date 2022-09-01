package app.stacq.plan.data.model

data class TaskCategory(
    var id: String,
    var title: String,
    var completed: Boolean,
    val completedAt: Long,
    var categoryName: String,
)