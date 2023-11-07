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
    var completedAt: Long = 0,
    var startedAt: Long = 0,
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
    completedAt = completedAt,
    startedAt = startedAt,
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
    completedAt = completedAt,
    startedAt = startedAt,
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
    completedAt = completedAt,
    startedAt = startedAt,
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
    completedAt = taskEntity.completedAt,
    startedAt = taskEntity.startedAt,
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