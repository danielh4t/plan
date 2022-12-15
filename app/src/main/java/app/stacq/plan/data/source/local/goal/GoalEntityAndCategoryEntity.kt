package app.stacq.plan.data.source.local.goal

import androidx.room.Embedded
import androidx.room.Relation
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.data.source.model.Goal

data class GoalEntityAndCategoryEntity(
    @Embedded val goalEntity: GoalEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity
)

fun GoalEntityAndCategoryEntity.asGoal() = Goal(
    id = goalEntity.id,
    createdAt = goalEntity.createdAt,
    name = goalEntity.name,
    achieved = goalEntity.achieved,
    achievedAt = goalEntity.achievedAt,
    achievedBy = goalEntity.achievedAt,
    categoryId = goalEntity.categoryId,
    categoryName = categoryEntity.name,
    categoryColor = categoryEntity.color
)