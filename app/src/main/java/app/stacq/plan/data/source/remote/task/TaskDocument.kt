package app.stacq.plan.data.source.remote.task

import androidx.annotation.Keep

@Keep
data class TaskDocument(
    val id: String? = null,
    val createdAt: Long? = null,
    val name: String? = null,
    var completedAt: Long? = null,
    var notes: String? = null,
)