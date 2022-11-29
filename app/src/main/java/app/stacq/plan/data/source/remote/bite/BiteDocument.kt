package app.stacq.plan.data.source.remote.bite

import androidx.annotation.Keep

@Keep
data class BiteDocument(
    val id: String? = null,
    val name: String? = null,
    val createdAt: Long? = null,
    var taskId: String? = null,
    var categoryId: String? = null,
    var completed: Boolean = false,
    var completedAt: Long? = null,
)