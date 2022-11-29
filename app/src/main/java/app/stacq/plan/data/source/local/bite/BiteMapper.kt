package app.stacq.plan.data.source.local.bite

import app.stacq.plan.data.source.remote.bite.BiteDocument


fun BiteEntity.toBiteDocument() = BiteDocument(
    id = id,
    name = name,
    taskId = taskId,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt
)