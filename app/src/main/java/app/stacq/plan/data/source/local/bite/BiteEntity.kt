package app.stacq.plan.data.source.local.bite

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import app.stacq.plan.util.TimeUtil
import java.util.UUID

@Keep
@Entity(tableName = "bite")
data class BiteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "created_at")
    val createdAt: Long = TimeUtil().nowInSeconds(),

    var name: String,

    @ColumnInfo(name = "task_id")
    var taskId: String,

    @ColumnInfo(name = "category_id")
    var categoryId: String,

    var completed: Boolean = false,

    @ColumnInfo(name = "completed_at")
    var completedAt: Long = 0,
)