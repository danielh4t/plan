package app.stacq.plan.data.source.local.task

import androidx.room.Embedded
import androidx.room.Relation
import app.stacq.plan.data.source.local.category.CategoryEntity
import app.stacq.plan.domain.Task

data class TaskEntityAndCategoryEntity(
    @Embedded val taskEntity: TaskEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity
)

fun TaskEntityAndCategoryEntity.asTask() = Task(
    id = taskEntity.id,
    createdAt = taskEntity.createdAt,
    name = taskEntity.name,
    completed = taskEntity.completed,
    completedAt = taskEntity.completedAt,
    timerFinishAt = taskEntity.timerFinishAt,
    timerAlarm = taskEntity.timerAlarm,
    priority = taskEntity.priority,
    categoryId = taskEntity.categoryId,
    categoryName = categoryEntity.name,
    categoryColor = categoryEntity.color

)