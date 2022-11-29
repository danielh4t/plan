package app.stacq.plan.data.source.local.category


import app.stacq.plan.data.model.Category
import app.stacq.plan.data.source.remote.task.TaskDocument


fun CategoryEntity.toCategory() = Category(
    id = id,
    createdAt = createdAt,
    name = name,
    color = color,
    enabled = enabled

)