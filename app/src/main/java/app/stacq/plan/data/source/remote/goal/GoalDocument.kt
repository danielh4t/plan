package app.stacq.plan.data.source.remote.goal

import androidx.annotation.Keep

@Keep
data class GoalDocument(
    val id: String? = null,
    val name: String? = null,
    val createdAt: Long? = null,
    var categoryId: String? = null,
    var days: Int? = null,
    var completed: Boolean? = null,
    var completedAt: Long? = null,
    var generate: Boolean? = null,
)