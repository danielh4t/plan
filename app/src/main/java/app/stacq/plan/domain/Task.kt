package app.stacq.plan.domain

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.remote.task.TaskDocument
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.util.*


@Keep
@Parcelize
data class Task(
    var id: String = UUID.randomUUID().toString(),
    var createdAt: Long = Instant.now().epochSecond,
    var name: String,
    var categoryId: String,
    var completed: Boolean = false,
    var completedAt: Long = 0,
    var timerFinishAt: Long = 0,
    var timerAlarm: Boolean = true,
    var priority: Int = 0,
    var categoryName: String  = "",
    var categoryColor: String = "",
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
    priority = priority
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
    priority = priority
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
    categoryColor = "",
    priority = priority
)

fun TaskEntity.asTaskDocument() = TaskDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
    priority = priority
)


