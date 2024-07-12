package app.stacq.plan.domain

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.remote.category.CategoryDocument
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Category(
    var id: String,
    var createdAt: Long,
    var name: String,
    var color: String,
    var enabled: Boolean,
    var archived: Boolean,
) : Parcelable

fun Category.asEntity() = CategoryEntity(
    id = id,
    createdAt = createdAt,
    name = name,
    color = color,
    enabled = enabled
)

fun Category.asDocument() = CategoryDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    color = color,
    enabled = enabled,
    archived = archived,
)

fun CategoryEntity.asCategory() = Category(
    id = id,
    createdAt = createdAt,
    name = name,
    color = color,
    enabled = enabled,
    archived = archived,
)

fun CategoryDocument.asCategory() = Category(
    id = id ?: "",
    createdAt = createdAt ?: 0L,
    name = name ?: "",
    color = color ?: "",
    enabled = enabled ?: false,
    archived = archived ?: false,
)