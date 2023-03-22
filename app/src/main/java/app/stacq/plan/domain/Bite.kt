package app.stacq.plan.domain

import android.os.Parcelable
import androidx.annotation.Keep
import app.stacq.plan.data.source.local.bite.BiteEntity
import app.stacq.plan.data.source.remote.bite.BiteDocument
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class Bite(
    var id: String,
    var name: String,
    var taskId: String,
    var categoryId: String,
    var completed: Boolean = false,
    var completedAt: Long = 0,
) : Parcelable


fun Bite.asEntity() = BiteEntity(
    id = id,
    name = name,
    taskId = taskId,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
)

fun Bite.asDocument() = BiteDocument(
    id = id,
    name = name,
    taskId = taskId,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
)

fun BiteEntity.asBite() = Bite(
    id = id,
    name = name,
    taskId = taskId,
    categoryId = categoryId,
    completed = completed,
    completedAt = completedAt,
)