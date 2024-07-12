package app.stacq.plan.data.source.remote.category

import androidx.annotation.Keep

@Keep
data class CategoryDocument(
    val id: String? = null,
    val createdAt: Long? = null,
    val name: String? = null,
    var color: String? = null,
    var enabled: Boolean? = null,
    var archived: Boolean? = null,
)