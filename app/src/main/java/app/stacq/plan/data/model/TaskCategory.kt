package app.stacq.plan.data.model

data class TaskCategory(
    var id: String,
    var createdAt: Long,
    var title: String,
    var completed: Boolean,
    var completedAt: Long,
    var categoryName: String,
    var timerFinishAt: Long,
    var timerAlarm: Boolean,
)