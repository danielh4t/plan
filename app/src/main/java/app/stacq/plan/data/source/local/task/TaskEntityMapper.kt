package app.stacq.plan.data.source.local.task


import app.stacq.plan.data.source.remote.task.TaskDocument


fun TaskEntity.toTaskDocument() = TaskDocument(
    id = id,
    createdAt = createdAt,
    name = name,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
    timerAlarm = timerAlarm,
    timerFinishAt = timerFinishAt,
)