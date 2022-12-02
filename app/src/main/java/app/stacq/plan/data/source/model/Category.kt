package app.stacq.plan.data.source.model

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.category.CategoryEntity
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Category(
    var id: String,
    var name: String,
    var color: String,
    var enabled: Boolean
) : Parcelable


fun CategoryEntity.toCategory() = Category(
    id = id,
    name = name,
    color = color,
    enabled = enabled
)