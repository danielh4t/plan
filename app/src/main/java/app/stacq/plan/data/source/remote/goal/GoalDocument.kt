package app.stacq.plan.data.source.remote.goal

import androidx.annotation.Keep

@Keep
data class GoalDocument(
    val id: String? = null,
    val createdAt: Long? = null,
    val name: String? = null,
    var categoryId: String? = null,
    var achieved: Boolean? = null,
    var achievedAt: Long? = null,
    var achievedBy: Long? = null,
)