package app.stacq.plan.data.source.local.task

import androidx.room.Embedded
import androidx.room.Relation
import app.stacq.plan.data.source.local.category.CategoryEntity

data class TaskEntityAndCategoryEntity(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity
)