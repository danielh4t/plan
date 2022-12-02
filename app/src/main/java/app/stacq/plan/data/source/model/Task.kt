package app.stacq.plan.data.source.model

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.remote.task.TaskDocument
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Task(
    var id: String,
    var createdAt: Long,
    var name: String,
    var completed: Boolean,
    var completedAt: Long,
    var timerFinishAt: Long,
    var timerAlarm: Boolean,
    var categoryId: String,
    var categoryName: String,
    var categoryColor: String,
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
    categoryName = "",
    categoryColor = ""
)



