package app.stacq.plan.domain

import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.remote.task.TaskDocument
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID


data class Task(
    var id: String = UUID.randomUUID().toString(),
    var createdAt: Long = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis,
    var name: String,
    var completedAt: Long? = null,
    var notes: String? = null,
)

fun Task.asTaskEntity() = TaskEntity(
    id = id,
    createdAt = createdAt,
    name = name,
    completedAt = completedAt,
    notes = notes,
)

fun Task.asTaskDocument() = TaskDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    completedAt = completedAt,
    notes = notes,
)

fun TaskEntity.asTask() = Task(
    id = id,
    createdAt = createdAt,
    name = name,
    completedAt = completedAt,
    notes = notes,
)

fun TaskDocument.asTask() = Task(
    id = id!!,
    createdAt = createdAt!!,
    name = name!!,
    completedAt = completedAt,
    notes = notes,
)
