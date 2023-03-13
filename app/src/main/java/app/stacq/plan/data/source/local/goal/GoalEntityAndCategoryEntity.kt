package app.stacq.plan.data.source.local.goal

import androidx.room.Embedded
import androidx.room.Relation
import app.stacq.plan.data.source.local.category.CategoryEntity

data class GoalEntityAndCategoryEntity(
    @Embedded val goalEntity: GoalEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity
)