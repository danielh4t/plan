package app.stacq.plan.data.source.remote.goal

import androidx.annotation.Keep

@Keep
data class GoalDocument(
    val id: String? = null,
    val createdAt: Long? = null,
    val name: String? = null,
    val measure: String? = null,
    var result: String? = null,
    var categoryId: String? = null,
    var days: Int? = null,
    var progress: Int? = null,
    var completed: Boolean? = null,
    var completedAt: Long? = null,
    var generate: Boolean? = null,
    var deleted: Boolean? = null,
)