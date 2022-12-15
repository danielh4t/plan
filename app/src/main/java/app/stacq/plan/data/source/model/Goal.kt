package app.stacq.plan.data.source.model

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.local.goal.GoalEntity
import app.stacq.plan.data.source.remote.goal.GoalDocument
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Goal(
    var id: String,
    var createdAt: Long,
    var name: String,
    var achieved: Boolean,
    var achievedAt: Long,
    var achievedBy: Long,
    var categoryId: String,
    var categoryName: String,
    var categoryColor: String,
) : Parcelable

fun Goal.asEntity() = GoalEntity(
    id = id,
    name = name,
    achieved = achieved,
    achievedAt = achievedAt,
    achievedBy = achievedBy,
    categoryId = categoryId,
)

fun Goal.asDocument() = GoalDocument(
    id = id,
    name = name,
    achieved = achieved,
    achievedAt = achievedAt,
    achievedBy = achievedBy,
    categoryId = categoryId,
)