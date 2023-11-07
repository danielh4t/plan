package app.stacq.plan.domain

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.goal.GoalEntity
import app.stacq.plan.data.source.local.goal.GoalEntityAndCategoryEntity
import app.stacq.plan.data.source.remote.goal.GoalDocument
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Goal(
    var id: String,
    var createdAt: Long,
    var name: String,
    var measure: String,
    var result: String,
    var categoryId: String,
    var days: Int,
    var progress: Int,
    var completedAt: Long,
    var generate: Boolean,
    var deleted: Boolean,
    var categoryName: String? = null,
    var categoryColor: String? = null,
) : Parcelable

fun Goal.asEntity() = GoalEntity(
    id = id,
    createdAt = createdAt,
    name = name,
    measure = measure,
    result = result,
    categoryId = categoryId,
    days = days,
    progress = progress,
    completedAt = completedAt,
    generate = generate,
    deleted = deleted,
)

fun Goal.asDocument() = GoalDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    measure = measure,
    result = result,
    categoryId = categoryId,
    days = days,
    progress = progress,
    completedAt = completedAt,
    generate = generate,
    deleted = deleted,
)

fun GoalEntity.asGoal() = Goal(
    id = id,
    createdAt = createdAt,
    name = name,
    measure = measure,
    result = result,
    categoryId = categoryId,
    days = days,
    progress = progress,
    completedAt = completedAt,
    generate = generate,
    deleted = deleted,
)

fun GoalEntityAndCategoryEntity.asGoal() = Goal(
    id = goalEntity.id,
    createdAt = goalEntity.createdAt,
    name = goalEntity.name,
    measure = goalEntity.measure,
    result = goalEntity.result,
    categoryId = goalEntity.categoryId,
    days = goalEntity.days,
    progress = goalEntity.progress,
    completedAt = goalEntity.completedAt,
    generate = goalEntity.generate,
    deleted = goalEntity.deleted,
    categoryName = categoryEntity.name,
    categoryColor = categoryEntity.color,
)

fun GoalDocument.asGoal() = Goal(
    id = id ?: "",
    createdAt = createdAt ?: 0L,
    name = name ?: "",
    measure = measure ?: "",
    result = result ?: "",
    categoryId = categoryId ?: "",
    days = days ?: 0,
    progress = progress ?: 0,
    completedAt = completedAt ?: 0L,
    generate = generate ?: false,
    deleted = deleted ?: false,
)
