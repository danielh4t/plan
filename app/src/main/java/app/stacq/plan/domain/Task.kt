package app.stacq.plan.domain

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.local.task.TaskEntityAndCategoryEntity
import app.stacq.plan.data.source.remote.task.TaskDocument
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Task(
    var id: String,
    var createdAt: Long,
    var name: String,
    var categoryId: String,
    var completed: Boolean = false,
    var completedAt: Long = 0,
    var timerFinishAt: Long = 0,
    var timerAlarm: Boolean = true,
    var priority: Int = 0,
    var categoryName: String?  = null,
    var categoryColor: String? = null,
    var goalId: String? = null,
    var archived: Boolean = false,
    var notes: String? = null,
) : Parcelable

fun Task.asTaskEntity() = TaskEntity(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
    priority = priority,
    goalId = goalId,
    archived = archived,
    notes = notes,
)

fun Task.asTaskDocument() = TaskDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
    priority = priority,
    goalId = goalId,
    archived = archived,
    notes = notes,
)

fun TaskEntity.asTask() = Task(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
    priority = priority,
    goalId = goalId,
    archived = archived,
    notes = notes,
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
    categoryColor = categoryEntity.color,
    goalId = taskEntity.goalId,
    archived = taskEntity.archived,
    notes = taskEntity.notes,
)