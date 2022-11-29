package app.stacq.plan.data.model

import app.stacq.plan.data.source.local.task.TaskEntity
import app.stacq.plan.data.source.remote.task.TaskDocument


fun Task.toTaskEntity() = TaskEntity(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
)

fun Task.toTaskDocument() = TaskDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
)