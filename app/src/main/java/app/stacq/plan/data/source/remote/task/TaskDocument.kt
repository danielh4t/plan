package app.stacq.plan.data.source.remote.task

import androidx.annotation.Keep

@Keep
data class TaskDocument(
    val id: String? = null,
    val createdAt: Long? = null,
    val name: String? = null,
    var categoryId: String? = null,
    var completed: Boolean = false,
    var completedAt: Long? = null,
    var timerAlarm: Boolean = false,
    var timerFinishAt: Long? = null,
    var priority: Int? = null
)