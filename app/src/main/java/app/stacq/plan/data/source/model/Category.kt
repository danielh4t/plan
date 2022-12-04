package app.stacq.plan.data.source.model

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.remote.category.CategoryDocument
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Category(
    var id: String,
    var name: String,
    var color: String,
    var enabled: Boolean
) : Parcelable

fun Category.asCategoryEntity() = CategoryEntity(
    id = id,
    name = name,
    color = color,
    enabled = enabled
)

fun Category.asCategoryDocument() = CategoryDocument(
    id = id,
    name = name,
    color = color,
    enabled = enabled
)

fun CategoryEntity.asCategory() = Category(
    id = id,
    name = name,
    color = color,
    enabled = enabled
)